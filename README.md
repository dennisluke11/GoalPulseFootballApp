# GoalPulse - Football App

A modern Android football application built with Kotlin, Jetpack Compose, MVVM architecture, Koin dependency injection, and following SOLID principles.

## Features

- **Search Functionality**: Search for leagues and teams
- **Leagues**: Browse and search football leagues from around the world
- **Teams**: Discover teams with detailed information
- **Fixtures**: View upcoming and past football matches
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Clean Architecture**: MVVM pattern with SOLID principles
- **Testing**: Comprehensive unit tests for Repository and ViewModel

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture pattern:

- **Data Layer**: Repository, API Service, Data Models
- **Domain Layer**: Business logic (implicit in ViewModel)
- **Presentation Layer**: ViewModel, UI (Compose Screens)

### SOLID Principles Implementation

1. **Single Responsibility**: Each class has a single, well-defined responsibility
   - `FootballRepository`: Handles data operations
   - `FootballViewModel`: Manages UI state
   - `ApiFootballService`: Defines API contracts

2. **Open/Closed**: Extensible through interfaces (Repository interface)

3. **Liskov Substitution**: Repository implementations can be swapped

4. **Interface Segregation**: Focused interfaces (e.g., `ApiFootballService`)

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
├── data/
│   ├── model/          # Data models (League, Team, Fixture)
│   ├── remote/         # API service and Retrofit client
│   └── repository/     # Repository interface and implementation
├── di/                 # Koin dependency injection modules
├── ui/
│   ├── navigation/     # Navigation routes
│   ├── screens/        # Compose UI screens
│   ├── theme/          # Material theme
│   └── viewmodel/      # ViewModels
├── GoalPulseApplication.kt
└── MainActivity.kt
```

## Technologies Used

- **Kotlin**: Programming language
- **Jetpack Compose**: Modern UI toolkit
- **MVVM**: Architecture pattern
- **Koin**: Dependency injection
- **Retrofit**: HTTP client for API calls
- **Coroutines**: Asynchronous programming
- **Navigation Compose**: Navigation between screens
- **Coil**: Image loading
- **Material Design 3**: UI components

## Testing

The project includes comprehensive unit tests:

- `FootballRepositoryTest`: Tests repository data operations
- `FootballViewModelTest`: Tests ViewModel state management

Run tests with:
```bash
./gradlew test
```

## API Documentation

This app uses the [API-Football](https://www.api-football.com/documentation-v3) API provided by RapidAPI.

## License

This project is created for educational purposes.

