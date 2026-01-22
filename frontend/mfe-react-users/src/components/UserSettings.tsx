/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * UserSettingsComponent
 * Handles user account settings including notifications, privacy, and security
 */
import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import UserService from '../services/UserService';

const SettingsContainer = styled.div`
  padding: 2rem;
  max-width: 900px;
  margin: 0 auto;
`;

const Title = styled.h1`
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
`;

const SettingsCard = styled.div`
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
`;

const CardTitle = styled.h2`
  color: #333;
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
  border-bottom: 2px solid #9F7AEA;
  padding-bottom: 0.75rem;
`;

const SettingRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
`;

const SettingLabel = styled.div``;

const SettingTitle = styled.h3`
  color: #333;
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
`;

const SettingDescription = styled.p`
  color: #999;
  margin: 0;
  font-size: 0.9rem;
`;

const Toggle = styled.input`
  width: 50px;
  height: 28px;
  cursor: pointer;
`;

const FormGroup = styled.div`
  margin-bottom: 1.5rem;

  &:last-child {
    margin-bottom: 0;
  }
`;

const Label = styled.label`
  display: block;
  color: #333;
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
`;

const Input = styled.input`
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;

  &:focus {
    outline: none;
    border-color: #9F7AEA;
    box-shadow: 0 0 0 3px rgba(159, 122, 234, 0.1);
  }

  &:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
  }
`;

const Select = styled.select`
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  background-color: white;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #9F7AEA;
    box-shadow: 0 0 0 3px rgba(159, 122, 234, 0.1);
  }
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
`;

const Button = styled.button<{ $danger?: boolean; $secondary?: boolean }>`
  padding: 0.875rem 1.5rem;
  background: ${(props) =>
    props.$danger
      ? '#f44336'
      : props.$secondary
        ? '#f0f0f0'
        : 'linear-gradient(135deg, #9F7AEA 0%, #6B46C1 100%)'};
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
        : `0 10px 20px ${props.$danger ? 'rgba(244, 67, 54, 0.3)' : 'rgba(102, 126, 234, 0.3)'}`};
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
`;

const SuccessMessage = styled.div`
  background-color: #efe;
  color: #3c3;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
`;

const ErrorMessage = styled.div`
  background-color: #fee;
  color: #c33;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
`;

const DangerZone = styled(SettingsCard)`
  border: 2px solid #f44336;
  background-color: #fff5f5;
