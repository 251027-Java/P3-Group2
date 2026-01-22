/**
 * TradeService
 * Handles all trade-related API calls
 */
import apiClient from './apiClient';

export interface Trade {
  tradeId: string;
  initiatorUserId: string;
  receiverUserId: string;
  initiatorCardId: string;
  receiverCardId: string;
  tradeStatus: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';
  createdAt: string;
  updatedAt: string;
}

export interface CreateTradeRequest {
  receiverUserId: string;
  initiatorCardId: string;
  receiverCardId: string;
}

export interface UpdateTradeStatusRequest {
  status: 'ACCEPTED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';
}

class TradeService {
  /**
   * Get all trades for a user
   */
  async getUserTrades(userId: string): Promise<Trade[]> {
    try {
      const response = await apiClient.get<Trade[]>(`/api/trades/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user trades:', error);
      return [];
    }
  }

  /**
   * Get trade by ID
   */
  async getTradeById(tradeId: string): Promise<Trade> {
    try {
      const response = await apiClient.get<Trade>(`/api/trades/${tradeId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching trade:', error);
      throw error;
    }
  }

  /**
   * Create a new trade
   */
  async createTrade(data: CreateTradeRequest): Promise<Trade> {
    try {
      const response = await apiClient.post<Trade>('/api/trades', data);
      return response.data;
    } catch (error) {
      console.error('Error creating trade:', error);
      throw error;
    }
  }

  /**
   * Update trade status
   */
  async updateTradeStatus(tradeId: string, data: UpdateTradeStatusRequest): Promise<Trade> {
    try {
      const response = await apiClient.put<Trade>(`/api/trades/${tradeId}/status`, data);
      return response.data;
    } catch (error) {
      console.error('Error updating trade status:', error);
      throw error;
    }
  }

  /**
   * Delete a trade
   */
  async deleteTrade(tradeId: string): Promise<void> {
    try {
      await apiClient.delete(`/api/trades/${tradeId}`);
    } catch (error) {
      console.error('Error deleting trade:', error);
      throw error;
    }
  }
}

export default new TradeService();
