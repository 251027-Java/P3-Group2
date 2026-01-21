/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import ReactDOM from 'react-dom';
import singleSpaReact from 'single-spa-react';
import Root from './Root';

const lifecycles = singleSpaReact({
  React,
  ReactDOM,
  rootComponent: Root,
  errorBoundary(err, _info, _props) {
    console.error('React Users MFE Error:', err);
    return (
      <div style={{ padding: '20px', color: 'red' }}>
        <h2>Error in User Management</h2>
        <p>{err.message}</p>
      </div>
    );
  },
});

export const { bootstrap, mount, unmount } = lifecycles;
