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

// Construct routes and applications from layout
const routes = constructRoutes(microfrontendLayout);
const applications = constructApplications({
  routes,
  loadApp({ name }) {
    return System.import(name);
  },
});

// Construct layout engine
const layoutEngine = constructLayoutEngine({ routes, applications });

// Register all applications
applications.forEach(registerApplication);

// Activate layout engine
layoutEngine.activate();

// Start Single-SPA
start({
  urlRerouteOnly: true,
});

console.log("[Root Config] Application started successfully");
