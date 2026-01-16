<!-- This file was created by Claude Sonnet 4.5 -->

# Frontend Development Setup

Complete guide for setting up and running the Marketplace micro-frontend application locally.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Development Workflow](#development-workflow)
- [Environment Configuration](#environment-configuration)
- [Docker Setup](#docker-setup)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before starting, ensure you have the following installed:

- **Node.js** 18+ and npm (Download from [nodejs.org](https://nodejs.org/))
- **Git** (for version control)
- **Docker** and Docker Compose (optional, for containerized development)
- A code editor like **VS Code**

Verify installations:

```bash
node --version  # Should be 18.x or higher
npm --version   # Should be 9.x or higher
git --version
docker --version  # Optional
```

---

## Project Structure

```
/frontend
├── /root-config              # Single-SPA orchestrator (Port 9000)
├── /mfe-angular-cards        # Angular MFE - Card marketplace (Port 4201)
├── /mfe-react-users          # React MFE - User management (Port 4202)
├── /shared-utils             # Shared utilities and design tokens
└── docker-compose.yml        # Docker configuration for all services
```

---

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd P3-Group2/frontend
```

### 2. Install Dependencies for All MFEs

Run the following commands to install dependencies for each micro-frontend:

```bash
# Install root-config dependencies
cd root-config
npm install
cd ..

# Install Angular Cards MFE dependencies
cd mfe-angular-cards
npm install
cd ..

# Install React Users MFE dependencies
cd mfe-react-users
npm install
cd ..

# Install shared utilities
cd shared-utils
npm install
cd ..
```

### 3. Set Up Environment Variables

Each MFE has an `.env.example` file. Copy these to create your local `.env` files:

```bash
# Angular Cards MFE
cp mfe-angular-cards/.env.example mfe-angular-cards/.env

# React Users MFE
cp mfe-react-users/.env.example mfe-react-users/.env

# Root Config
cp root-config/.env.example root-config/.env
```

Edit the `.env` files as needed for your local environment (see [Environment Configuration](#environment-configuration)).

---

## Running the Application

### Option 1: Run All Services Manually

Open **three separate terminal windows** and run each service:

**Terminal 1 - Root Config (Orchestrator):**
```bash
cd frontend/root-config
npm start
# Runs on http://localhost:9000
```

**Terminal 2 - Angular Cards MFE:**
```bash
cd frontend/mfe-angular-cards
npm start
# Runs on http://localhost:4201
```

**Terminal 3 - React Users MFE:**
```bash
cd frontend/mfe-react-users
npm start
# Runs on http://localhost:4202
```

**Access the application:**
- Main application: [http://localhost:9000](http://localhost:9000)
- Angular Cards (standalone): [http://localhost:4201](http://localhost:4201)
- React Users (standalone): [http://localhost:4202](http://localhost:4202)

### Option 2: Run with Docker Compose

Run all services with a single command:

```bash
cd frontend
docker-compose up
```

This will start all micro-frontends and the root-config. Access the main application at [http://localhost:9000](http://localhost:9000).

To stop all services:

```bash
docker-compose down
```

---

## Development Workflow

### Hot Module Replacement (HMR)

All services are configured with **Hot Module Replacement** enabled. Changes to your code will automatically reload in the browser without losing application state.

### Code Quality

Each MFE has linting and formatting configured:

```bash
# Run linting
npm run lint

# Auto-fix linting issues
npm run lint:fix  # (React) or npm run lint (Angular)

# Format code
npm run format

# Check formatting
npm run format:check
```

### Running Tests

```bash
# Root Config
cd root-config
npm test

# Angular Cards MFE
cd mfe-angular-cards
npm test

# React Users MFE
cd mfe-react-users
npm test
npm run test:coverage  # With coverage report
```

### Building for Production

```bash
# Build all services
cd root-config && npm run build
cd ../mfe-angular-cards && npm run build:prod
cd ../mfe-react-users && npm run build
```

---

## Environment Configuration

### Root Config Environment Variables

Located in `root-config/.env`:

```env
NODE_ENV=development
MFE_ANGULAR_CARDS_URL=http://localhost:4201
MFE_REACT_USERS_URL=http://localhost:4202
```

### Angular Cards MFE Environment Variables

Located in `mfe-angular-cards/.env`:

```env
NODE_ENV=development
API_URL=http://localhost:8080
SINGLE_SPA_ACTIVE=true
ROOT_CONFIG_URL=http://localhost:9000
```

### React Users MFE Environment Variables

Located in `mfe-react-users/.env`:

```env
NODE_ENV=development
API_URL=http://localhost:8081
SINGLE_SPA_ACTIVE=true
ROOT_CONFIG_URL=http://localhost:9000
JWT_SECRET=your-jwt-secret-key-here
```

---

## Docker Setup

### Building Docker Images

Build images for all services:

```bash
cd frontend

# Build all services
docker-compose build

# Build a specific service
docker-compose build root-config
docker-compose build mfe-angular-cards
docker-compose build mfe-react-users
```

### Running Services

```bash
# Start all services in the background
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for a specific service
docker-compose logs -f root-config

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Accessing Services

When running with Docker:

- **Root Config**: [http://localhost:9000](http://localhost:9000)
- **Angular Cards MFE**: [http://localhost:4201](http://localhost:4201)
- **React Users MFE**: [http://localhost:4202](http://localhost:4202)

---

## Troubleshooting

### Port Already in Use

If you get a "port already in use" error:

```bash
# Find the process using the port (e.g., 9000)
lsof -ti:9000

# Kill the process
kill -9 <PID>
```

Or change the port in the respective `package.json` file.

### Module Not Found Errors

Try clearing node_modules and reinstalling:

```bash
cd <service-directory>
rm -rf node_modules package-lock.json
npm install
```

### CORS Errors

Ensure all services are running and the environment variables point to the correct URLs. Check `webpack.config.js` for CORS headers configuration.

### MFE Not Loading in Root Config

1. Check that the MFE is running on the correct port
2. Verify the import map in `root-config/src/index.ejs` has the correct URLs
3. Check browser console for specific errors
4. Ensure CORS headers are properly configured

### Docker Issues

```bash
# Rebuild containers from scratch
docker-compose down -v
docker-compose build --no-cache
docker-compose up

# Check container logs
docker-compose logs <service-name>
```

### TypeScript Errors

```bash
# Rebuild TypeScript definitions
npm run build:types
```

---

## Additional Resources

- [Single-SPA Documentation](https://single-spa.js.org/)
- [Angular Documentation](https://angular.io/docs)
- [React Documentation](https://react.dev/)
- [Webpack Documentation](https://webpack.js.org/)

---

## Support

For questions or issues, please:

1. Check the [Troubleshooting](#troubleshooting) section
2. Review existing documentation in each MFE's README
3. Contact the development team

---

**Happy coding! 🚀**
