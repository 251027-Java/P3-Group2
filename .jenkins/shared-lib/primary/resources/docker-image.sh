#!/usr/bin/bash

# Generated with assistance from Copilot
# Reviewed and modified by JB Ladera

# https://packagemain.tech/p/optimizing-multi-platform-docker

clean-platform() {
    local input="$1"
    echo "${input//\//-}"
}

get-external-hosts() {
    local exclude="$1"
    awk -v ex="$exclude" '/^Host / && $2 != ex {print $2}' "$ssh_config_file"
}

get-random-string() {
    tr -dc 'a-z' </dev/urandom | head -c 32
}

curPlatform=$(docker version --format '{{.Server.Os}}/{{.Server.Arch}}')
curSafePlatform=$(docker version --format '{{.Server.Os}}-{{.Server.Arch}}')
builderName=$(get-random-string)
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

# ensure platforms only consists of ones that are supported
supportedPlatforms=("$curSafePlatform")

if [ -f "$ssh_config_file" ]; then
    supportedPlatforms+=($(get-external-hosts $curSafePlatform))
fi

platforms=()

for p in "${givenPlatforms[@]}"; do
    cleaned=$(clean-platform "$p")

    for a in "${supportedPlatforms[@]}"; do
        if [[ "$cleaned" == "$a" ]]; then
            platforms+=("$p")
            break
        fi
    done
done

echo "finalized platforms: ${platforms[@]}"

# create context - for each (except host if not TLS)
declare -A contextMap

for platform in "${platforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(clean-platform "$platform")
        contextName=$(get-random-string)
        contextMap["$safePlatform"]="$contextName"

        docker context create $contextName --docker "host=ssh://$safePlatform"
    fi
done

mainBuilderContext="default"

# TLS needs its own context
if [[ "$DOCKER_TLS_VERIFY" == "1" ]]; then
    contextName=$(get-random-string)
    contextMap["$curSafePlatform"]="$contextName"
    mainBuilderContext="$contextName"

    # https://serverfault.com/a/1152599
    docker context create $contextName --docker "host=$DOCKER_HOST,ca=$DOCKER_CERT_PATH/ca.pem,cert=$DOCKER_CERT_PATH/cert.pem,key=$DOCKER_CERT_PATH/key.pem"
fi

# create builder
docker builder create --name $builderName --platform $curPlatform $mainBuilderContext

# append to builder - for each (except host)
for platform in "${platforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(clean-platform "$platform")
        contextName="${contextMap[$safePlatform]}"

        docker builder create --name $builderName --platform $platform --append $contextName
    fi
done

# build cache - for each
for platform in "${platforms[@]}"; do
    safePlatform=$(clean-platform "$platform")

    docker build --builder $builderName -t $builderName:$safePlatform --platform $platform --cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries --cache-to=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries --load $dockerLocation
done

# final push
platformString=$(IFS=,; echo "${platforms[*]}")
cacheFromFlags=()

for platform in "${platforms[@]}"; do
    safePlatform=$(clean-platform "$platform")
    cacheFromFlags+=("--cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries")
done

tagFlags=("-t" "$dockerRepo:$tagSeries-$tagMeta")
if [[ "$latest" == "true" ]]; then
    tagFlags+=("-t" "$dockerRepo:$tagSeries-latest")
fi

docker build --builder $builderName "${tagFlags[@]}" --platform $platformString "${cacheFromFlags[@]}" --push $dockerLocation

# clean up created contexts - for each
for key in "${!contextMap[@]}"; do
    contextName="${contextMap[$key]}"

    docker --context $contextName container ls -a --format '{{.ID}} {{.Names}}' | grep "$builderName" | awk '{print $1}' | xargs -r docker --context $contextName rm -f -v
    docker --context $contextName volume ls --format '{{.Name}}' | grep "$builderName" | xargs -r docker --context $contextName volume rm -f
    docker context rm $contextName
done

# clean up local
docker builder rm $builderName
docker image ls --format '{{.Repository}}:{{.Tag}} {{.ID}}' | grep "$builderName" | awk '{print $2}' | xargs -r docker rmi -f
