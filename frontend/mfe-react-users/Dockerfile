# This file was created by Claude Sonnet 4.5

# Multi-stage Dockerfile for React Users MFE
# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY ./mfe-react-users/package*.json ./

# Copy shared-utils (referenced as file:../shared-utils in package.json)
COPY ./shared-utils ../shared-utils

# Install dependencies
RUN npm ci

# Copy source code
COPY ./mfe-react-users .

# Build the application for production
RUN npm run build

# Stage 2: Production
FROM nginx:alpine

# Copy built assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY ./mfe-react-users/nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 4202

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:4202/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
