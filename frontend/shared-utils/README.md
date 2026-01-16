<!-- This file was created by Claude Sonnet 4.5 -->

# Shared Utilities

Shared utilities, design tokens, and common functionality for all micro-frontends.

## Overview

This package provides:
- Design tokens (colors, typography, spacing)
- Custom event system for inter-MFE communication
- Shared TypeScript types and interfaces
- Common utilities and helper functions
- Shared authentication utilities
- API configuration

## Technology Stack

- **TypeScript**: Type definitions and utilities
- **CSS Variables**: Design tokens
- **Event System**: Custom events for MFE communication

## Installation

This package is intended to be used by all MFEs in the monorepo.

```bash
npm install
```

## Usage

### Design Tokens

```typescript
import { colors, typography, spacing } from '@marketplace/shared-utils';

const primaryColor = colors.primary;
const baseFont = typography.fontFamily.base;
const spacing1 = spacing.unit * 1;
```

### Custom Events

```typescript
import { emitEvent, onEvent } from '@marketplace/shared-utils';

// Emit event
emitEvent('user:login', { userId: '123', username: 'john' });

// Listen to event
onEvent('user:login', (data) => {
  console.log('User logged in:', data);
});
```

### Shared Types

```typescript
import { User, Card, ApiResponse } from '@marketplace/shared-utils';

const user: User = {
  id: '123',
  username: 'john',
  email: 'john@example.com',
  role: 'USER'
};
```

## Design Tokens

### Colors

```css
:root {
  /* Primary Colors */
  --color-primary: #3498db;
  --color-primary-dark: #2980b9;
  --color-primary-light: #5dade2;
  
  /* Secondary Colors */
  --color-secondary: #2ecc71;
  --color-secondary-dark: #27ae60;
  --color-secondary-light: #58d68d;
  
  /* Neutral Colors */
  --color-gray-100: #f8f9fa;
  --color-gray-200: #e9ecef;
  --color-gray-300: #dee2e6;
  --color-gray-400: #ced4da;
  --color-gray-500: #adb5bd;
  --color-gray-600: #6c757d;
  --color-gray-700: #495057;
  --color-gray-800: #343a40;
  --color-gray-900: #212529;
  
  /* Semantic Colors */
  --color-success: #28a745;
  --color-warning: #ffc107;
  --color-error: #dc3545;
  --color-info: #17a2b8;
}
```

### Typography

```css
:root {
  /* Font Families */
  --font-family-base: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  --font-family-heading: 'Helvetica Neue', Arial, sans-serif;
  --font-family-mono: 'Monaco', 'Courier New', monospace;
  
  /* Font Sizes */
  --font-size-xs: 0.75rem;    /* 12px */
  --font-size-sm: 0.875rem;   /* 14px */
  --font-size-base: 1rem;     /* 16px */
  --font-size-lg: 1.125rem;   /* 18px */
  --font-size-xl: 1.25rem;    /* 20px */
  --font-size-2xl: 1.5rem;    /* 24px */
  --font-size-3xl: 1.875rem;  /* 30px */
  --font-size-4xl: 2.25rem;   /* 36px */
  
  /* Font Weights */
  --font-weight-light: 300;
  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
  
  /* Line Heights */
  --line-height-tight: 1.25;
  --line-height-normal: 1.5;
  --line-height-relaxed: 1.75;
}
```

### Spacing

```css
:root {
  /* Spacing Scale */
  --spacing-0: 0;
  --spacing-1: 0.25rem;   /* 4px */
  --spacing-2: 0.5rem;    /* 8px */
  --spacing-3: 0.75rem;   /* 12px */
  --spacing-4: 1rem;      /* 16px */
  --spacing-5: 1.25rem;   /* 20px */
  --spacing-6: 1.5rem;    /* 24px */
  --spacing-8: 2rem;      /* 32px */
  --spacing-10: 2.5rem;   /* 40px */
  --spacing-12: 3rem;     /* 48px */
  --spacing-16: 4rem;     /* 64px */
  --spacing-20: 5rem;     /* 80px */
  --spacing-24: 6rem;     /* 96px */
}
```

### Breakpoints

```css
:root {
  --breakpoint-xs: 320px;
  --breakpoint-sm: 576px;
  --breakpoint-md: 768px;
  --breakpoint-lg: 1024px;
  --breakpoint-xl: 1280px;
  --breakpoint-2xl: 1536px;
}
```

## Event System

### Standard Events

- `user:login` - User has logged in
- `user:logout` - User has logged out
- `user:updated` - User profile has been updated
- `cart:updated` - Shopping cart has been updated
- `navigation:change` - Navigation has changed
- `theme:changed` - Theme has been changed

### Event Payload Types

```typescript
interface UserLoginEvent {
  userId: string;
  username: string;
  email: string;
  role: string;
  token: string;
}

interface CartUpdatedEvent {
  itemCount: number;
  total: number;
}

interface NavigationChangeEvent {
  from: string;
  to: string;
}
```

## Building

```bash
npm run build
```

## Port

Development port: `9001`

## Development Status

🚧 **Planned** - Phase 1 of frontend development

## AI Attribution

This project was developed with assistance from GitHub Copilot. All AI-generated code has been reviewed and modified by the development team.
