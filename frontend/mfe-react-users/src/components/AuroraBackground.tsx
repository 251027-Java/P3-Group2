/**
 * This file was created by Claude Sonnet 4.5
 * 
 * Aurora Background Component
 * Creates an animated gradient aurora effect for the login page
 */
import React from 'react';
import styled, { keyframes } from 'styled-components';

const gradientAnimation = keyframes`
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
`;

const AuroraContainer = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(
    -45deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #4facfe 75%,
    #00f2fe 100%
  );
  background-size: 400% 400%;
  animation: ${gradientAnimation} 15s ease infinite;
`;

const AuroraOverlay = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(
    circle at 20% 50%,
    rgba(120, 119, 198, 0.3),
    transparent 50%
  ),
  radial-gradient(
    circle at 80% 80%,
    rgba(252, 121, 255, 0.3),
    transparent 50%
  ),
  radial-gradient(
    circle at 40% 20%,
    rgba(75, 172, 254, 0.3),
    transparent 50%
  );
  opacity: 0.8;
`;

interface AuroraBackgroundProps {
  children?: React.ReactNode;
}

const AuroraBackground: React.FC<AuroraBackgroundProps> = ({ children }) => {
  return (
    <>
      <AuroraContainer>
        <AuroraOverlay />
      </AuroraContainer>
      {children}
    </>
  );
};

export default AuroraBackground;
