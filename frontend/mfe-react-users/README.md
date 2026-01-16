<!-- This file was created by Claude Sonnet 4.5 -->

# React MFE - User Management

React micro-frontend for user authentication and profile management.

## Overview

This MFE handles:
- User authentication (login/register)
- User profile management
- Account settings
- Session management
- JWT token handling

## Technology Stack

- **Framework**: React 18+
- **Single-SPA**: Micro-frontend wrapper
- **Routing**: React Router v6
- **State Management**: React Context / Redux
- **HTTP Client**: Axios
- **Styling**: Styled Components / CSS Modules
- **Forms**: React Hook Form
- **Validation**: Yup

## Port

Development port: `8080`

## Getting Started

### Prerequisites

- Node.js 18+
- npm

### Installation

```bash
npm install
```

### Running Locally

```bash
npm start
```

The application will be available at `http://localhost:8080`.

### Building

```bash
npm run build
```

### Testing

```bash
# Run unit tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

## Routes

- `/auth` - Login and registration page
- `/profile` - User profile page
- `/settings` - User settings page

## Components

1. **LoginFormComponent** - Login form with validation
2. **RegisterFormComponent** - Registration form
3. **UserProfileComponent** - User profile display
4. **UserSettingsComponent** - Account settings
5. **AuthHeaderComponent** - User menu and avatar
6. **ProtectedRouteComponent** - Route guard wrapper
7. **FormInputComponent** - Reusable form input

## Authentication Flow

- JWT token-based authentication
- Token stored in localStorage/sessionStorage
- Automatic token refresh
- Role-based access control (USER, ADMIN)
- Session timeout handling

## API Integration

Consumes the User/Auth Service API:
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/logout` - User logout
- `GET /api/v1/users/profile` - Get user profile
- `PUT /api/v1/users/profile` - Update user profile

## Security

- Input sanitization (XSS prevention)
- CSRF protection
- Secure password requirements
- Token security best practices
- Rate limiting feedback

## Development Status

🚧 **Planned** - Phase 3 of frontend development

## AI Attribution

This project was developed with assistance from GitHub Copilot. All AI-generated code has been reviewed and modified by the development team.
