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
  25% {
    background-position: 100% 50%;
  }
  50% {
    background-position: 0% 100%;
  }
  75% {
    background-position: 100% 0%;
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
    #000000 0%,
    #0a0515 25%,
    #000000 50%,
    #0f0a1f 75%,
    #000000 100%
  );
  background-size: 400% 400%;
  animation: ${gradientAnimation} 5s ease-in-out infinite;
`;

const AuroraOverlay = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(
    ellipse 80vw 60vh at 20% 30%,
    rgba(138, 43, 226, 0.45),
    rgba(75, 0, 130, 0.25) 40%,
    transparent 70%
  ),
  radial-gradient(
    ellipse 70vw 50vh at 80% 20%,
    rgba(255, 20, 147, 0.4),
    rgba(199, 21, 133, 0.22) 40%,
    transparent 70%
  ),
  radial-gradient(
    ellipse 75vw 55vh at 50% 40%,
    rgba(0, 191, 255, 0.35),
    rgba(30, 144, 255, 0.18) 40%,
    transparent 70%
  ),
  radial-gradient(
    ellipse 60vw 45vh at 65% 25%,
    rgba(147, 51, 234, 0.42),
    rgba(126, 34, 206, 0.23) 40%,
    transparent 70%
  );
  opacity: 1;
  filter: blur(50px);
  animation: auroraShimmer 7s ease-in-out infinite;
  
  @keyframes auroraShimmer {
    0%, 100% {
      opacity: 1;
      transform: scale(1) translateY(0) rotate(0deg);
    }
    20% {
      opacity: 0.8;
      transform: scale(1.12) translateY(-25px) rotate(2deg);
    }
    40% {
      opacity: 0.95;
      transform: scale(0.98) translateY(15px) rotate(-1deg);
    }
    60% {
      opacity: 0.85;
      transform: scale(1.15) translateY(-30px) rotate(3deg);
    }
    80% {
      opacity: 0.92;
      transform: scale(1.05) translateY(20px) rotate(-2deg);
    }
  }
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
