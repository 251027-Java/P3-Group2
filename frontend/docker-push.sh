# This file was created by Claude Sonnet 4.5

# Docker Registry Push Script for Frontend MFEs
# Usage: ./docker-push.sh [environment]
# Example: ./docker-push.sh production

set -e

# Configuration
DOCKER_REGISTRY="${DOCKER_REGISTRY:-docker.io}"
DOCKER_USERNAME="${DOCKER_USERNAME:-marketplace}"
BUILD_NUMBER="${BUILD_NUMBER:-latest}"
ENVIRONMENT="${1:-development}"

# Color output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Docker Registry Push Script ===${NC}"
echo "Registry: ${DOCKER_REGISTRY}"
echo "Username: ${DOCKER_USERNAME}"
echo "Build Number: ${BUILD_NUMBER}"
echo "Environment: ${ENVIRONMENT}"
echo ""

# Function to build and push image
build_and_push() {
    local service_name=$1
    local directory=$2
    local port=$3
    
    echo -e "${YELLOW}Building ${service_name}...${NC}"
    
    # Build image
    docker build \
        -t ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:${BUILD_NUMBER} \
        -t ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:${ENVIRONMENT} \
        -t ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:latest \
        ./frontend/${directory}
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build successful for ${service_name}${NC}"
    else
        echo -e "${RED}✗ Build failed for ${service_name}${NC}"
        exit 1
    fi
    
    # Push images
    echo -e "${YELLOW}Pushing ${service_name}...${NC}"
    
    docker push ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:${BUILD_NUMBER}
    docker push ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:${ENVIRONMENT}
    docker push ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${service_name}:latest
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Push successful for ${service_name}${NC}"
    else
        echo -e "${RED}✗ Push failed for ${service_name}${NC}"
        exit 1
    fi
    
    echo ""
}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# Check if logged in to Docker registry
if ! docker info | grep -q "Username"; then
    echo -e "${YELLOW}Warning: Not logged in to Docker registry${NC}"
    echo "Please run: docker login ${DOCKER_REGISTRY}"
    exit 1
fi

# Build and push all services
build_and_push "root-config" "root-config" "9000"
build_and_push "mfe-angular-cards" "mfe-angular-cards" "4201"
build_and_push "mfe-react-users" "mfe-react-users" "4202"

echo -e "${GREEN}=== All images built and pushed successfully! ===${NC}"
echo ""
echo "Images pushed:"
echo "  - ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/root-config:${BUILD_NUMBER}"
echo "  - ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/mfe-angular-cards:${BUILD_NUMBER}"
echo "  - ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/mfe-react-users:${BUILD_NUMBER}"
