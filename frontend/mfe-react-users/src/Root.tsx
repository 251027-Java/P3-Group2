/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import App from './App';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export default function Root(_props: any) {
  // Check if we're under /users path or at root
  const basename = window.location.pathname.startsWith('/users') ? '/users' : '';
  
  return (
    <BrowserRouter basename={basename}>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  );
}
