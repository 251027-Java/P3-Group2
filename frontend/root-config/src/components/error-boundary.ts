/**
 * Error Boundary Component
 * Catches and handles errors in micro-frontends
 * Generated with assistance from GitHub Copilot
 */

import { environment } from "../config/environment";

export interface ErrorInfo {
  message: string;
  stack?: string;
  componentStack?: string;
  timestamp: string;
  appName?: string;
  url: string;
}

export class ErrorBoundary {
  private errorContainer: HTMLElement | null = null;
  private errors: ErrorInfo[] = [];

  constructor() {
    this.setupGlobalErrorHandlers();
    this.createErrorContainer();
  }

  /**
   * Setup global error handlers
   */
  private setupGlobalErrorHandlers(): void {
    // Handle uncaught JavaScript errors
    window.addEventListener("error", (event: ErrorEvent) => {
      this.handleError({
        message: event.message,
        stack: event.error?.stack,
        timestamp: new Date().toISOString(),
        url: window.location.href,
      });
    });

    // Handle unhandled promise rejections
    window.addEventListener(
      "unhandledrejection",
      (event: PromiseRejectionEvent) => {
        this.handleError({
          message: event.reason?.message || "Unhandled Promise Rejection",
          stack: event.reason?.stack,
          timestamp: new Date().toISOString(),
          url: window.location.href,
        });
      }
    );

    // Handle single-spa application errors
    window.addEventListener(
      "single-spa:routing-event" as any,
      (event: CustomEvent) => {
        if (event.detail?.errorsLoading?.length > 0) {
          event.detail.errorsLoading.forEach((error: any) => {
            this.handleError({
              message: `Failed to load application: ${error.appOrParcelName}`,
              stack: error.error?.stack,
              timestamp: new Date().toISOString(),
              appName: error.appOrParcelName,
              url: window.location.href,
            });
          });
        }
      }
    );
  }

  /**
   * Create the error display container
   */
  private createErrorContainer(): void {
    this.errorContainer = document.createElement("div");
    this.errorContainer.id = "error-boundary-container";
    this.errorContainer.style.display = "none";
    document.body.appendChild(this.errorContainer);
  }

  /**
   * Handle and log errors
   */
  handleError(errorInfo: ErrorInfo): void {
    this.errors.push(errorInfo);
    this.logError(errorInfo);

    if (!environment.production) {
      console.error("Error Boundary caught an error:", errorInfo);
    }

    // Show error UI if configured
    if (environment.enableDevTools) {
      this.displayError(errorInfo);
    }

    // Send to monitoring service (e.g., Sentry, ELK)
    this.reportError(errorInfo);
  }

  /**
   * Display error in the UI
   */
  private displayError(errorInfo: ErrorInfo): void {
    if (!this.errorContainer) return;

    this.errorContainer.style.display = "block";
    this.errorContainer.innerHTML = `
      <div style="
        position: fixed;
        top: 20px;
        right: 20px;
        max-width: 500px;
        background: #fee;
        border: 2px solid #c33;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        z-index: 10000;
        font-family: system-ui, -apple-system, sans-serif;
      ">
        <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 10px;">
          <h3 style="margin: 0; color: #c33; font-size: 18px;">⚠️ Application Error</h3>
          <button onclick="this.closest('div').parentElement.style.display='none'" 
                  style="border: none; background: none; font-size: 24px; cursor: pointer; color: #c33;">×</button>
        </div>
        <p style="margin: 10px 0; color: #333; font-size: 14px;">
          <strong>Message:</strong> ${this.escapeHtml(errorInfo.message)}
        </p>
        ${
          errorInfo.appName
            ? `<p style="margin: 10px 0; color: #666; font-size: 12px;">
          <strong>Application:</strong> ${this.escapeHtml(errorInfo.appName)}
        </p>`
            : ""
        }
        <p style="margin: 10px 0; color: #666; font-size: 12px;">
          <strong>Time:</strong> ${new Date(
            errorInfo.timestamp
          ).toLocaleString()}
        </p>
        ${
          errorInfo.stack && environment.logLevel === "debug"
            ? `
          <details style="margin-top: 10px;">
            <summary style="cursor: pointer; color: #666; font-size: 12px;">View Stack Trace</summary>
            <pre style="
              margin-top: 10px;
              padding: 10px;
              background: #f5f5f5;
              border-radius: 4px;
              overflow-x: auto;
              font-size: 11px;
              color: #333;
            ">${this.escapeHtml(errorInfo.stack)}</pre>
          </details>
        `
            : ""
        }
        <button onclick="location.reload()" style="
          margin-top: 15px;
          padding: 8px 16px;
          background: #c33;
          color: white;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 14px;
        ">Reload Page</button>
      </div>
    `;

    // Auto-hide after 10 seconds in production
    if (environment.production) {
      setTimeout(() => {
        if (this.errorContainer) {
          this.errorContainer.style.display = "none";
        }
      }, 10000);
    }
  }

  /**
   * Log error to console with appropriate level
   */
  private logError(errorInfo: ErrorInfo): void {
    const logEntry = {
      level: "error",
      timestamp: errorInfo.timestamp,
      message: errorInfo.message,
      appName: errorInfo.appName,
      url: errorInfo.url,
      stack: errorInfo.stack,
      environment: environment.environment,
    };

    console.error("[Error Boundary]", logEntry);
  }

  /**
   * Report error to monitoring service
   */
  private reportError(errorInfo: ErrorInfo): void {
    // TODO: Integrate with ELK Stack or other monitoring service
    // This could send errors to Logstash via HTTP
    if (environment.production) {
      // Example: Send to logging endpoint
      // fetch('/api/v1/logs/errors', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(errorInfo),
      // }).catch(console.error);
    }
  }

  /**
   * Escape HTML to prevent XSS
   */
  private escapeHtml(text: string): string {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Clear all errors
   */
  clearErrors(): void {
    this.errors = [];
    if (this.errorContainer) {
      this.errorContainer.style.display = "none";
      this.errorContainer.innerHTML = "";
    }
  }

  /**
   * Get all logged errors
   */
  getErrors(): ErrorInfo[] {
    return [...this.errors];
  }
}

// Export singleton instance
export const errorBoundary = new ErrorBoundary();
