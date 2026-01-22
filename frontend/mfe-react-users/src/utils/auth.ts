/**
 * Local auth utilities to avoid shared-utils import issues in production
 */

const STORAGE_KEYS = {
  AUTH_TOKEN: 'marketplace_auth_token',
  REFRESH_TOKEN: 'marketplace_refresh_token',
  USER_DATA: 'marketplace_user_data',
};

export function setAuthToken(token: { accessToken: string; refreshToken: string }): void {
  try {
    localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token.accessToken);
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, token.refreshToken);
  } catch (error) {
    console.error('Failed to store auth token:', error);
  }
}

export function getAuthToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  } catch (error) {
    console.error('Failed to get auth token:', error);
    return null;
  }
}

export function getRefreshToken(): string | null {
  try {
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  } catch (error) {
    console.error('Failed to get refresh token:', error);
    return null;
  }
}

export function clearAuthTokens(): void {
  try {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  } catch (error) {
    console.error('Failed to clear auth tokens:', error);
  }
}

export function getUserData(): any | null {
  try {
    const data = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return data ? JSON.parse(data) : null;
  } catch {
    return null;
  }
}

export function setUserData(user: any): void {
  try {
    localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(user));
  } catch (error) {
    console.error('Failed to store user data:', error);
  }
}

export function isTokenExpired(token: string): boolean {
  try {
    const payload = parseJWT(token);
    const currentTime = Date.now() / 1000;
    return payload.exp <= currentTime;
  } catch {
    return true;
  }
}

export function parseJWT(token: string): {
  exp: number;
  iat: number;
  sub: string;
  [key: string]: any;
} {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Failed to parse JWT:', error);
    throw new Error('Invalid JWT token');
  }
}

export function hasRole(requiredRole: string): boolean {
  const user = getUserData();
  return user?.role === requiredRole;
}
