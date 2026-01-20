/**
 * This file was created by Claude Haiku 4.5
 */

/**
 * UserService
 * Handles user profile and settings API calls
 */
import apiClient from './apiClient';
import { hasRole } from '@marketplace/shared-utils';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  role: string;
  joinDate: string;
  lastLogin: string;
  activity?: Array<{
    action: string;
    timestamp: string;
  }>;
}

export interface UpdateProfileRequest {
  username?: string;
  email?: string;
  avatar?: string;
}

export interface UserSettings {
  notificationsEmail: boolean;
  notificationsPush: boolean;
  notificationsMarketing: boolean;
  privacyProfile: 'public' | 'private' | 'friends';
  privacyActivity: 'public' | 'private' | 'friends';
  theme: 'light' | 'dark' | 'auto';
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

class UserService {
  /**
   * Get user profile
   */
  async getProfile(): Promise<UserProfile> {
    try {
      const response = await apiClient.get<UserProfile>('/users/profile');
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Update user profile
   */
  async updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
    try {
      const response = await apiClient.put<UserProfile>('/users/profile', data);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Upload user avatar
   */
  async uploadAvatar(file: File): Promise<{ avatarUrl: string }> {
    try {
      const formData = new FormData();
      formData.append('avatar', file);

      const response = await apiClient.post<{ avatarUrl: string }>(
        '/users/profile/avatar',
        formData,
        {
          headers: { 'Content-Type': 'multipart/form-data' },
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Get user settings
   */
  async getSettings(): Promise<UserSettings> {
    try {
      const response = await apiClient.get<UserSettings>('/users/settings');
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Update user settings
   */
  async updateSettings(settings: Partial<UserSettings>): Promise<UserSettings> {
    try {
      const response = await apiClient.put<UserSettings>('/users/settings', settings);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Change password
   */
  async changePassword(data: ChangePasswordRequest): Promise<{ message: string }> {
    try {
      const response = await apiClient.post<{ message: string }>(
        '/users/change-password',
        data
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Delete account
   */
  async deleteAccount(): Promise<{ message: string }> {
    try {
      const response = await apiClient.delete<{ message: string }>('/users/account');
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  /**
   * Check if user has a specific role
   */
  hasRole(role: string): boolean {
    return hasRole(role);
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Check if user is moderator
   */
  isModerator(): boolean {
    return this.hasRole('MODERATOR');
  }

  /**
   * Get user's authorization level
   */
  getAuthorizationLevel(): 'ADMIN' | 'MODERATOR' | 'USER' | null {
    if (this.isAdmin()) return 'ADMIN';
    if (this.isModerator()) return 'MODERATOR';
    return 'USER';
  }

  /**
   * Check if user can perform a specific action based on role
   */
  canPerformAction(action: string, requiredRole?: string): boolean {
    if (requiredRole) {
      return this.hasRole(requiredRole);
    }

    // Default actions for any authenticated user
    const publicActions = ['view_profile', 'edit_profile', 'view_cards'];

    if (publicActions.includes(action)) {
      return true;
    }

    // Moderator actions
    if (this.isModerator()) {
      const moderatorActions = ['moderate_comments', 'flag_content'];
      if (moderatorActions.includes(action)) {
        return true;
      }
    }

    // Admin actions
    if (this.isAdmin()) {
      return true; // Admins can do anything
    }

    return false;
  }
}

export default new UserService();