`;

interface SettingsData {
  notificationsEmail: boolean;
  notificationsPush: boolean;
  notificationsMarketing: boolean;
  privacyProfile: 'public' | 'private' | 'friends';
  privacyActivity: 'public' | 'private' | 'friends';
  theme: 'light' | 'dark' | 'auto';
}

interface UserSettingsComponentProps {
  onSettingsChange?: (settings: Partial<SettingsData>) => void;
}

const UserSettingsComponent: React.FC<UserSettingsComponentProps> = ({
  onSettingsChange,
}) => {
  const [settings, setSettings] = useState<SettingsData>({
    notificationsEmail: true,
    notificationsPush: true,
    notificationsMarketing: false,
    privacyProfile: 'public',
    privacyActivity: 'friends',
    theme: 'light',
  });

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      const settingsData = await UserService.getSettings();
      setSettings(settingsData);
    } catch (err: any) {
      console.error('Failed to fetch settings');
    }
  };

  const handleToggle = (key: keyof SettingsData) => {
    const updatedSettings = {
      ...settings,
      [key]: !settings[key],
    };
    setSettings(updatedSettings as SettingsData);
    onSettingsChange?.(updatedSettings);
  };

  const handleSelectChange = (key: keyof SettingsData, value: string) => {
    const updatedSettings = {
      ...settings,
      [key]: value,
    };
    setSettings(updatedSettings as SettingsData);
    onSettingsChange?.(updatedSettings);
  };

  const handleSaveSettings = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await UserService.updateSettings(settings);
      setSuccess('Settings updated successfully');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save settings');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async () => {
    setError('');
    setSuccess('');

    if (!passwordData.currentPassword) {
      setError('Current password is required');
      return;
    }
    if (!passwordData.newPassword) {
      setError('New password is required');
      return;
    }
    if (passwordData.newPassword.length < 8) {
      setError('New password must be at least 8 characters');
      return;
    }
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);
    try {
      await UserService.changePassword({
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      });
      setSuccess('Password changed successfully');
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setTimeout(() => setSuccess(''), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (
      !window.confirm(
        'Are you sure you want to delete your account? This cannot be undone.'
      )
    ) {
      return;
    }

    setLoading(true);
    try {
      await UserService.deleteAccount();
      window.location.href = '/users/auth/login';
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete account');
      setLoading(false);
    }
  };

  return (
    <SettingsContainer>
      <Title>Settings</Title>

      {success && <SuccessMessage>{success}</SuccessMessage>}
      {error && <ErrorMessage>{error}</ErrorMessage>}

      {/* Notification Settings */}
      <SettingsCard>
        <CardTitle>Notification Preferences</CardTitle>

        <SettingRow>
          <SettingLabel>
            <SettingTitle>Email Notifications</SettingTitle>
            <SettingDescription>Receive email updates about your account</SettingDescription>
          </SettingLabel>
          <Toggle
            type="checkbox"
            checked={settings.notificationsEmail}
            onChange={() => handleToggle('notificationsEmail')}
          />
        </SettingRow>

        <SettingRow>
          <SettingLabel>
            <SettingTitle>Push Notifications</SettingTitle>
            <SettingDescription>Receive push notifications on your device</SettingDescription>
          </SettingLabel>
          <Toggle
            type="checkbox"
            checked={settings.notificationsPush}
            onChange={() => handleToggle('notificationsPush')}
          />
        </SettingRow>

        <SettingRow>
          <SettingLabel>
            <SettingTitle>Marketing Emails</SettingTitle>
            <SettingDescription>Receive promotional offers and newsletters</SettingDescription>
          </SettingLabel>
          <Toggle
            type="checkbox"
            checked={settings.notificationsMarketing}
            onChange={() => handleToggle('notificationsMarketing')}
          />
        </SettingRow>
      </SettingsCard>

      {/* Privacy Settings */}
      <SettingsCard>
        <CardTitle>Privacy Settings</CardTitle>

        <FormGroup>
          <Label>Profile Visibility</Label>
          <Select
            value={settings.privacyProfile}
            onChange={(e) =>
              handleSelectChange('privacyProfile', e.target.value)
            }
          >
            <option value="public">Public</option>
            <option value="friends">Friends Only</option>
            <option value="private">Private</option>
          </Select>
        </FormGroup>

        <FormGroup>
          <Label>Activity Visibility</Label>
          <Select
            value={settings.privacyActivity}
            onChange={(e) =>
              handleSelectChange('privacyActivity', e.target.value)
            }
          >
            <option value="public">Public</option>
            <option value="friends">Friends Only</option>
            <option value="private">Private</option>
          </Select>
        </FormGroup>

        <FormGroup>
          <Label>Theme</Label>
          <Select
            value={settings.theme}
            onChange={(e) => handleSelectChange('theme', e.target.value)}
          >
            <option value="light">Light</option>
            <option value="dark">Dark</option>
            <option value="auto">Auto (System)</option>
          </Select>
        </FormGroup>

        <ButtonGroup>
          <Button onClick={handleSaveSettings} disabled={loading}>
            {loading ? 'Saving...' : 'Save Settings'}
          </Button>
        </ButtonGroup>
      </SettingsCard>

      {/* Security Settings */}
      <SettingsCard>
        <CardTitle>Security</CardTitle>

        <FormGroup>
          <Label htmlFor="currentPassword">Current Password</Label>
          <Input
            id="currentPassword"
            type="password"
            placeholder="Enter your current password"
            value={passwordData.currentPassword}
            onChange={(e) =>
              setPasswordData({
                ...passwordData,
                currentPassword: e.target.value,
              })
            }
            disabled={loading}
          />
        </FormGroup>

        <FormGroup>
          <Label htmlFor="newPassword">New Password</Label>
          <Input
            id="newPassword"
            type="password"
            placeholder="Enter your new password"
            value={passwordData.newPassword}
            onChange={(e) =>
              setPasswordData({
                ...passwordData,
                newPassword: e.target.value,
              })
            }
            disabled={loading}
          />
        </FormGroup>

        <FormGroup>
          <Label htmlFor="confirmPassword">Confirm Password</Label>
          <Input
            id="confirmPassword"
            type="password"
            placeholder="Confirm your new password"
            value={passwordData.confirmPassword}
            onChange={(e) =>
              setPasswordData({
                ...passwordData,
                confirmPassword: e.target.value,
              })
            }
            disabled={loading}
          />
        </FormGroup>

        <ButtonGroup>
          <Button onClick={handleChangePassword} disabled={loading}>
            {loading ? 'Changing...' : 'Change Password'}
          </Button>
        </ButtonGroup>
      </SettingsCard>

      {/* Danger Zone */}
      <DangerZone>
        <CardTitle style={{ borderBottomColor: '#f44336' }}>
          Danger Zone
        </CardTitle>

        <SettingRow>
          <SettingLabel>
            <SettingTitle>Delete Account</SettingTitle>
            <SettingDescription>
              Permanently delete your account and all associated data
            </SettingDescription>
          </SettingLabel>
        </SettingRow>

        <ButtonGroup>
          <Button $danger onClick={handleDeleteAccount} disabled={loading}>
            {loading ? 'Deleting...' : 'Delete Account'}
          </Button>
        </ButtonGroup>
      </DangerZone>
    </SettingsContainer>
  );
};

export default UserSettingsComponent;
