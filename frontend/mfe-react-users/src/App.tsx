/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { GlobalStyles } from './styles/GlobalStyles';
import Navigation from './components/Navigation';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import SettingsPage from './pages/SettingsPage';
import TestPage from './pages/TestPage';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  const location = useLocation();
  const showNavigation = location.pathname !== '/';
  
  console.log('[React MFE] App rendering, current path:', window.location.pathname);
  console.log('[React MFE] Available routes: /test, /profile, /auth/login, /auth/register, /settings');
  
  return (
    <>
      <GlobalStyles />
      {showNavigation && <Navigation />}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/test" element={<TestPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/auth/login" element={<LoginPage />} />
        <Route path="/auth/register" element={<RegisterPage />} />
        <Route path="/auth" element={<Navigate to="/auth/login" replace />} />
        
        <Route
          path="/settings"
          element={
            <ProtectedRoute>
              <SettingsPage />
            </ProtectedRoute>
          }
        />
        
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}

export default App;
