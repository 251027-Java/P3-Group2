/**
 * This file was created by Claude Haiku 4.5
 */
import React, { createContext, useContext, useEffect, useState, ReactNode, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getAuthToken, clearAuthTokens, userState, getUserData } from '@marketplace/shared-utils';
import AuthService from '../services/AuthService';

interface User {
  id: string;
  username: string;
  email: string;
  role: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  loading: boolean;
  redirectAfterLogin: () => void;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
  sessionTimeoutMs?: number;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ 
  children, 
  sessionTimeoutMs = 30 * 60 * 1000 // 30 minutes default
}) => {
  const [isAuthenticated, setIsAuthenticated] = useState(!!getAuthToken());
  const [user, setUser] = useState<User | null>(getUserData() as User | null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const sessionTimeoutCleanupRef = React.useRef<(() => void) | null>(null);

  useEffect(() => {
    // Subscribe to user state changes
    const unsubscribe = userState.subscribe(() => {
      const token = getAuthToken();
      const userData = getUserData();
      setIsAuthenticated(!!token);
      setUser(userData as User | null);
    });

    // Setup session timeout if user is authenticated
    if (isAuthenticated) {
      sessionTimeoutCleanupRef.current = AuthService.setupSessionTimeout(sessionTimeoutMs);
    }

    return () => {
      unsubscribe();
      if (sessionTimeoutCleanupRef.current) {
        sessionTimeoutCleanupRef.current();
      }
    };
  }, [isAuthenticated, sessionTimeoutMs]);

  const redirectAfterLogin = useCallback(() => {
    // Get the location they tried to visit before being redirected to login
    const from = (location.state as any)?.from?.pathname || '/users/profile';
    navigate(from, { replace: true });
  }, [location, navigate]);

  const handleLogout = useCallback(async () => {
    setLoading(true);
    try {
      await AuthService.logout();
      setIsAuthenticated(false);
      setUser(null);
      navigate('/users/auth/login', { replace: true });
    } catch (error) {
      console.error('Logout error:', error);
      // Still clear auth even if API call fails
      clearAuthTokens();
      setIsAuthenticated(false);
      setUser(null);
      navigate('/users/auth/login', { replace: true });
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (sessionTimeoutCleanupRef.current) {
        sessionTimeoutCleanupRef.current();
      }
      AuthService.cleanup();
    };
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, loading, redirectAfterLogin, logout: handleLogout }}>
      {children}
    </AuthContext.Provider>
  );
};
