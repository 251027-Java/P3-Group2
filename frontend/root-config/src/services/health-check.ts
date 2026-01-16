/**
 * This file was created by Claude Sonnet 4.5
 *
 * Health Check Service
 * Monitors the health of all micro-frontends and backend services
 * Generated with assistance from GitHub Copilot
 */

import { environment } from "../config/environment";

export interface HealthStatus {
  status: "healthy" | "unhealthy" | "degraded";
  message?: string;
  timestamp: string;
}

export interface ServiceHealth {
  serviceName: string;
  url: string;
  status: HealthStatus;
}

export class HealthCheckService {
  private healthCheckInterval: number = 30000; // 30 seconds
  private intervalId: NodeJS.Timeout | null = null;

  /**
   * Check the health of a single service
   */
  async checkService(serviceName: string, url: string): Promise<ServiceHealth> {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout

      const response = await fetch(`${url}/health`, {
        method: "GET",
        signal: controller.signal,
        headers: {
          "Content-Type": "application/json",
        },
      });

      clearTimeout(timeoutId);

      const status: HealthStatus = {
        status: response.ok ? "healthy" : "unhealthy",
        message: response.ok
          ? "Service is operational"
          : `HTTP ${response.status}`,
        timestamp: new Date().toISOString(),
      };

      return { serviceName, url, status };
    } catch (error) {
      const status: HealthStatus = {
        status: "unhealthy",
        message: error instanceof Error ? error.message : "Unknown error",
        timestamp: new Date().toISOString(),
      };

      return { serviceName, url, status };
    }
  }

  /**
   * Check the health of all configured services
   */
  async checkAllServices(): Promise<ServiceHealth[]> {
    const services = [
      { name: "API Gateway", url: environment.apiGatewayUrl },
      { name: "Card Service", url: `${environment.apiUrl}/cards` },
      { name: "User Service", url: `${environment.apiUrl}/users` },
    ];

    const healthChecks = services.map((service) =>
      this.checkService(service.name, service.url)
    );

    return Promise.all(healthChecks);
  }

  /**
   * Start periodic health checks
   */
  startMonitoring(callback: (results: ServiceHealth[]) => void): void {
    if (this.intervalId) {
      console.warn("Health monitoring is already running");
      return;
    }

    // Initial check
    this.checkAllServices().then(callback);

    // Periodic checks
    this.intervalId = setInterval(() => {
      this.checkAllServices().then(callback);
    }, this.healthCheckInterval);

    console.log("Health monitoring started");
  }

  /**
   * Stop periodic health checks
   */
  stopMonitoring(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
      console.log("Health monitoring stopped");
    }
  }

  /**
   * Get the overall system health status
   */
  getOverallStatus(
    serviceHealths: ServiceHealth[]
  ): "healthy" | "unhealthy" | "degraded" {
    const unhealthyCount = serviceHealths.filter(
      (sh) => sh.status.status === "unhealthy"
    ).length;
    const degradedCount = serviceHealths.filter(
      (sh) => sh.status.status === "degraded"
    ).length;

    if (unhealthyCount > 0) {
      return unhealthyCount === serviceHealths.length
        ? "unhealthy"
        : "degraded";
    }

    if (degradedCount > 0) {
      return "degraded";
    }

    return "healthy";
  }
}

// Export singleton instance
export const healthCheckService = new HealthCheckService();
