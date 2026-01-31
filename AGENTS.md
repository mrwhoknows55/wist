# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Project Overview

Wist is a Kotlin Multiplatform wishlist application with web scraping capabilities. It targets
Android, iOS, Desktop (JVM), Web (JS/WASM), and includes a Ktor backend server with PostgreSQL
integration.

## Build Commands

### Server

```bash
# Run development server
./gradlew :server:run

# Build server JAR
./gradlew :server:shadowJar
```

### Android

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install and run on device
./gradlew :composeApp:installDebug
```

### Desktop (JVM)

```bash
# Run desktop application
./gradlew :composeApp:run
```

### Web

```bash
# Wasm target (modern browsers, faster)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# JS target (legacy browser support)
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### iOS

Open `/iosApp` directory in Xcode and run from there.

### Docker

```bash
# Start PostgreSQL + server
docker-compose up

# Build and push image (CI/CD)
docker build -t ghcr.io/username/wist:latest .
docker push ghcr.io/username/wist:latest
```

### Testing

```bash
# Run all tests
./gradlew test

# Test specific module
./gradlew :server:test
./gradlew :client:test
```

## Module Architecture

The project uses a 5-module structure with clear separation of concerns:

### `:core`

- Purpose: Shared DTOs across all platforms
- Key Files: `WishlistDtos.kt`, `ScrapedProductDto.kt`, `RetailerInfo.kt`
- Targets: JVM, iOS, JS, WASM
- Note: Pure data models with `@Serializable` - no business logic

### `:shared`

- Purpose: Platform utilities and constants
- Key Files: `Platform.kt` (platform detection), `Constants.kt`
- Targets: Android, iOS, JVM, JS, WASM

### `:client`

- Purpose: HTTP API client facade
- Architecture: `WistApiClient` aggregates domain-specific clients (`WishlistApiClient`,
  `WishlistItemApiClient`)
- HTTP Config: Ktor Client with 30s timeout (accommodates web scraping delays)
- Platform Engines: CIO for JVM/Android, Darwin for iOS
- Error Handling: Returns `Result<T>` wrapper, use `runCatchingSafe` extension

### `:server`

- Purpose: Ktor REST API with PostgreSQL backend
- Architecture: Layered (Routes → Services → Repositories → Database)
- Key Components:
    - Routes: `WishlistRoute.kt`, `WishlistItemRoute.kt`, `HealthRoute.kt`
    - Services: `WishlistItemService.kt` (orchestrates scraping), `FirecrawlService.kt` (web
      scraping)
    - Repositories: `WishlistRepository.kt`, `WishlistItemRepository.kt` (Exposed ORM)
    - Database: `DatabaseFactory.kt` (HikariCP pooling), `Tables.kt` (schema)
- Entry Point: `Application.kt` (module configuration)

### `:composeApp`

- Purpose: Multiplatform UI (Android, iOS, Desktop, Web)
- Architecture: MVVM-inspired with local Compose state
- Component Structure (Atomic Design):
    - `atoms/`: Reusable primitives (buttons, icons, price tags)
    - `molecules/`: Composed components (search input, list items)
    - `organisms/`: Complex composites (product cards, bottom sheets)
    - `screens/`: Full-page composables (`WishlistListScreen`, `WishlistDetailScreen`)
- Navigation: Enum-based (`Screen.Home`, `Screen.Detail`) with mutable state in root `App.kt`
- Theme: Material 3 dark theme in `theme/` package

## API Structure

Base URL: `http://localhost:8080` (Android emulator: `10.0.2.2:8080`)

### Endpoints

```
GET    /health                                   # Health check + DB connectivity
GET    /api/v1/wishlists                         # List all active wishlists
POST   /api/v1/wishlists                         # Create wishlist (name required)
GET    /api/v1/wishlists/{id}                    # Get single wishlist
PUT    /api/v1/wishlists/{id}                    # Update wishlist name
DELETE /api/v1/wishlists/{id}                    # Soft-delete wishlist
GET    /api/v1/wishlists/{id}/items              # List items in wishlist
POST   /api/v1/wishlists/{id}/items              # Add item (triggers web scraping)
PUT    /api/v1/wishlists/{id}/items/{itemId}     # Update item details
DELETE /api/v1/wishlists/{id}/items/{itemId}     # Delete item
```

### Add Item Flow (Complex)

1. Client POSTs URL to `/api/v1/wishlists/{id}/items`
2. Server validates URL format
3. `WishlistItemService.addItemToWishlist()` calls `FirecrawlService.scrapeProduct(url)`
4. Firecrawl API extracts product data via LLM with JSON schema
5. Service maps response to `ScrapedProductDto` + detects retailer from URL
6. Repository saves normalized fields to `wishlist_items` table
7. Returns `WishlistItemDto` to client
8. Errors: 400 (bad request), 502 (scraping failed), 500 (internal error)

## Database Schema

PostgreSQL with Exposed ORM. Schema auto-created on server startup.

### Tables

