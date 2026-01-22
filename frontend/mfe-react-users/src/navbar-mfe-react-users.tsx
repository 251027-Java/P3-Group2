/**
 * This file was created by Claude Sonnet 4.5
 * Standalone export for React Navigation component
 */
import React from 'react';
import ReactDOM from 'react-dom';
import singleSpaReact from 'single-spa-react';
import styled from 'styled-components';
import logo from './assets/navbarLogo.png';

const HeaderContainer = styled.header`
  background: linear-gradient(135deg, #0a0a0a 0%, #1a0a2e 25%, #0a0a0a 50%, #16213e 75%, #0a0a0a 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(159, 122, 234, 0.2);
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 2px solid transparent;
  border-image: linear-gradient(90deg, #9F7AEA 0%, #C471ED 50%, #FF6B9D 100%);
  border-image-slice: 1;
  
  @keyframes gradientShift {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
`;

const Nav = styled.nav`
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
`;

const LogoLink = styled.a`
  display: flex;
  align-items: center;
  text-decoration: none;
  transition: filter 0.2s ease;

  &:hover {
    filter: brightness(1.2);
  }
`;

const LogoImage = styled.img`
  height: 50px;
  width: auto;
  filter: drop-shadow(0 0 10px rgba(159, 122, 234, 0.5));
`;

const NavLinks = styled.div`
  display: flex;
  gap: 2rem;
  align-items: center;
`;

const NavLink = styled.a`
  color: #e0e0e0;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
  position: relative;
  
  &:after {
    content: '';
    position: absolute;
    bottom: -4px;
    left: 0;
    width: 0;
    height: 2px;
    background: linear-gradient(90deg, #9F7AEA 0%, #C471ED 100%);
    transition: width 0.2s ease;
  }

  &:hover {
    color: #9F7AEA;
    
    &:after {
      width: 100%;
    }
  }
`;

const NavigationWrapper = () => {
  console.log('React Navbar rendering...');
  return (
    <HeaderContainer>
      <Nav>
        <LogoLink href="/users">
          <LogoImage src={logo} alt="Marketplace" />
        </LogoLink>
        
        <NavLinks>
          <NavLink href="/marketplace">Marketplace</NavLink>
          <NavLink href="/users/profile">Profile</NavLink>
        </NavLinks>
      </Nav>
    </HeaderContainer>
  );
};

const lifecycles = singleSpaReact({
  React,
  ReactDOM,
  rootComponent: NavigationWrapper,
  errorBoundary(err, _info, _props) {
    console.error('React Navbar MFE Error:', err);
    return (
      <div style={{ padding: '20px', color: 'red', background: 'white' }}>
        <h2>Error in Navigation</h2>
        <p>{err.message}</p>
      </div>
    );
  },
});

export const { bootstrap, mount, unmount } = lifecycles;
