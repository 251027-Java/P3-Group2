/**
 * This file was created by Claude Haiku 4.5
 */
import React from 'react';

const TestPage: React.FC = () => {
  console.log('[TestPage] Rendering TestPage component!');
  
  return (
    <div style={{
      padding: '2rem',
      textAlign: 'center',
      fontFamily: 'Arial, sans-serif'
    }}>
      <h1 style={{ color: '#667eea' }}>Test Page Works! ✅</h1>
      <p style={{ fontSize: '1.2rem', color: '#333' }}>
        This is a simple React component rendered by the React MFE.
      </p>
      <p style={{ color: '#666' }}>
        Route: <code>/test</code>
      </p>
      <p style={{ color: '#666' }}>
        Current time: {new Date().toLocaleTimeString()}
      </p>
      <p style={{ color: '#999', marginTop: '1rem' }}>
        Full path: {window.location.pathname}
      </p>
    </div>
  );
};

export default TestPage;
