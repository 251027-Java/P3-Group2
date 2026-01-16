# This file was created by Claude Sonnet 4.5

# Multi-stage Dockerfile for Angular Cards MFE
# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Install Angular CLI globally
RUN npm install -g @angular/cli@17

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci && npm cache clean --force

# Copy source code
COPY . .

# Build the application for production
RUN npm run build:prod

# Stage 2: Production
FROM nginx:alpine

# Copy built assets from builder stage
COPY --from=builder /app/dist/mfe-angular-cards /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 4201

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:4201/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
