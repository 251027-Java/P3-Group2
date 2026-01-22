/**
 * This file was created by Claude Sonnet 4.5
 */
import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { getAuthToken, clearAuthTokens } from '../utils/auth';
<<<<<<< HEAD
import { environment } from '../utils/environment';
=======
>>>>>>> origin/frontendLoginSignup

// Create axios instance with base configuration
const apiClient: AxiosInstance = axios.create({
  baseURL: environment.apiUrl || 'http://localhost:8081',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add auth token to requests
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getAuthToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors globally
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error: AxiosError) => {
    if (error.response) {
      // Server responded with error status
      const { status } = error.response;

      if (status === 401) {
        // Unauthorized - clear tokens and redirect to login
        clearAuthTokens();
        window.location.href = '/users/auth/login';
      } else if (status === 403) {
        // Forbidden - user doesn't have permission
        console.error('Access forbidden');
      } else if (status >= 500) {
        // Server error
        console.error('Server error:', error.response.data);
      }
    } else if (error.request) {
      // Request made but no response received
      console.error('No response from server');
    } else {
      // Error in request setup
      console.error('Request error:', error.message);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
