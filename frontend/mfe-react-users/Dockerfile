# This file was created by Claude Sonnet 4.5

# Multi-stage Dockerfile for React Users MFE
# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY ./mfe-react-users/package*.json ./


# Install dependencies
RUN npm ci

# Copy source code
COPY ./mfe-react-users .

# Build the application for production
RUN npm run build

# syntax=docker/dockerfile:1

# Comments are provided throughout this file to help you get started.
# If you need more help, visit the Dockerfile reference guide at
# https://docs.docker.com/go/dockerfile-reference/

# Want to help us make this template better? Share your feedback here: https://forms.gle/ybq9Krt8jtBL3iCk7

################################################################################
# Use node image for base image for all stages.
FROM node:lts-alpine as base

# Set working directory for all build stages.
WORKDIR /usr/src/app

# Copy shared-utils (referenced as file:../shared-utils in package.json)
COPY ./shared-utils ../shared-utils

################################################################################
# Create a stage for installing production dependecies.
FROM base as deps

# Download dependencies as a separate step to take advantage of Docker's caching.
# Leverage a cache mount to /root/.npm to speed up subsequent builds.
# Leverage bind mounts to package.json and package-lock.json to avoid having to copy them
# into this layer.
RUN --mount=type=bind,source=mfe-react-users/package.json,target=package.json \
    --mount=type=bind,source=mfe-react-users/package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci --omit=dev

################################################################################
# Create a stage for building the application.
FROM deps as build

# Download additional development dependencies before building, as some projects require
# "devDependencies" to be installed to build. If you don't need this, remove this step.
RUN --mount=type=bind,source=mfe-react-users/package.json,target=package.json \
    --mount=type=bind,source=mfe-react-users/package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci

# Copy the rest of the source files into the image.
COPY ./mfe-react-users/src src
COPY ./mfe-react-users/*.json .
COPY ./mfe-react-users/*.js .
COPY ./mfe-react-users/.env.production .

# Run the build script.
RUN npm run build

# Stage 2: Production
FROM nginx:alpine AS final

COPY --from=build /usr/src/app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY ./mfe-react-users/nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 4202

# Start nginx
COPY --chmod=755 ./mfe-react-users/start-nginx.sh /usr/local/bin/

ENTRYPOINT [ "start-nginx.sh" ]