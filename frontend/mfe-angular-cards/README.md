<!-- This file was created by Claude Sonnet 4.5 -->

# Angular MFE - Card Marketplace

Angular micro-frontend for the card marketplace features.

## Overview

This MFE handles:
- Card browsing and listing
- Card details and information
- Search and filtering functionality
- Shopping cart integration

## Technology Stack

- **Framework**: Angular 17+
- **Single-SPA**: Micro-frontend wrapper
- **UI Library**: Angular Material
- **State Management**: NgRx or Angular Services
- **HTTP Client**: Angular HttpClient
- **Styling**: SCSS with shared design tokens

## Port

Development port: `4200`

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

The application will be available at `http://localhost:4200`.

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

# Run E2E tests
npm run e2e
```

## Routes

- `/cards` - Card listing/browse page
- `/cards/:id` - Card details page
- `/cards/search` - Search and filter page

## Components

1. **CardListComponent** - Display grid/list of cards
2. **CardItemComponent** - Individual card display
3. **CardDetailsComponent** - Full card information
4. **SearchFilterComponent** - Search and filter controls
5. **CardHeaderComponent** - Navigation bar
6. **LoadingSpinnerComponent** - Loading indicators
7. **ErrorMessageComponent** - Error display

## API Integration

Consumes the Card Service API:
- `GET /api/v1/cards` - List cards
- `GET /api/v1/cards/:id` - Get card details
- `GET /api/v1/cards/search` - Search cards

## Development Status

🚧 **In Progress** - Phase 2 of frontend development

## AI Attribution

This project was developed with assistance from GitHub Copilot. All AI-generated code has been reviewed and modified by the development team.
