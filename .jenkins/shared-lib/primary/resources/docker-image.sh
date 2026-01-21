#!/usr/bin/bash

# Generated with assistance from Copilot
# Reviewed and modified by JB Ladera

# https://packagemain.tech/p/optimizing-multi-platform-docker
# https://depot.dev/blog/how-to-use-cache-mount-to-speed-up-docker-builds

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
dockerfilePathArg=""
dockerContext="."
dockerRepo=""
tagSeries=""
branch=""
sha=""
latest="false"

failed=0

# parse args
while [[ $# -gt 0 ]]; do
    case "$1" in
        --platforms)
            IFS=',' read -r -a givenPlatforms <<< "$2"
            shift 2
            ;;
        --context)
            dockerContext="$2"
            shift 2
            ;;
        --dockerfile)
            dockerfilePathArg="$2"
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
        --branch)
            branch="$2"
            shift 2
            ;;
        --sha)
            sha="$2"
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

dockerfilePath="$dockerContext/Dockerfile"

if [[ -z "$dockerfilePathArg" ]]; then
    dockerfilePath="$dockerfilePathArg"
fi

if [ ! -e "$dockerContext" ]; then
    echo "directory not found: $dockerContext"
    exit 1
fi

# display steps that are being executed in the bash script
set -x
PS4='+ ${LINENO}: '

# ensure platforms only consists of ones that are supported
supportedPlatforms=("$curSafePlatform")

if [ -f "$ssh_config_file" ]; then
    supportedPlatforms+=($(get-external-hosts $curSafePlatform))
fi

matchedPlatforms=()

for given in "${givenPlatforms[@]}"; do
    cleaned=$(clean-platform "$given")

    for supported in "${supportedPlatforms[@]}"; do
        if [[ "$cleaned" == "$supported" ]]; then
            matchedPlatforms+=("$given")
            break
        fi
    done
done

echo "matched platforms: ${matchedPlatforms[@]}"

# create context - for each (except host if not TLS)
declare -A contextMap

for platform in "${matchedPlatforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(clean-platform "$platform")
        contextName=$(get-random-string)
        contextMap["$safePlatform"]="$contextName"

        # this does NOT produce an error if the ssh connection cannot be established
        # error would be shown in the `docker builder ls`
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
platforms=("$curPlatform")

# append to builder - for each (except host)
for platform in "${matchedPlatforms[@]}"; do
    if [[ "$platform" != "$curPlatform" ]]; then
        safePlatform=$(clean-platform "$platform")
        contextName="${contextMap[$safePlatform]}"

        # this could fail due to not being able to connect to context.
        # we don't want to break or exit earlier because we should build what we can. still indicate failure at the end
        if docker builder create --name $builderName --platform $platform --append $contextName; then
            platforms+=("$platform")
        else
            failed=1
        fi
    fi
done

echo "finalized platforms: ${platforms[@]}"

build-and-push() {
    # build cache - for each
    for platform in "${platforms[@]}"; do
        safePlatform=$(clean-platform "$platform")

        # this could fail due to failed Dockerfile
        if ! docker build --builder $builderName -t $builderName:$safePlatform --platform $platform \
            --cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries \
            --cache-to=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries,mode=max \
            --load -f "$dockerfilePath" "$dockerContext"; then
            return 1
        fi
    done

    # final push prep
    local platformString=$(IFS=,; echo "${platforms[*]}")
    local cacheFromFlags=()

    for platform in "${platforms[@]}"; do
        safePlatform=$(clean-platform "$platform")
        cacheFromFlags+=("--cache-from=type=registry,ref=$dockerRepo:buildcache-$safePlatform-$tagSeries")
    done

    local tagFlags=("-t" "$dockerRepo:$tagSeries-$branch-$sha")
    if [[ "$latest" == "true" ]]; then
        tagFlags+=("-t" "$dockerRepo:$tagSeries-latest")
    fi

    # this could fail due to failed Dockerfile
    if ! docker build --builder $builderName "${tagFlags[@]}" \
        --platform $platformString "${cacheFromFlags[@]}" \
        --push -f "$dockerfilePath" "$dockerContext"; then
        return 1
    fi

    return 0
}

build-and-push || failed=1

# clean up created contexts - for each
for key in "${!contextMap[@]}"; do
    contextName="${contextMap[$key]}"

    # these docker --context commands can fail. just continue upon on failure to ensure clean up
    docker --context $contextName container ls -a --format '{{.ID}} {{.Names}}' \
        | grep "$builderName" | awk '{print $1}' \
        | xargs -r docker --context $contextName rm -f -v
    docker --context $contextName volume ls --format '{{.Name}}' \
        | grep "$builderName" \
        | xargs -r docker --context $contextName volume rm -f
    docker context rm $contextName
done

# clean up local
docker builder rm $builderName
docker image ls --format '{{.Repository}}:{{.Tag}} {{.ID}}' \
    | grep "$builderName" \
    | awk '{print $2}' \
    | xargs -r docker rmi -f

[[ $failed -ne 0 ]] && exit 1

exit 0
