/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { getAuthToken, clearAuthTokens, getUserData } from '../utils/auth';
import logo from '../assets/navbarLogo.png';

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

const LogoLink = styled(Link)`
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

const NavLink = styled(Link)<{ $active?: boolean }>`
  color: ${props => props.$active ? '#9F7AEA' : '#e0e0e0'};
  text-decoration: none;
  font-weight: ${props => props.$active ? '600' : '500'};
  transition: color 0.2s;
  position: relative;
  
  &:after {
    content: '';
    position: absolute;
    bottom: -4px;
    left: 0;
    width: ${props => props.$active ? '100%' : '0'};
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

const ExternalNavLink = styled.a<{ $active?: boolean }>`
  color: ${props => props.$active ? '#9F7AEA' : '#e0e0e0'};
  text-decoration: none;
  font-weight: ${props => props.$active ? '600' : '500'};
  transition: color 0.2s;
  position: relative;
  
  &:after {
    content: '';
    position: absolute;
    bottom: -4px;
    left: 0;
    width: ${props => props.$active ? '100%' : '0'};
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

const Button = styled.button`
  background: linear-gradient(135deg, #9F7AEA 0%, #6B46C1 100%);
  color: white;
  padding: 0.6rem 1.8rem;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 1px solid rgba(159, 122, 234, 0.3);
  box-shadow: 0 4px 15px rgba(159, 122, 234, 0.3);

  &:hover {
    background: linear-gradient(135deg, #C471ED 0%, #9F7AEA 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(159, 122, 234, 0.5);
  }
  
  &:active {
    transform: translateY(0);
  }
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const UserName = styled.span`
  color: #e0e0e0;
  font-weight: 500;
`;

const Navigation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isAuthenticated = !!getAuthToken();
  const currentUser = getUserData();

  const handleLogout = () => {
    clearAuthTokens();
    window.location.href = '/login';
  };

  const isActive = (path: string) => location.pathname === path;

  return (
    <HeaderContainer>
      <Nav>
        <LogoLink to="/users">
          <LogoImage src={logo} alt="Marketplace" />
        </LogoLink>
        
        <NavLinks>
          <ExternalNavLink 
            href="/marketplace" 
            $active={location.pathname === '/marketplace'}
          >
            Marketplace
          </ExternalNavLink>
          
          <ExternalNavLink 
            href="/users/profile" 
            $active={location.pathname === '/users/profile'}
          >
            Profile
          </ExternalNavLink>
          
          {isAuthenticated && (
            <>
              <NavLink 
                to="/settings" 
                $active={isActive('/users/settings')}
              >
                Settings
              </NavLink>
              <Button onClick={handleLogout}>Logout</Button>
            </>
          )}
        </NavLinks>
      </Nav>
    </HeaderContainer>
  );
};

export default Navigation;
