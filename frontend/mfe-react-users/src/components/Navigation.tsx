/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { getAuthToken, clearAuthTokens, getUserData } from '../utils/auth';

const HeaderContainer = styled.header`
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(159, 122, 234, 0.2);
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 2px solid transparent;
  border-image: linear-gradient(90deg, #9F7AEA 0%, #C471ED 50%, #FF6B9D 100%);
  border-image-slice: 1;
`;

const Nav = styled.nav`
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
`;

const Logo = styled(Link)`
  font-size: 1.5rem;
  font-weight: bold;
  background: linear-gradient(135deg, #9F7AEA 0%, #C471ED 50%, #FF6B9D 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-decoration: none;
  transition: filter 0.2s ease;

  &:hover {
    filter: brightness(1.2);
  }
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
        <Logo to="/users">Marketplace</Logo>
        
        <NavLinks>
          {!isAuthenticated ? (
            <>
              <a href="/login" style={{ 
                color: location.pathname === '/login' ? '#9F7AEA' : '#e0e0e0',
                textDecoration: 'none',
                fontWeight: location.pathname === '/login' ? 600 : 500,
                transition: 'color 0.2s ease',
                position: 'relative'
              }}>
                Login
              </a>
              <a href="/signup" style={{ 
                color: location.pathname === '/signup' ? '#9F7AEA' : '#e0e0e0',
                textDecoration: 'none',
                fontWeight: location.pathname === '/signup' ? 600 : 500,
                transition: 'color 0.2s ease',
                position: 'relative'
              }}>
                Register
              </a>
            </>
          ) : (
            <>
              <NavLink 
                to="/users/profile" 
                $active={isActive('/users/profile')}
              >
                Profile
              </NavLink>
              <NavLink 
                to="/users/settings" 
                $active={isActive('/users/settings')}
              >
                Settings
              </NavLink>
              <UserInfo>
                {currentUser?.username && (
                  <UserName>Hello, {currentUser.username}</UserName>
                )}
                <Button onClick={handleLogout}>Logout</Button>
              </UserInfo>
            </>
          )}
        </NavLinks>
      </Nav>
    </HeaderContainer>
  );
};

export default Navigation;
