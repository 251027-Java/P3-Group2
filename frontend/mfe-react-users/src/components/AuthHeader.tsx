/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * AuthHeaderComponent
 * Displays user info and provides quick access to profile and logout
 */
import React, { useState } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { getAuthToken, clearAuthTokens, userState, getUserData } from '@marketplace/shared-utils';
import AuthService from '../services/AuthService';

const HeaderContainer = styled.div`
  position: relative;
`;

const AuthButton = styled.button`
  background: none;
  border: none;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: all 0.2s;
  color: #333;

  &:hover {
    background-color: rgba(102, 126, 234, 0.1);
  }
`;

const Avatar = styled.img`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
`;

const AvatarPlaceholder = styled.div`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.9rem;
  font-weight: bold;
`;

const UserName = styled.span`
  font-weight: 600;
  color: #333;

  @media (max-width: 768px) {
    display: none;
  }
`;

const DropdownArrow = styled.span<{ $open?: boolean }>`
  display: inline-block;
  transition: transform 0.2s;
  transform: ${(props) => (props.$open ? 'rotate(180deg)' : 'rotate(0)')};
`;

const DropdownMenu = styled.div<{ $open?: boolean }>`
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  opacity: ${(props) => (props.$open ? 1 : 0)};
  visibility: ${(props) => (props.$open ? 'visible' : 'hidden')};
  transform: ${(props) =>
    props.$open ? 'translateY(0)' : 'translateY(-10px)'};
  transition: all 0.2s;
  z-index: 1000;
  margin-top: 0.5rem;
`;

const DropdownItem = styled.a<{ $danger?: boolean }>`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  color: ${(props) => (props.$danger ? '#f44336' : '#333')};
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  font-size: 0.95rem;

  &:hover {
    background-color: ${(props) =>
      props.$danger ? 'rgba(244, 67, 54, 0.1)' : 'rgba(102, 126, 234, 0.1)'};
  }

  &:first-child {
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
  }

  &:last-child {
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;
  }
`;

const Divider = styled.div`
  height: 1px;
  background-color: #e0e0e0;
  margin: 0.25rem 0;
`;

const NotificationBadge = styled.span`
  background-color: #f44336;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: bold;
  position: absolute;
  top: -8px;
  right: -8px;
`;

interface UserData {
  username: string;
  email: string;
  avatar?: string;
  notificationCount?: number;
}

interface AuthHeaderComponentProps {
  user?: UserData;
  onProfileClick?: () => void;
  onLogoutClick?: () => void;
}

const AuthHeaderComponent: React.FC<AuthHeaderComponentProps> = ({
  user,
  onProfileClick,
  onLogoutClick,
}) => {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [userData, setUserData] = useState<UserData | undefined>(user);
  const navigate = useNavigate();

  React.useEffect(() => {
    // Fetch user data if not provided
    if (!userData) {
      fetchUserData();
    }

    // Listen for user state changes
    const unsubscribe = userState.subscribe(() => {
      const token = getAuthToken();
      if (!token) {
        navigate('/users/auth/login');
      }
    });

    return unsubscribe;
  }, []);

  const fetchUserData = async () => {
    try {
      // In a real app, this would fetch from the API
      // For now, we'll use mock data
      const mockUser: UserData = {
        username: 'John Doe',
        email: 'john@example.com',
        notificationCount: 2,
      };
      setUserData(mockUser);
    } catch (error) {
      console.error('Failed to fetch user data:', error);
    }
  };

  const handleLogout = () => {
    AuthService.logout();
    setDropdownOpen(false);

    if (onLogoutClick) {
      onLogoutClick();
    } else {
      navigate('/users/auth/login');
    }
  };

  const handleProfileClick = () => {
    setDropdownOpen(false);
    if (onProfileClick) {
      onProfileClick();
    } else {
      navigate('/users/profile');
    }
  };

  const handleSettingsClick = () => {
    setDropdownOpen(false);
    navigate('/users/settings');
  };

  if (!userData) {
    return null;
  }

  const initials = userData.username.substring(0, 2).toUpperCase();

  return (
    <HeaderContainer>
      <AuthButton onClick={() => setDropdownOpen(!dropdownOpen)}>
        <div style={{ position: 'relative' }}>
          {userData.avatar ? (
            <Avatar src={userData.avatar} alt={userData.username} />
          ) : (
            <AvatarPlaceholder>{initials}</AvatarPlaceholder>
          )}
          {userData.notificationCount && userData.notificationCount > 0 && (
            <NotificationBadge>{userData.notificationCount}</NotificationBadge>
          )}
        </div>
        <UserName>{userData.username}</UserName>
        <DropdownArrow $open={dropdownOpen}>▼</DropdownArrow>
      </AuthButton>

      <DropdownMenu $open={dropdownOpen}>
        <DropdownItem as="div" onClick={handleProfileClick} role="button">
          👤 My Profile
        </DropdownItem>
        <DropdownItem as="div" onClick={handleSettingsClick} role="button">
          ⚙️ Settings
        </DropdownItem>
        <Divider />
        <DropdownItem as="div" onClick={handleLogout} $danger role="button">
          🚪 Logout
        </DropdownItem>
      </DropdownMenu>
    </HeaderContainer>
  );
};

export default AuthHeaderComponent;
