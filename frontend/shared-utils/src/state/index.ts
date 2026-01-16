/**
 * This file was created by Claude Sonnet 4.5
 *
 * Shared State Management
 * Simple state management for cross-MFE communication
 * Generated with assistance from GitHub Copilot
 * Reviewed and modified by Development Team
 */

import { emitEvent, EVENT_TYPES } from "../events";
import { User, Cart } from "../types";

/**
 * Type for state change listeners
 */
type StateListener<T> = (newState: T, oldState: T) => void;

/**
 * Shared state manager class
 */
class StateManager<T> {
  private state: T;
  private listeners: Set<StateListener<T>> = new Set();

  constructor(initialState: T) {
    this.state = initialState;
  }

  /**
   * Get current state
   */
  getState(): T {
    return this.state;
  }

  /**
   * Set new state and notify listeners
   */
  setState(newState: Partial<T>): void {
    const oldState = this.state;
    this.state = { ...this.state, ...newState };
    this.notifyListeners(this.state, oldState);
  }

  /**
   * Subscribe to state changes
   */
  subscribe(listener: StateListener<T>): () => void {
    this.listeners.add(listener);

    // Return unsubscribe function
    return () => {
      this.listeners.delete(listener);
    };
  }

  /**
   * Notify all listeners of state change
   */
  private notifyListeners(newState: T, oldState: T): void {
    this.listeners.forEach((listener) => {
      try {
        listener(newState, oldState);
      } catch (error) {
        console.error("Error in state listener:", error);
      }
    });
  }

  /**
   * Clear all listeners
   */
  clearListeners(): void {
    this.listeners.clear();
  }
}

/**
 * User state interface
 */
interface UserState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

/**
 * Cart state interface
 */
interface CartState {
  cart: Cart | null;
  itemCount: number;
  total: number;
}

// Create global state managers
const userStateManager = new StateManager<UserState>({
  user: null,
  isAuthenticated: false,
  isLoading: false,
});

const cartStateManager = new StateManager<CartState>({
  cart: null,
  itemCount: 0,
  total: 0,
});

/**
 * User State Management
 */
export const userState = {
  /**
   * Get current user state
   */
  get: () => userStateManager.getState(),

  /**
   * Set user (after login)
   */
  setUser: (user: User) => {
    userStateManager.setState({
      user,
      isAuthenticated: true,
      isLoading: false,
    });
    emitEvent(EVENT_TYPES.USER_LOGIN, user);
  },

  /**
   * Clear user (after logout)
   */
  clearUser: () => {
    const currentUser = userStateManager.getState().user;
    userStateManager.setState({
      user: null,
      isAuthenticated: false,
      isLoading: false,
    });
    emitEvent(EVENT_TYPES.USER_LOGOUT, {
      userId: currentUser?.id || "",
      reason: "logout",
    });
  },

  /**
   * Update user data
   */
  updateUser: (updates: Partial<User>) => {
    const currentState = userStateManager.getState();
    if (currentState.user) {
      const updatedUser = { ...currentState.user, ...updates };
      userStateManager.setState({ user: updatedUser });
      emitEvent(EVENT_TYPES.USER_UPDATED, updatedUser);
    }
  },

  /**
   * Set loading state
   */
  setLoading: (isLoading: boolean) => {
    userStateManager.setState({ isLoading });
  },

  /**
   * Subscribe to user state changes
   */
  subscribe: (listener: StateListener<UserState>) =>
    userStateManager.subscribe(listener),
};

/**
 * Cart State Management
 */
export const cartState = {
  /**
   * Get current cart state
   */
  get: () => cartStateManager.getState(),

  /**
   * Set cart
   */
  setCart: (cart: Cart) => {
    cartStateManager.setState({
      cart,
      itemCount: cart.itemCount,
      total: cart.total,
    });
    emitEvent(EVENT_TYPES.CART_UPDATED, {
      itemCount: cart.itemCount,
      total: cart.total,
      items: cart.items,
    });
  },

  /**
   * Update cart item count
   */
  updateItemCount: (itemCount: number) => {
    const currentState = cartStateManager.getState();
    cartStateManager.setState({ itemCount });
    if (currentState.cart) {
      emitEvent(EVENT_TYPES.CART_UPDATED, {
        itemCount,
        total: currentState.total,
        items: currentState.cart.items,
      });
    }
  },

  /**
   * Clear cart
   */
  clearCart: () => {
    cartStateManager.setState({
      cart: null,
      itemCount: 0,
      total: 0,
    });
    emitEvent(EVENT_TYPES.CART_UPDATED, {
      itemCount: 0,
      total: 0,
      items: [],
    });
  },

  /**
   * Subscribe to cart state changes
   */
  subscribe: (listener: StateListener<CartState>) =>
    cartStateManager.subscribe(listener),
};

/**
 * Initialize shared state from storage
 */
export function initializeSharedState(): void {
  // Load user from storage if exists
  const storedUser = localStorage.getItem("marketplace_user_data");
  if (storedUser) {
    try {
      const user: User = JSON.parse(storedUser);
      userState.setUser(user);
    } catch (error) {
      console.error("Failed to load user from storage:", error);
    }
  }

  // Load cart from storage if exists
  const storedCart = localStorage.getItem("marketplace_cart");
  if (storedCart) {
    try {
      const cart: Cart = JSON.parse(storedCart);
      cartState.setCart(cart);
    } catch (error) {
      console.error("Failed to load cart from storage:", error);
    }
  }
}

/**
 * Clear all shared state
 */
export function clearSharedState(): void {
  userState.clearUser();
  cartState.clearCart();
}
