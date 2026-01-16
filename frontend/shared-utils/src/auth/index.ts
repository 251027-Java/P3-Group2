/**
 * This file was created by Claude Sonnet 4.5
 *
 * Shared Authentication Utilities
 * JWT token management and authentication helpers
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

import { STORAGE_KEYS } from "../constants";
import { AuthToken, User } from "../types";

/**
 * Store authentication token in storage
 */
export function setAuthToken(token: AuthToken): void {
  try {
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token.accessToken);
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, token.refreshToken);
  } catch (error) {
    console.error("Failed to store auth token:", error);
  }
}

/**
 * Get authentication token from storage
 */
export function getAuthToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  } catch (error) {
    console.error("Failed to get auth token:", error);
    return null;
  }
}

/**
 * Get refresh token from storage
 */
export function getRefreshToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  } catch (error) {
    console.error("Failed to get refresh token:", error);
    return null;
  }
}

/**
 * Remove authentication tokens from storage
 */
export function clearAuthTokens(): void {
  try {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  } catch (error) {
    console.error("Failed to clear auth tokens:", error);
  }
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated(): boolean {
  const token = getAuthToken();
  if (!token) return false;

  try {
    const payload = parseJWT(token);
    const currentTime = Date.now() / 1000;
    return payload.exp > currentTime;
  } catch {
    return false;
  }
}

/**
 * Parse JWT token to get payload
 */
export function parseJWT(token: string): {
  exp: number;
  iat: number;
  sub: string;
  [key: string]: unknown;
} {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error("Failed to parse JWT:", error);
    throw new Error("Invalid JWT token");
  }
}

/**
 * Get token expiration time
 */
export function getTokenExpiration(token: string): Date | null {
  try {
    const payload = parseJWT(token);
    return new Date(payload.exp * 1000);
  } catch {
    return null;
  }
}

/**
 * Check if token is expired
 */
export function isTokenExpired(token: string): boolean {
  try {
    const payload = parseJWT(token);
    const currentTime = Date.now() / 1000;
    return payload.exp <= currentTime;
  } catch {
    return true;
  }
}

/**
 * Store user data in storage
 */
export function setUserData(user: User): void {
  try {
    localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(user));
  } catch (error) {
    console.error("Failed to store user data:", error);
  }
}

/**
 * Get user data from storage
 */
export function getUserData(): User | null {
  try {
    const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return userData ? JSON.parse(userData) : null;
  } catch (error) {
    console.error("Failed to get user data:", error);
    return null;
  }
}

/**
 * Get user role from token or storage
 */
export function getUserRole(): string | null {
  const user = getUserData();
  if (user) return user.role;

  const token = getAuthToken();
  if (!token) return null;

  try {
    const payload = parseJWT(token);
    return (payload.role as string) || null;
  } catch {
    return null;
  }
}

/**
 * Check if user has a specific role
 */
export function hasRole(role: string): boolean {
  const userRole = getUserRole();
  return userRole === role;
}

/**
 * Check if user has any of the specified roles
 */
export function hasAnyRole(roles: string[]): boolean {
  const userRole = getUserRole();
  return userRole ? roles.includes(userRole) : false;
}

/**
 * Create Authorization header value
 */
export function getAuthHeader(): string | null {
  const token = getAuthToken();
  return token ? `Bearer ${token}` : null;
}

/**
 * Add Authorization header to request headers
 */
export function addAuthHeader(
  headers: Record<string, string> = {}
): Record<string, string> {
  const authHeader = getAuthHeader();
  if (authHeader) {
    headers["Authorization"] = authHeader;
  }
  return headers;
}
