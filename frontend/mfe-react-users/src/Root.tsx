/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import App from './App';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export default function Root(_props: any) {
  return (
    <BrowserRouter basename="/users">
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  );
}
