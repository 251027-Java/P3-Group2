/**
 * CardService
 * Handles all card-related API calls
 */
import apiClient from './apiClient';

export interface Card {
  cardId: string;
  name: string;
  type: string;
  rarity: string;
  hp?: number;
  attack?: number;
  defense?: number;
  description?: string;
  imageUrl?: string;
  createdAt?: string;
}

export interface UserCard {
  userCardId: string;
  userId: string;
  cardId: string;
  quantity: number;
  acquiredAt: string;
  card?: Card;
}

class CardService {
  /**
   * Get all cards
   */
  async getAllCards(): Promise<Card[]> {
    try {
      const response = await apiClient.get<Card[]>('/api/cards');
      return response.data;
    } catch (error) {
      console.error('Error fetching cards:', error);
      return [];
    }
  }

  /**
   * Get card by ID
   */
  async getCardById(cardId: string): Promise<Card> {
    try {
      const response = await apiClient.get<Card>(`/api/cards/${cardId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching card:', error);
      throw error;
    }
  }

  /**
   * Get user's cards
   */
  async getUserCards(userId: string): Promise<UserCard[]> {
    try {
      const response = await apiClient.get<UserCard[]>(`/api/cards/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user cards:', error);
      return [];
    }
  }

  /**
   * Search cards by name
   */
  async searchCards(query: string): Promise<Card[]> {
    try {
      const response = await apiClient.get<Card[]>(`/api/cards/search?q=${encodeURIComponent(query)}`);
      return response.data;
    } catch (error) {
      console.error('Error searching cards:', error);
      return [];
    }
  }

  /**
   * Filter cards by type
   */
  async filterCardsByType(type: string): Promise<Card[]> {
    try {
      const response = await apiClient.get<Card[]>(`/api/cards/filter?type=${encodeURIComponent(type)}`);
      return response.data;
    } catch (error) {
      console.error('Error filtering cards:', error);
      return [];
    }
  }
}

export default new CardService();
