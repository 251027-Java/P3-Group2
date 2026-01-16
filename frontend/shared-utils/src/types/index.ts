/**
 * This file was created by Claude Sonnet 4.5
 * 
 * Shared TypeScript Types and Interfaces
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

// User Types
export interface User {
  id: string;
  username: string;
  email: string;
  role: UserRole;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export type UserRole = "USER" | "ADMIN" | "MODERATOR";

export interface AuthToken {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

// Card Types
export interface Card {
  id: string;
  name: string;
  description: string;
  imageUrl: string;
  cardType: CardType;
  rarity: CardRarity;
  setName: string;
  setCode: string;
  price?: number;
  stockQuantity?: number;
  createdAt: string;
  updatedAt: string;
}

export type CardType =
  | "POKEMON"
  | "MAGIC"
  | "YUGIOH"
  | "SPORTS"
  | "OTHER";

export type CardRarity =
  | "COMMON"
  | "UNCOMMON"
  | "RARE"
  | "ULTRA_RARE"
  | "SECRET_RARE";

export interface CardSearchParams {
  query?: string;
  cardType?: CardType;
  rarity?: CardRarity;
  minPrice?: number;
  maxPrice?: number;
  setName?: string;
  page?: number;
  size?: number;
  sort?: string;
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface ApiError {
  message: string;
  status: number;
  error: string;
  path: string;
  timestamp: string;
  details?: Record<string, string[]>;
}

// Cart Types
export interface CartItem {
  cardId: string;
  card: Card;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  itemCount: number;
  total: number;
  updatedAt: string;
}

// Event Types
export interface CustomEventData {
  type: string;
  payload: unknown;
  timestamp: string;
  source?: string;
}

export interface UserLoginEvent {
  userId: string;
  username: string;
  email: string;
  role: UserRole;
  token: string;
}

export interface UserLogoutEvent {
  userId: string;
  reason?: string;
}

export interface CartUpdatedEvent {
  itemCount: number;
  total: number;
  items: CartItem[];
}

export interface NavigationChangeEvent {
  from: string;
  to: string;
  timestamp: string;
}

export interface ThemeChangedEvent {
  theme: "light" | "dark";
}
