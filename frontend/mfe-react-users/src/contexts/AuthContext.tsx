/**
 * This file was created by Claude Sonnet 4.5
 */
import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getAuthToken, userState } from '@marketplace/shared-utils';

interface AuthContextType {
  isAuthenticated: boolean;
  redirectAfterLogin: () => void;
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
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(!!getAuthToken());
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    // Subscribe to user state changes
    const unsubscribe = userState.subscribe(() => {
      const token = getAuthToken();
      setIsAuthenticated(!!token);
    });

    return unsubscribe;
  }, []);

  const redirectAfterLogin = () => {
    // Get the location they tried to visit before being redirected to login
    const from = (location.state as any)?.from?.pathname || '/users/profile';
    navigate(from, { replace: true });
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, redirectAfterLogin }}>
      {children}
    </AuthContext.Provider>
  );
};
