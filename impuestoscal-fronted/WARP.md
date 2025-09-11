# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**impuestoscal-fronted** is an Angular 20 application with Server-Side Rendering (SSR) support, built for tax calculation functionality. The project uses Tailwind CSS v4 for styling and follows Angular's standalone component architecture.

## Technology Stack

- **Framework**: Angular 20.2.x with standalone components
- **Styling**: Tailwind CSS v4.1.13 with PostCSS
- **Testing**: Jasmine + Karma
- **Build System**: Angular CLI with esbuild
- **Server**: Express.js for SSR
- **Language**: TypeScript with strict mode enabled

## Architecture

### Project Structure

```
src/
├── app/
│   ├── auth/           # Authentication components
│   │   ├── login/      # Login component
│   │   └── register/   # Registration component
│   ├── dashboard/      # Dashboard-related components
│   │   └── home/       # Home dashboard component
│   └── reu/           # Reusable UI components
│       └── navbar/     # Navigation bar component
├── main.ts            # Client-side bootstrap
├── main.server.ts     # Server-side bootstrap
├── server.ts          # Express server configuration
└── styles.css         # Global styles with CSS variables
```

### Key Architectural Decisions

- **Standalone Components**: All components use Angular's standalone API (no modules)
- **Spanish Routes**: Application uses Spanish route paths (`iniciar-sesion`, `registrarse`, `inicio`)
- **SSR-Ready**: Configured for server-side rendering with hydration support
- **Dark Theme**: Built with dark theme as default using CSS custom properties
- **Signal-Based**: Uses Angular signals for reactive state management

### CSS Architecture

Global CSS variables are defined in `src/styles.css`:
- `--primary-color: #1173d4`
- `--background-color: #111827`
- `--card-background-color: #1f2937`
- `--text-primary: #ffffff`
- `--text-secondary: #9ca3af`
- `--accent-color: #3b82f6`

## Development Commands

### Start Development Server
```bash
npm start
# or
ng serve
```
Runs at `http://localhost:4200/` with live reload.

### Build Application
```bash
# Production build
npm run build
# or
ng build

# Development build with watch mode
npm run watch
# or
ng build --watch --configuration development
```

### Testing
```bash
# Run unit tests
npm test
# or
ng test

# Run tests in watch mode (default behavior)
ng test --watch

# Run tests once (CI mode)
ng test --watch=false
```

### SSR Development
```bash
# Build and serve SSR version
npm run build
npm run serve:ssr:impuestoscal-fronted
```

### Code Generation
```bash
# Generate new component
ng generate component component-name

# Generate component in specific folder
ng generate component auth/new-component

# See all available schematics
ng generate --help
```

### Formatting
Project uses Prettier with these configurations:
- Print width: 100 characters
- Single quotes preferred
- Angular HTML parser for templates

## Component Development Guidelines

### Component Structure
All components follow this pattern:
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-component-name',
  imports: [], // List standalone component imports
  templateUrl: './component-name.html',
  styleUrl: './component-name.css'
})
export class ComponentName {
  // Component logic
}
```

### File Naming Convention
- Component files: `component-name.ts`
- Templates: `component-name.html`
- Styles: `component-name.css`
- Tests: `component-name.spec.ts`

### Routing
Routes are defined in `src/app/app.routes.ts`. Current routes:
- `/iniciar-sesion` → Login component
- `/registrarse` → Register component  
- `/inicio` → Home component

## Build Configuration

### TypeScript Configuration
- Strict mode enabled with comprehensive type checking
- Experimental decorators enabled
- ES2022 target with module preservation

### Bundle Size Budgets
- Initial bundle: 500kB warning, 1MB error
- Component styles: 4kB warning, 8kB error

### Development vs Production
- **Development**: Source maps enabled, no optimization
- **Production**: Full optimization, output hashing, license extraction

## Testing Guidelines

### Unit Testing
- Uses Jasmine test framework with Karma runner
- Test files end with `.spec.ts`
- All components should have accompanying unit tests
- Tests are located alongside component files

### Running Specific Tests
```bash
# Run tests for specific file
ng test --include="**/login.spec.ts"

# Run tests matching pattern
ng test --include="**/auth/**/*.spec.ts"
```

## Tailwind CSS Usage

### Configuration
- Uses Tailwind CSS v4 via PostCSS plugin
- Configuration in `.postcssrc.json`
- Global styles import Tailwind directives in `src/styles.css`

### Custom CSS Variables
Prefer using CSS custom properties for theming:
```css
background-color: var(--background-color);
color: var(--text-primary);
```
