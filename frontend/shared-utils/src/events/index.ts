/**
 * This file was created by Claude Sonnet 4.5
 * 
 * Custom Event System
 * Inter-MFE communication via custom browser events
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

export const EVENT_TYPES = {
  USER_LOGIN: "user:login",
  USER_LOGOUT: "user:logout",
  USER_UPDATED: "user:updated",
  CART_UPDATED: "cart:updated",
  NAVIGATION_CHANGE: "navigation:change",
  THEME_CHANGED: "theme:changed",
  ERROR_OCCURRED: "error:occurred",
} as const;

export type EventType = (typeof EVENT_TYPES)[keyof typeof EVENT_TYPES];

/**
 * Emit a custom event to communicate between MFEs
 */
export function emitEvent<T = unknown>(
  eventType: EventType,
  payload: T,
  source?: string
): void {
  const event = new CustomEvent(eventType, {
    detail: {
      type: eventType,
      payload,
      timestamp: new Date().toISOString(),
      source: source || "unknown",
    },
    bubbles: true,
    composed: true,
  });

  window.dispatchEvent(event);

  // Log events in development mode
  if (typeof window !== "undefined" && window.location.hostname === "localhost") {
    console.log(`[Event] ${eventType}`, { payload, source });
  }
}

/**
 * Listen to custom events from other MFEs
 */
export function onEvent<T = unknown>(
  eventType: EventType,
  callback: (payload: T, detail: CustomEventDetail<T>) => void
): () => void {
  const handler = (event: Event) => {
    const customEvent = event as CustomEvent<CustomEventDetail<T>>;
    if (customEvent.detail) {
      callback(customEvent.detail.payload, customEvent.detail);
    }
  };

  window.addEventListener(eventType, handler);

  // Return cleanup function
  return () => {
    window.removeEventListener(eventType, handler);
  };
}

/**
 * Listen to an event once, then automatically remove the listener
 */
export function onEventOnce<T = unknown>(
  eventType: EventType,
  callback: (payload: T, detail: CustomEventDetail<T>) => void
): void {
  const handler = (event: Event) => {
    const customEvent = event as CustomEvent<CustomEventDetail<T>>;
    if (customEvent.detail) {
      callback(customEvent.detail.payload, customEvent.detail);
    }
    window.removeEventListener(eventType, handler);
  };

  window.addEventListener(eventType, handler);
}

interface CustomEventDetail<T> {
  type: EventType;
  payload: T;
  timestamp: string;
  source: string;
}
