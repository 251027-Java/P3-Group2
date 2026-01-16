<!-- This file was created by Claude Sonnet 4.5 -->

# Marketplace Root Config

The root configuration for the Marketplace micro-frontend application using Single-SPA.

## Overview

This is the orchestrator application that manages all micro-frontends (MFEs) in the Marketplace platform. It handles:

- Application routing and navigation
- MFE lifecycle management
- Layout structure (header, footer, main content)
- Global error handling
- Health monitoring
- Environment configuration

## Architecture

### Micro-Frontends

- **@marketplace/mfe-angular-cards** - Card marketplace (Angular)
- **@marketplace/mfe-react-users** - User management (React)
- **@marketplace/navbar** - Global navigation
- **@marketplace/footer** - Global footer
- **@marketplace/home** - Home page
- **@marketplace/not-found** - 404 page

### Routes

- `/` - Home page
- `/cards/*` - Card marketplace (Angular MFE)
- `/auth/*` - Authentication (React MFE)
- `/profile/*` - User profile (React MFE)
- `/settings/*` - User settings (React MFE)
- `/404` - Not found page

## Local Development

### Prerequisites

- Node.js 18+ and npm
- All micro-frontend applications running

### Installation

```bash
npm install
```

### Running Locally

```bash
npm start
```

The root-config will be available at `http://localhost:9000`.

### Development Ports

- Root Config: `9000`
- Angular Cards MFE: `4200`
- React Users MFE: `8080`
- Navbar: `8081`
- Footer: `8082`
- Home: `8083`
- Not Found: `8084`
- Shared Utils: `9001`

### Testing

```bash
# Run tests
npm test

# Run linting
npm run lint

# Format code
npm run format
```

## Environment Configuration

The application supports three environments:

- **Development** - `localhost` (default)
- **Staging** - Hostnames containing "staging" or "stg"
- **Production** - All other hostnames

Configuration is automatically selected based on the hostname. See [environment.ts](src/config/environment.ts) for details.

## Features

### Error Boundary

Global error handling that catches:

- Uncaught JavaScript errors
- Unhandled promise rejections
- MFE loading failures
- Single-SPA routing errors

Errors are logged and displayed in development mode.

### Health Checks

Monitors the health of backend services:

- API Gateway
- Card Service
- User Service

Health checks run every 30 seconds in development/staging.

### Import Maps

Import maps define where each MFE is loaded from:

- **Local Development**: `index.ejs` with localhost URLs
- **Production**: `importmap.json` with deployed URLs

### Debug Tools

In development mode, access debug utilities via the browser console:

```javascript
// Access environment config
window.__marketplace__.environment;

// View all errors
window.__marketplace__.errorBoundary.getErrors();

// Check service health
window.__marketplace__.healthCheckService.checkAllServices();

// View registered applications
window.__marketplace__.applications;
```

## Building for Production

```bash
npm run build
```

This creates optimized bundles in the `dist/` directory.

## Import Map Configuration

### Development

Import maps are automatically configured in `index.ejs` for local development.

### Production

Update `importmap.json` with production URLs and add this line to `index.ejs`:

```html
<script type="injector-importmap" src="/importmap.json"></script>
```

## Deployment

### Docker

A Dockerfile is provided for containerized deployment:

```bash
# Build image
docker build -t marketplace-root-config .

# Run container
docker run -p 9000:80 marketplace-root-config
```

### CI/CD

The application integrates with Jenkins pipeline for automated:

- Building
- Testing
- Docker image creation
- Deployment

See `Jenkinsfile-Frontend` in the project root.

## Project Structure

```
root-config/
├── src/
│   ├── components/
│   │   └── error-boundary.ts      # Global error handling
│   ├── config/
│   │   └── environment.ts         # Environment configuration
│   ├── services/
│   │   └── health-check.ts        # Health monitoring
│   ├── index.ejs                  # HTML template
│   ├── microfrontend-layout.html  # Layout definition
│   ├── importmap.json             # Production import map
│   └── marketplace-root-config.ts # Main entry point
├── package.json
├── tsconfig.json
├── webpack.config.js
└── README.md
```

## Adding New Micro-Frontends

1. Update `microfrontend-layout.html` with new routes
2. Add import map entries in `index.ejs` (dev) and `importmap.json` (prod)
3. Register the application port in this README

## Troubleshooting

### MFE Not Loading

1. Check that the MFE is running on the correct port
2. Verify import map URLs in browser DevTools
3. Check browser console for loading errors
4. Use `window.__marketplace__.errorBoundary.getErrors()` to view errors

### CORS Issues

Ensure all MFEs have CORS configured to allow requests from the root-config origin.

### Import Map Override

Use the import-map-overrides tool (bottom-right of page in dev mode) to:

- Override MFE URLs
- Point to local development versions
- Debug production issues

## Contributing

1. Follow the existing code style
2. Add tests for new features
3. Update documentation
4. Run `npm run format` before committing

## AI Attribution

This project was developed with assistance from GitHub Copilot. All AI-generated code has been reviewed and modified by the development team.

## License

Copyright © 2026 Marketplace Team
