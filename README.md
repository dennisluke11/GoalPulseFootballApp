# GoalPulse - Football App

A modern Android football application built with Kotlin, Jetpack Compose, MVVM architecture, Koin dependency injection, and following SOLID principles.

## Features

- **Search Functionality**: Search for leagues and teams with debounced input
- **Leagues**: Browse and search football leagues from around the world
- **Teams**: Discover teams with detailed information
- **Fixtures**: View upcoming and past football matches
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Clean Architecture**: MVVM pattern with SOLID principles
- **Testing**: Comprehensive unit tests for all Repositories and ViewModels
- **Caching**: File-based caching to reduce API calls

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture pattern:

- **Data Layer**: Repository interfaces and implementations, API Service, Data Models, Cache Manager
- **Domain Layer**: Business logic (implicit in ViewModel)
- **Presentation Layer**: ViewModels, UI State classes, UI (Compose Screens)

### SOLID Principles Implementation

1. **Single Responsibility**: Each class has a single, well-defined responsibility
   - `LeaguesRepository`, `TeamsRepository`, `FixturesRepository`: Handle data operations for specific entities
   - `LeaguesViewModel`, `TeamsViewModel`, `FixturesViewModel`: Manage UI state for specific screens
   - `LeagueDetailViewModel`, `TeamDetailViewModel`, `FixtureDetailViewModel`: Handle detail screen logic
   - `ApiFootballService`: Defines API contracts
   - `ErrorHandler`: Centralized error handling
   - `CacheManager`: Manages caching logic
   - `SeasonCalculator`: Calculates current season
   - `ApiKeyValidator`: Validates API keys

2. **Open/Closed**: Extensible through interfaces (Repository interfaces)

3. **Liskov Substitution**: Repository implementations can be swapped

4. **Interface Segregation**: Focused interfaces (e.g., `LeaguesRepository`, `TeamsRepository`, `FixturesRepository`)

5. **Dependency Inversion**: Dependencies injected via Koin, not hardcoded

## Setup Instructions

### 1. Get API Key

1. Visit [RapidAPI - API-Football](https://rapidapi.com/api-sports/api/api-football)
2. Sign up for a free account
3. Subscribe to the API-Football API (free tier available)
4. Copy your API key

### 2. Configure API Key

1. Copy the template file to the config directory:
   ```bash
   cp app/ApiConfig.template.kt app/src/main/java/com/example/goalpulse/config/ApiConfig.kt
   ```

2. Open `app/src/main/java/com/example/goalpulse/config/ApiConfig.kt` and replace `"YOUR_API_KEY_HERE"` with your actual API key:

```kotlin
object ApiConfig {
    const val API_KEY = "YOUR_ACTUAL_API_KEY_HERE"
}
```

**Important**: The `ApiConfig.kt` file is gitignored and will never be committed to the repository. Only the template file is tracked.

**Note**: For production apps, consider storing the API key in:
- `local.properties` (for local development)
- `BuildConfig` (for build variants)
- Secure storage or environment variables

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Project Structure

```
app/src/main/java/com/example/goalpulse/
├── config/              # App constants and API configuration
├── data/
│   ├── local/           # Cache manager for file-based caching
│   ├── model/           # Data models (League, Team, Fixture)
│   ├── remote/          # API service, Retrofit client, Error handler
│   └── repository/      # Repository interfaces and implementations
│       ├── LeaguesRepository.kt
│       ├── TeamsRepository.kt
│       ├── FixturesRepository.kt
│       ├── ApiKeyValidator.kt
│       └── SeasonCalculator.kt
├── di/                  # Koin dependency injection modules
├── ui/
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation routes
│   ├── screens/         # Compose UI screens
│   ├── strings/         # String resources
│   ├── theme/           # Material theme and dimensions
│   └── viewmodel/       # ViewModels and UI state classes
│       ├── LeaguesViewModel.kt
│       ├── TeamsViewModel.kt
│       ├── FixturesViewModel.kt
│       ├── LeagueDetailViewModel.kt
│       ├── TeamDetailViewModel.kt
│       └── FixtureDetailViewModel.kt
├── util/                # Utility classes (DateFormatter)
├── GoalPulseApplication.kt
└── MainActivity.kt
```

## Technologies Used

- **Kotlin**: Programming language
- **Jetpack Compose**: Modern UI toolkit
- **MVVM**: Architecture pattern
- **Koin**: Dependency injection
- **Retrofit**: HTTP client for API calls
- **Coroutines**: Asynchronous programming with Flow
- **Navigation Compose**: Navigation between screens
- **Coil**: Image loading
- **Material Design 3**: UI components
- **Turbine**: Flow testing library
- **MockK**: Mocking library for unit tests

## Testing

The project includes comprehensive unit tests:

### ViewModel Tests
- `LeaguesViewModelTest`: Tests leagues state management, search, and error handling
- `TeamsViewModelTest`: Tests teams state management, search, and popular teams loading
- `FixturesViewModelTest`: Tests fixtures loading with various parameters
- `LeagueDetailViewModelTest`: Tests league detail parsing and error handling
- `TeamDetailViewModelTest`: Tests team detail parsing and error handling
- `FixtureDetailViewModelTest`: Tests fixture detail parsing and error handling

### Repository Tests
- `LeaguesRepositoryTest`: Tests leagues repository caching and API calls
- `TeamsRepositoryTest`: Tests teams repository caching, search, and API calls
- `FixturesRepositoryTest`: Tests fixtures repository caching and API calls

Run tests with:
```bash
./gradlew test
```

Run specific test classes:
```bash
./gradlew test --tests "com.example.goalpulse.ui.viewmodel.*"
./gradlew test --tests "com.example.goalpulse.data.repository.*"
```

## API Documentation

This app uses the [API-Football](https://www.api-football.com/documentation-v3) API provided by RapidAPI.

## License

This project is created for educational purposes.
