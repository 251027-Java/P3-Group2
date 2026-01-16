/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Navigate } from 'react-router-dom';
import { getAuthToken, isTokenExpired } from '@marketplace/shared-utils';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const token = getAuthToken();
  
  // Check if token exists and is not expired
  if (!token || isTokenExpired(token)) {
    return <Navigate to="/users/auth/login" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
