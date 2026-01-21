/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * AuthService
 * Handles all authentication-related API calls and token management
 */
import apiClient from './apiClient';
import { setAuthToken, clearAuthTokens, getRefreshToken, setUserData } from '@marketplace/shared-utils';

export interface LoginCredentials {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterCredentials {
  email: string;
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
  expiresIn?: number;
  user?: {
    id: string;
    username: string;
    email: string;
    role: string;
  };
}

class AuthService {
  private refreshTimeout: NodeJS.Timeout | null = null;

  /**
   * Login user with email and password
   */
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/login', {
        email: credentials.email,
        password: credentials.password,
        rememberMe: credentials.rememberMe || false,
      });

      if (response.data.token) {
        const authToken = {
          accessToken: response.data.token,
          refreshToken: response.data.refreshToken || '',
          expiresIn: response.data.expiresIn || 3600,
          tokenType: 'Bearer',
        };
        setAuthToken(authToken);

        if (response.data.user) {
          setUserData(response.data.user as any);
        }

        // Start token refresh if remember me is enabled
        if (credentials.rememberMe) {
          this.scheduleTokenRefresh(authToken.expiresIn);
        }
      }

      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Register user with email, username, and password
   */
  async register(credentials: RegisterCredentials): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/register', {
        email: credentials.email,
        username: credentials.username,
        password: credentials.password,
      });

      if (response.data.token) {
        const authToken = {
          accessToken: response.data.token,
          refreshToken: response.data.refreshToken || '',
          expiresIn: response.data.expiresIn || 3600,
          tokenType: 'Bearer',
        };
        setAuthToken(authToken);

        if (response.data.user) {
          setUserData(response.data.user as any);
        }

        // Start token refresh
        this.scheduleTokenRefresh(authToken.expiresIn);
      }

      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Refresh authentication token using refresh token
   */
  async refreshToken(): Promise<AuthResponse> {
    try {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await apiClient.post<AuthResponse>('/auth/refresh', {
        refreshToken,
      });

      if (response.data.token) {
        const authToken = {
          accessToken: response.data.token,
          refreshToken: response.data.refreshToken || refreshToken,
          expiresIn: response.data.expiresIn || 3600,
          tokenType: 'Bearer',
        };
        setAuthToken(authToken);

        // Reschedule token refresh
        this.scheduleTokenRefresh(authToken.expiresIn);
      }

      return response.data;
    } catch (error) {
      // If refresh fails, clear auth and let user login again
      clearAuthTokens();
      throw error;
    }
  }

  /**
   * Logout user - clear tokens and notify server
   */
  async logout(): Promise<void> {
    try {
      // Cancel any pending token refresh
      if (this.refreshTimeout) {
        clearTimeout(this.refreshTimeout);
        this.refreshTimeout = null;
      }

      // Notify server of logout
      await apiClient.post('/auth/logout', {});
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Always clear tokens locally
      clearAuthTokens();
    }
  }

  /**
   * Schedule automatic token refresh before expiration
   * Refreshes token 5 minutes before expiration
   */
  private scheduleTokenRefresh(expiresIn: number): void {
    // Cancel any existing timeout
    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
    }

    // Refresh 5 minutes (300 seconds) before expiration
    const refreshTime = Math.max((expiresIn - 300) * 1000, 1000);

    this.refreshTimeout = setTimeout(async () => {
      try {
        await this.refreshToken();
      } catch (error) {
        console.error('Automatic token refresh failed:', error);
        // Refresh failed, tokens have been cleared
      }
    }, refreshTime);
  }

  /**
   * Handle session timeout
   * Called when user is inactive for a certain period
   */
  handleSessionTimeout(): void {
    clearAuthTokens();
    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
      this.refreshTimeout = null;
    }
  }

  /**
   * Setup session timeout listener
   * Logs user out after specified inactivity period (in milliseconds)
   */
  setupSessionTimeout(inactivityMs: number = 30 * 60 * 1000): () => void {
    let timeoutId: NodeJS.Timeout | null = null;

    const resetTimeout = () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      timeoutId = setTimeout(() => {
        this.handleSessionTimeout();
        window.location.href = '/users/auth/login?sessionExpired=true';
      }, inactivityMs);
    };

    // Listen for user activity
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];

    events.forEach((event) => {
      document.addEventListener(event, resetTimeout);
    });

    // Initial timeout
    resetTimeout();

    // Return cleanup function
    return () => {
      events.forEach((event) => {
        document.removeEventListener(event, resetTimeout);
      });
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  }

  /**
   * Clear all timeouts and timers
   */
  cleanup(): void {
    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
      this.refreshTimeout = null;
    }
  }
}

export default new AuthService();
