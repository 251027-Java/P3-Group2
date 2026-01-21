/**
 * This file was created by Claude Sonnet 4.5
 *
 * Marketplace Root Config
 * Main orchestration file for Single-SPA micro-frontends
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

import "regenerator-runtime/runtime";
import { registerApplication, start } from "single-spa";
import {
  constructApplications,
  constructRoutes,
  constructLayoutEngine,
} from "single-spa-layout";
import microfrontendLayout from "./microfrontend-layout.html";
import { environment } from "./config/environment";
import { errorBoundary } from "./components/error-boundary";
import { healthCheckService } from "./services/health-check";

// Initialize error boundary
errorBoundary.clearErrors();

// Log environment configuration
console.log(`[Root Config] Starting in ${environment.environment} mode`);
console.log(`[Root Config] API Gateway URL: ${environment.apiGatewayUrl}`);

// Construct routes and applications from layout
const routes = constructRoutes(microfrontendLayout);
const applications = constructApplications({
  routes,
  loadApp({ name }) {
    return System.import(name);
    // console.log(`[Root Config] Loading application: ${name}`);
    // return import(/* webpackIgnore: true */ name).catch((error) => {
    //   console.error(`[Root Config] Failed to load ${name}:`, error);
    //   errorBoundary.handleError({
    //     message: `Failed to load micro-frontend: ${name}`,
    //     stack: error.stack,
    //     timestamp: new Date().toISOString(),
    //     appName: name,
    //     url: window.location.href,
    //   });
    //   throw error;
    // });
  },
});

// Construct layout engine
const layoutEngine = constructLayoutEngine({ routes, applications });

// Register all applications
console.log('[Root Config] Registering applications:', applications.map(a => a.name));
applications.forEach(registerApplication);

// Activate layout engine
layoutEngine.activate();

// Debug: Log route changes
window.addEventListener('single-spa:before-routing-event', (evt: any) => {
  console.log('[Root Config] Routing to:', window.location.pathname);
  console.log('[Root Config] Active apps:', evt.detail?.newAppStatuses);
});

// Start Single-SPA
start({
  urlRerouteOnly: true,
});

// Start health monitoring in development/staging
if (environment.enableDevTools) {
  healthCheckService.startMonitoring((results) => {
    const overallStatus = healthCheckService.getOverallStatus(results);
    console.log(`[Health Check] Overall Status: ${overallStatus}`, results);
  });
}

// Expose utilities globally for debugging (dev only)
if (environment.enableDevTools) {
  (window as any).__marketplace__ = {
    environment,
    errorBoundary,
    healthCheckService,
    applications,
  };
  console.log(
    "[Root Config] Debug utilities available at window.__marketplace__"
  );
}

console.log("[Root Config] Application started successfully");
