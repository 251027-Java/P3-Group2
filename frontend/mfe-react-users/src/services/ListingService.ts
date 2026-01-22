/**
 * ListingService
 * Handles all listing-related API calls
 */
import apiClient from './apiClient';

export interface Listing {
  listingId: string;
  userId: string;
  cardId: string;
  price: number;
  listingStatus: 'ACTIVE' | 'SOLD' | 'INACTIVE' | 'PENDING';
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateListingRequest {
  cardId: string;
  price: number;
  description?: string;
}

export interface UpdateListingRequest {
  price?: number;
  description?: string;
  listingStatus?: 'ACTIVE' | 'SOLD' | 'INACTIVE';
}

class ListingService {
  /**
   * Get all listings for a user
   */
  async getUserListings(userId: string): Promise<Listing[]> {
    try {
      const response = await apiClient.get<Listing[]>(`/api/listings/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user listings:', error);
      return [];
    }
  }

  /**
   * Get all active listings
   */
  async getAllListings(): Promise<Listing[]> {
    try {
      const response = await apiClient.get<Listing[]>('/api/listings');
      return response.data;
    } catch (error) {
      console.error('Error fetching listings:', error);
      return [];
    }
  }

  /**
   * Get listing by ID
   */
  async getListingById(listingId: string): Promise<Listing> {
    try {
      const response = await apiClient.get<Listing>(`/api/listings/${listingId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching listing:', error);
      throw error;
    }
  }

  /**
   * Create a new listing
   */
  async createListing(data: CreateListingRequest): Promise<Listing> {
    try {
      const response = await apiClient.post<Listing>('/api/listings', data);
      return response.data;
    } catch (error) {
      console.error('Error creating listing:', error);
      throw error;
    }
  }

  /**
   * Update listing
   */
  async updateListing(listingId: string, data: UpdateListingRequest): Promise<Listing> {
    try {
      const response = await apiClient.put<Listing>(`/api/listings/${listingId}`, data);
      return response.data;
    } catch (error) {
      console.error('Error updating listing:', error);
      throw error;
    }
  }

  /**
   * Delete a listing
   */
  async deleteListing(listingId: string): Promise<void> {
    try {
      await apiClient.delete(`/api/listings/${listingId}`);
    } catch (error) {
      console.error('Error deleting listing:', error);
      throw error;
    }
  }
}

export default new ListingService();
