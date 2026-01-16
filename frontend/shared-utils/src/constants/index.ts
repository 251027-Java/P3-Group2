/**
 * This file was created by Claude Sonnet 4.5
 * 
 * Shared Constants
 * Global constants used across all MFEs
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

// API Configuration
// Note: BASE_URL should be configured per MFE based on their environment
export const API_CONFIG = {
  BASE_URL: "http://localhost:8080", // Override this in each MFE
  VERSION: "v1",
  TIMEOUT: 30000, // 30 seconds
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: "marketplace_auth_token",
  REFRESH_TOKEN: "marketplace_refresh_token",
  USER_DATA: "marketplace_user_data",
  THEME: "marketplace_theme",
  LANGUAGE: "marketplace_language",
  CART: "marketplace_cart",
} as const;

// API Endpoints
export const API_ENDPOINTS = {
  // Auth endpoints
  LOGIN: "/api/v1/auth/login",
  REGISTER: "/api/v1/auth/register",
  LOGOUT: "/api/v1/auth/logout",
  REFRESH_TOKEN: "/api/v1/auth/refresh",

  // User endpoints
  USER_PROFILE: "/api/v1/users/profile",
  UPDATE_PROFILE: "/api/v1/users/profile",
  CHANGE_PASSWORD: "/api/v1/users/password",

  // Card endpoints
  CARDS: "/api/v1/cards",
  CARD_DETAIL: (id: string) => `/api/v1/cards/${id}`,
  CARD_SEARCH: "/api/v1/cards/search",

  // Cart endpoints
  CART: "/api/v1/cart",
  ADD_TO_CART: "/api/v1/cart/items",
  REMOVE_FROM_CART: (itemId: string) => `/api/v1/cart/items/${itemId}`,
} as const;

// HTTP Status Codes
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
  SERVICE_UNAVAILABLE: 503,
} as const;

// Pagination Defaults
export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 20,
  MAX_SIZE: 100,
} as const;

// Validation Rules
export const VALIDATION = {
  MIN_PASSWORD_LENGTH: 8,
  MAX_PASSWORD_LENGTH: 128,
  MIN_USERNAME_LENGTH: 3,
  MAX_USERNAME_LENGTH: 50,
  MAX_EMAIL_LENGTH: 255,
} as const;

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: "MMM DD, YYYY",
  FULL: "MMMM DD, YYYY h:mm A",
  ISO: "YYYY-MM-DDTHH:mm:ss",
} as const;

// User Roles
export const USER_ROLES = {
  USER: "USER",
  ADMIN: "ADMIN",
  MODERATOR: "MODERATOR",
} as const;

// Card Types
export const CARD_TYPES = {
  POKEMON: "POKEMON",
  MAGIC: "MAGIC",
  YUGIOH: "YUGIOH",
  SPORTS: "SPORTS",
  OTHER: "OTHER",
} as const;

// Card Rarities
export const CARD_RARITIES = {
  COMMON: "COMMON",
  UNCOMMON: "UNCOMMON",
  RARE: "RARE",
  ULTRA_RARE: "ULTRA_RARE",
  SECRET_RARE: "SECRET_RARE",
} as const;

// Routes
export const ROUTES = {
  HOME: "/",
  CARDS: "/cards",
  CARD_DETAIL: (id: string) => `/cards/${id}`,
  CARD_SEARCH: "/cards/search",
  AUTH: "/auth",
  LOGIN: "/auth/login",
  REGISTER: "/auth/register",
  PROFILE: "/profile",
  SETTINGS: "/settings",
  NOT_FOUND: "/404",
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  GENERIC: "An error occurred. Please try again.",
  NETWORK: "Network error. Please check your connection.",
  UNAUTHORIZED: "You are not authorized. Please log in.",
  FORBIDDEN: "You do not have permission to perform this action.",
  NOT_FOUND: "The requested resource was not found.",
  VALIDATION: "Please check your input and try again.",
  SERVER_ERROR: "Server error. Please try again later.",
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN: "Successfully logged in!",
  LOGOUT: "Successfully logged out!",
  REGISTER: "Account created successfully!",
  PROFILE_UPDATED: "Profile updated successfully!",
  PASSWORD_CHANGED: "Password changed successfully!",
  ITEM_ADDED: "Item added to cart!",
  ITEM_REMOVED: "Item removed from cart!",
} as const;
