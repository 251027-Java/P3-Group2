/**
 * This file was created by Claude Sonnet 4.5
 */
import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import styled from 'styled-components';
import { getAuthToken, clearAuthTokens, userState } from '@marketplace/shared-utils';

const HeaderContainer = styled.header`
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 1rem 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
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
  color: #667eea;
  text-decoration: none;

  &:hover {
    color: #764ba2;
  }
`;

const NavLinks = styled.div`
  display: flex;
  gap: 2rem;
  align-items: center;
`;

const NavLink = styled(Link)<{ $active?: boolean }>`
  color: ${props => props.$active ? '#667eea' : '#333'};
  text-decoration: none;
  font-weight: ${props => props.$active ? '600' : '400'};
  transition: color 0.2s;

  &:hover {
    color: #667eea;
  }
`;

const Button = styled.button`
  background: #667eea;
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    background: #764ba2;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const UserName = styled.span`
  color: #333;
  font-weight: 500;
`;

const Navigation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isAuthenticated = !!getAuthToken();
  const currentUser = userState.get().user;

  const handleLogout = () => {
    clearAuthTokens();
    userState.clearUser();
    navigate('/users/auth/login');
  };

  const isActive = (path: string) => location.pathname === path;

  return (
    <HeaderContainer>
      <Nav>
        <Logo to="/users">Marketplace</Logo>
        
        <NavLinks>
          {!isAuthenticated ? (
            <>
              <NavLink 
                to="/users/auth/login" 
                $active={isActive('/users/auth/login')}
              >
                Login
              </NavLink>
              <NavLink 
                to="/users/auth/register" 
                $active={isActive('/users/auth/register')}
              >
                Register
              </NavLink>
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
