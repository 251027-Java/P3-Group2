# This file was created by Claude Sonnet 4.5

# Docker Registry Configuration

This document describes the Docker image build and push process for frontend micro-frontends.

## Docker Registry Setup

### Configuration

The project uses the following Docker registry configuration:

- **Registry**: `docker.io` (Docker Hub)
- **Organization**: `marketplace`
- **Credentials ID** (Jenkins): `docker-hub-cred`

### Image Naming Convention

Images follow this naming pattern:
```
${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${SERVICE_NAME}:${TAG}
```

Example:
```
docker.io/marketplace/root-config:latest
docker.io/marketplace/mfe-angular-cards:v1.0.0
docker.io/marketplace/mfe-react-users:production
```

## Services

### Root Config
- **Service Name**: `root-config`
- **Port**: 9000
- **Description**: Single-SPA orchestrator

### Angular Cards MFE
- **Service Name**: `mfe-angular-cards`
- **Port**: 4201
- **Description**: Card marketplace micro-frontend

### React Users MFE
- **Service Name**: `mfe-react-users`
- **Port**: 4202
- **Description**: User management micro-frontend

## Tagging Strategy

Each image is tagged with three tags:

1. **Build Number**: `${BUILD_NUMBER}` - Unique identifier for each build
2. **Environment**: `development`, `staging`, or `production`
3. **Latest**: `latest` - Always points to the most recent build

Example:
```bash
marketplace/root-config:123      # Build number
marketplace/root-config:production
marketplace/root-config:latest
```

## Manual Push

### Prerequisites

1. Install Docker
2. Login to Docker registry:
```bash
docker login docker.io
```

### Using the Push Script

```bash
# Push with default settings (development)
./frontend/docker-push.sh

# Push for specific environment
./frontend/docker-push.sh production
./frontend/docker-push.sh staging
```

### Environment Variables

Configure these before running the script:

```bash
export DOCKER_REGISTRY="docker.io"
export DOCKER_USERNAME="marketplace"
export BUILD_NUMBER="v1.0.0"
```

## Jenkins Pipeline

The Jenkins pipeline automatically:

1. Builds all MFE Docker images
2. Runs tests and quality checks
3. Tags images appropriately
4. Pushes to Docker registry (main branch only)
5. Cleans up local images

### Pipeline Configuration

Located in `frontend/Jenkinsfile`:

- Runs on `main` branch merges
- Requires `docker-hub-cred` credentials
- Publishes coverage reports
- Archives test results

## Pulling Images

### From Docker Hub

```bash
# Pull latest
docker pull marketplace/root-config:latest

# Pull specific version
docker pull marketplace/mfe-angular-cards:v1.0.0

# Pull environment-specific
docker pull marketplace/mfe-react-users:production
```

### Using Docker Compose

Update `docker-compose.yml` to use registry images:

```yaml
services:
  root-config:
    image: marketplace/root-config:latest
    # Remove 'build' section
```

## Troubleshooting

### Authentication Issues

If you get authentication errors:

```bash
# Re-login to Docker Hub
docker login docker.io

# Verify credentials
docker info | grep Username
```

### Build Failures

Check build logs:
```bash
docker build -t test ./frontend/root-config
```

### Push Failures

Verify network connectivity and registry access:
```bash
# Test registry connectivity
docker pull hello-world

# Check image exists locally
docker images | grep marketplace
```

### Image Size Issues

Optimize Dockerfiles:
- Use multi-stage builds (already implemented)
- Minimize layers
- Use `.dockerignore` files
- Remove development dependencies in production builds

## Security

### Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for sensitive data
3. **Scan images** for vulnerabilities:
```bash
docker scan marketplace/root-config:latest
```
4. **Keep base images updated**
5. **Use minimal base images** (alpine)

### Secrets Management

In Jenkins:
- Store Docker credentials in Jenkins Credentials Manager
- Use credential ID: `docker-hub-cred`
- Never expose credentials in logs

## Monitoring

### Image Registry

Monitor your Docker Hub repository:
- Check pull statistics
- Review image sizes
- Track version history
- Monitor vulnerability scans

### CI/CD Pipeline

Monitor Jenkins pipeline:
- Build success rate
- Build duration
- Test coverage
- Docker push success rate

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)
- [Jenkins Docker Plugin](https://plugins.jenkins.io/docker-plugin/)
- [Multi-stage Build Best Practices](https://docs.docker.com/develop/dev-best-practices/)
