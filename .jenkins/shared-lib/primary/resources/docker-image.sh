#!/usr/bin/bash

# https://packagemain.tech/p/optimizing-multi-platform-docker
curPlatform=$(docker version --format '{{.Server.Os}}/{{.Server.Arch}}')
builderName=$(tr -dc 'a-z' </dev/urandom | head -c 32)
ssh_config_file="$HOME/.ssh/config"

givenPlatforms=("$curPlatform")
dockerLocation="."
dockerRepo=""
tagSeries=""
tagMeta=""
latest="false"

# parse args
while [[ $# -gt 0 ]]; do
    case "$1" in
        --platforms)
            IFS=',' read -r -a givenPlatforms <<< "$2"
            shift 2
            ;;
        --location)
            dockerLocation="$2"
            shift 2
            ;;
        --repo)
            dockerRepo="$2"
            shift 2
            ;;
        --series)
            tagSeries="$2"
            shift 2
            ;;
        --meta)
            tagMeta="$2"
            shift 2
            ;;
        --latest)
            latest="true"
            shift
            ;;
        *)
            echo "Unknown argument: $1"
            exit 1
            ;;
    esac
done

if [ ! -e "$dockerLocation" ]; then
    echo "directory not found: $dockerLocation"
    exit 1
fi

cleanPlatform() {
    local input="$1"
    echo "${input//\//-}"
}

get-external-hosts() {
    local exclude=$(cleanPlatform "$curPlatform")
    awk -v ex="$exclude" '/^Host / && $2 != ex {print $2}' "$ssh_config_file"
}

# ensure platforms only consists of ones that are supported
supportedPlatforms=($(cleanPlatform "$curPlatform"))

if [ -f "$ssh_config_file" ]; then
    supportedPlatforms+=($(get-external-hosts))
fi

platforms=()

for p in "${givenPlatforms[@]}"; do
    cleaned=$(cleanPlatform "$p")
    for a in "${supportedPlatforms[@]}"; do
        if [[ "$cleaned" == "$a" ]]; then
            platforms+=("$p")
            break
        fi
    done
done

echo "finalized platforms: ${platforms[@]}"

# create context - for each (except host)
for platform in "${platforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(cleanPlatform "$platform")
        docker context create $safePlatform --docker "host=ssh://$safePlatform"
    fi
done

# create builder
docker builder create --name $builderName --platform $curPlatform default

# append to builder - for each (except host)
for platform in "${platforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(cleanPlatform "$platform")
        docker builder create --name $builderName --platform $platform --append $safePlatform
    fi
done

# build cache - for each
for platform in "${platforms[@]}"; do
    safePlatform=$(cleanPlatform "$platform")
    docker build --builder $builderName -t $builderName:$safePlatform --platform $platform --cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries --cache-to=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries --load $dockerLocation
done

# final push
platformString=$(IFS=,; echo "${platforms[*]}")
cacheFromFlags=()

for platform in "${platforms[@]}"; do
    safePlatform=$(cleanPlatform "$platform")
    cacheFromFlags+=("--cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries")
done

tagFlags=("-t" "$dockerRepo:$tagSeries-$tagMeta")
if [[ "$latest" == "true" ]]; then
    tagFlags+=("-t" "$dockerRepo:$tagSeries-latest")
fi

docker build --builder $builderName "${tagFlags[@]}" --platform $platformString "${cacheFromFlags[@]}" --push $dockerLocation

# clean up - for each (except host)
for platform in "${platforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(cleanPlatform "$platform")
        docker --context $safePlatform container ls -a --format '{{.ID}} {{.Names}}' | grep "$builderName" | awk '{print $1}' | xargs -r docker --context $safePlatform rm -f -v
        docker --context $safePlatform volume ls --format '{{.Name}}' | grep "$builderName" | xargs -r docker --context $safePlatform volume rm -f
        docker context rm $safePlatform
    fi
done

# clean up local
docker builder rm $builderName
docker image ls --format '{{.Repository}}:{{.Tag}} {{.ID}}' | grep "$builderName" | awk '{print $2}' | xargs -r docker rmi -f