```sql
wishlists:
  - id (PK)
  - name
  - createdAt
  - updatedAt
  - deletedAt (nullable, soft delete)

wishlist_items:
  - id (PK)
  - wishlistId (FK to wishlists)
  - sourceUrl
  - productName
  - price
  - currency
  - imageUrl
  - retailerName
  - rating
  - createdAt
  - updatedAt
```

Important: Wishlists use soft deletes (`deletedAt` timestamp). Queries filter
`deletedAt IS NULL` by default.

## Configuration

### Environment Variables (`.env`)

```env
PORT=8080
FIRECRAWL_API_KEY=<your_api_key>
DB_URL=jdbc:postgresql://localhost:5432/wist
DB_USER=postgres
DB_PASSWORD=postgres
```

### Server Config (`server/src/main/resources/application.yaml`)

```yaml
ktor:
  deployment:
    port: ${?PORT}
  application:
    modules:
      - dev.avadhut.wist.ApplicationKt.module

firecrawl:
  apiKey: ${?FIRECRAWL_API_KEY}
  baseUrl: "https://api.firecrawl.dev"

db:
  driver: "org.postgresql.Driver"
  url: ${?DB_URL}
  user: ${?DB_USER}
  password: ${?DB_PASSWORD}
```

## Key Architectural Patterns

### Repository Pattern

- Server-side repositories (`WishlistRepository`, `WishlistItemRepository`) abstract database
  queries
- All queries wrapped in Exposed `transaction { }` blocks
- Soft deletes handled at repository layer

### Service Layer

- `WishlistItemService`: Orchestrates scraping pipeline (URL → Firecrawl → DB)
- `FirecrawlService`: External API integration with retailer detection post-processing
- Decouples business logic from HTTP routes

### Dependency Injection

- Manual constructor injection (no DI framework)
- Services created in `Plugins.kt` and passed to routes via lambda closures
- Client instantiated at root `App()` level, passed down to screens as parameters

### State Management

- Compose local state via `remember { mutableStateOf() }`
- No global state management (Redux/MVI not used)
- Screen state isolated per composable

### Error Handling

- Client: `Result<T>` wrapper with `onSuccess`/`onFailure` branches
- Server: HTTP status codes + JSON error responses
- Use `runCatchingSafe` extension for consistent error mapping

## Web Scraping Integration

- Provider: Firecrawl API (LLM-powered extraction)
- Schema: Defined in `core/ScrapeRequest.kt` (title, price, currency, rating, etc.)
- Retailer Detection: Domain-based mapping in `FirecrawlService.extractRetailerFromUrl()`
    - Supported: Amazon, Flipkart, Myntra, Ajio, Croma
    - Logo URLs from Clearbit API
- Timeout: 30s request timeout accommodates slow scraping operations

## Platform-Specific Notes

### Android

- API client configured for emulator: `http://10.0.2.2:8080`
- Edge-to-edge UI support in `MainActivity`
- Material 3 dark theme

### Desktop (JVM)

- Window-based entry in `main.kt`
- Standard localhost: `http://localhost:8080`

### Web

- ComponentDemoScreen as fallback demo
- Wasm target preferred for performance

### iOS

- Darwin HTTP engine
- Framework bindings via `MainViewController`

## Android 80-20 Rule: High-Impact Practices

1. Never hardcode colors — use MaterialTheme.colorScheme in Compose, colors.xml in XML
2. Don't catch CancellationException — let it propagate; catching breaks coroutine cancellation
3. Avoid GlobalScope — use viewModelScope/lifecycleScope so work ties to lifecycle
4. Single Activity + Navigation — one activity, navigate with NavController
5. Stateless Composables — hoist state up, pass callbacks down
6. Remember expensive operations — use remember, derivedStateOf, LaunchedEffect
7. Proper lifecycle collection — collectAsStateWithLifecycle() or repeatOnLifecycle
8. Responsive layouts — WindowSizeClass, adaptive layouts, avoid fixed sizes
9. Dependency Injection — Hilt/Koin, inject ViewModels and repositories
10. Sealed classes for state — UI/Loading/Error states, no nulls or booleans
11. Repository pattern — separate data layer, Room as single source of truth
12. Background dispatchers — IO for network/DB, Default for CPU work, never Main
13. Immutable data — val over var, use copy() for updates
14. Modifier order matters — size before padding, clickable after padding
15. ProGuard/R8 + shrinkResources — enable for release, keep rules updated

## Development Best Practices (from AGENTS.md)

1. Never hardcode colors - Use `MaterialTheme.colorScheme`
2. Stateless Composables - Hoist state up, pass callbacks down
3. Remember expensive operations - Use `remember`, `derivedStateOf`, `LaunchedEffect`
4. Repository pattern - Separate data layer, database as single source of truth
5. Background dispatchers - IO for network/DB, Default for CPU work
6. Immutable data - `val` over `var`, use `copy()` for updates
7. Modifier order matters - Size before padding, clickable after padding

## CI/CD

GitHub Actions workflow (`.github/workflows/deploy.yml`):

- Triggers on push to `main`
- Builds Docker image
- Pushes to `ghcr.io/<repository>:latest`
- Uses GitHub Container Registry with caching
