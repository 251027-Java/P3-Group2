/**
 * This file was created by Claude Sonnet 4.5
 *
 * Environment Configuration
 * Manages environment-specific settings for dev, staging, and production
 * Generated with assistance from GitHub Copilot
 */

export interface Environment {
  production: boolean;
  apiUrl: string;
  apiGatewayUrl: string;
  environment: "development" | "staging" | "production";
  enableDevTools: boolean;
  logLevel: "debug" | "info" | "warn" | "error";
}

// Detect environment from hostname or environment variable
const getEnvironment = (): "development" | "staging" | "production" => {
  const hostname = window.location.hostname;

  if (hostname === "localhost" || hostname === "127.0.0.1") {
    return "development";
  } else if (hostname.includes("staging") || hostname.includes("stg")) {
    return "staging";
  } else {
    return "production";
  }
};

const env = getEnvironment();

// Development environment
const developmentConfig: Environment = {
  production: false,
  apiUrl: "http://localhost:8080/api/v1",
  apiGatewayUrl: "http://localhost:8080",
  environment: "development",
  enableDevTools: true,
  logLevel: "debug",
};

// Staging environment
const stagingConfig: Environment = {
  production: false,
  apiUrl: "https://staging-api.marketplace.com/api/v1",
  apiGatewayUrl: "https://staging-api.marketplace.com",
  environment: "staging",
  enableDevTools: true,
  logLevel: "info",
};

// Production environment
const productionConfig: Environment = {
  production: true,
  apiUrl: "https://api.marketplace.com/api/v1",
  apiGatewayUrl: "https://api.marketplace.com",
  environment: "production",
  enableDevTools: false,
  logLevel: "error",
};

// Export the appropriate configuration
export const environment: Environment = (() => {
  switch (env) {
    case "development":
      return developmentConfig;
    case "staging":
      return stagingConfig;
    case "production":
      return productionConfig;
    default:
      return developmentConfig;
  }
})();

// Export individual configs for testing
export { developmentConfig, stagingConfig, productionConfig };
