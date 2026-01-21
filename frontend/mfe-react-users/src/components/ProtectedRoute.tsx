/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { getAuthToken, isTokenExpired } from '@marketplace/shared-utils';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const token = getAuthToken();
  const location = useLocation();
  
  // Check if token exists and is not expired
  if (!token || isTokenExpired(token)) {
    // Redirect to login and save the attempted location
    return <Navigate to="/users/auth/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
