/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * UserProfileComponent
 * Displays user profile information with edit functionality
 */
import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import UserService from '../services/UserService';

const ProfileContainer = styled.div`
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
`;

const ProfileHeader = styled.div`
  display: flex;
  align-items: flex-start;
  gap: 2rem;
  margin-bottom: 3rem;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
`;

const AvatarContainer = styled.div`
  position: relative;
`;

const Avatar = styled.img`
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #667eea;
`;

const AvatarPlaceholder = styled.div`
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 3rem;
  font-weight: bold;
`;

const AvatarUploadLabel = styled.label`
  position: absolute;
  bottom: 0;
  right: 0;
  background: #667eea;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.2rem;
  border: 3px solid white;

  &:hover {
    background: #764ba2;
  }

  input {
    display: none;
  }
`;

const UserInfo = styled.div`
  flex: 1;
`;

const UserName = styled.h1`
  color: #333;
  margin: 0 0 0.5rem 0;
  font-size: 1.8rem;
`;

const UserEmail = styled.p`
  color: #666;
  margin: 0 0 1rem 0;
  font-size: 1rem;
`;

const UserStats = styled.div`
  display: flex;
  gap: 2rem;
  margin-top: 1.5rem;

  @media (max-width: 768px) {
    justify-content: center;
  }
`;

const StatItem = styled.div`
  text-align: center;
`;

const StatValue = styled.div`
  font-size: 1.5rem;
  font-weight: bold;
  color: #667eea;
`;

const StatLabel = styled.div`
  font-size: 0.85rem;
  color: #999;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;

  @media (max-width: 768px) {
    justify-content: center;
    flex-wrap: wrap;
  }
`;

const Button = styled.button<{ $secondary?: boolean }>`
  padding: 0.75rem 1.5rem;
  background: ${(props) =>
    props.$secondary ? '#f0f0f0' : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'};
  color: ${(props) => (props.$secondary ? '#333' : 'white')};
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: ${(props) =>
      props.$secondary
        ? 'none'
        : '0 10px 20px rgba(102, 126, 234, 0.3)'};
  }
`;

const ProfileContent = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

const Card = styled.div`
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

const CardTitle = styled.h3`
  color: #333;
  margin: 0 0 1.5rem 0;
  font-size: 1.2rem;
  border-bottom: 2px solid #667eea;
  padding-bottom: 0.75rem;
`;

const InfoRow = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
`;

const InfoLabel = styled.span`
  color: #666;
  font-weight: 500;
`;

const InfoValue = styled.span`
  color: #333;
  font-weight: 600;
`;

const ActivityList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const ActivityItem = styled.div`
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid #667eea;
`;

const ActivityTime = styled.p`
  color: #999;
  font-size: 0.85rem;
  margin: 0;
`;

const ActivityText = styled.p`
  color: #333;
  margin: 0.25rem 0 0 0;
`;

const LoadingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  color: #666;
`;

interface UserProfileData {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  joinDate: string;
  lastLogin: string;
  role: string;
  activity?: Array<{
    action: string;
    timestamp: string;
  }>;
}

interface UserProfileComponentProps {
  onEditClick?: () => void;
}

const UserProfileComponent: React.FC<UserProfileComponentProps> = ({ onEditClick }) => {
  const [profile, setProfile] = useState<UserProfileData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const profileData = await UserService.getProfile();
      setProfile(profileData);
      setError('');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleAvatarUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      const response = await UserService.uploadAvatar(file);
      setProfile((prev) => prev ? { ...prev, avatar: response.avatarUrl } : null);
    } catch (err) {
      console.error('Failed to upload avatar');
    }
  };

  if (loading) {
    return <LoadingContainer>Loading profile...</LoadingContainer>;
  }

  if (!profile) {
    return <LoadingContainer>No profile data available</LoadingContainer>;
  }

  const initials = profile.username.substring(0, 2).toUpperCase();
  const joinDate = new Date(profile.joinDate).toLocaleDateString();
  const lastLogin = new Date(profile.lastLogin).toLocaleDateString();

  return (
    <ProfileContainer>
      <ProfileHeader>
        <AvatarContainer>
          {profile.avatar ? (
            <Avatar src={profile.avatar} alt={profile.username} />
          ) : (
            <AvatarPlaceholder>{initials}</AvatarPlaceholder>
          )}
          <AvatarUploadLabel>
            📷
            <input
              type="file"
              accept="image/*"
              onChange={handleAvatarUpload}
            />
          </AvatarUploadLabel>
        </AvatarContainer>

        <UserInfo>
          <UserName>{profile.username}</UserName>
          <UserEmail>{profile.email}</UserEmail>
          <UserStats>
            <StatItem>
              <StatValue>{profile.role}</StatValue>
              <StatLabel>Account Type</StatLabel>
            </StatItem>
            <StatItem>
              <StatValue>{joinDate}</StatValue>
              <StatLabel>Member Since</StatLabel>
            </StatItem>
          </UserStats>
          <ButtonGroup>
            <Button onClick={onEditClick}>Edit Profile</Button>
            <Button $secondary as="a" href="/users/settings">
              Settings
            </Button>
          </ButtonGroup>
        </UserInfo>
      </ProfileHeader>

      <ProfileContent>
        <Card>
          <CardTitle>Account Information</CardTitle>
          <InfoRow>
            <InfoLabel>Username</InfoLabel>
            <InfoValue>{profile.username}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>Email</InfoLabel>
            <InfoValue>{profile.email}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>Role</InfoLabel>
            <InfoValue>{profile.role}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>Member Since</InfoLabel>
            <InfoValue>{joinDate}</InfoValue>
          </InfoRow>
          <InfoRow>
            <InfoLabel>Last Login</InfoLabel>
            <InfoValue>{lastLogin}</InfoValue>
          </InfoRow>
        </Card>

        <Card>
          <CardTitle>Recent Activity</CardTitle>
          <ActivityList>
            {profile.activity && profile.activity.length > 0 ? (
              profile.activity.map((item, idx) => (
                <ActivityItem key={idx}>
                  <ActivityText>{item.action}</ActivityText>
                  <ActivityTime>{new Date(item.timestamp).toLocaleString()}</ActivityTime>
                </ActivityItem>
              ))
            ) : (
              <p style={{ color: '#999' }}>No recent activity</p>
            )}
          </ActivityList>
        </Card>
      </ProfileContent>
    </ProfileContainer>
  );
};

export default UserProfileComponent;
