# Wisp Weather App

A modern Android weather application built with Jetpack Compose, featuring multi-module architecture, Hilt dependency injection, and Material 3 design.

## Features

- 🌤️ Current weather conditions
- 📍 Multiple location support
- 🔍 Location search
- 📱 Material 3 design
- 🏗️ Multi-module architecture
- 💉 Hilt dependency injection
- 🎨 Jetpack Compose UI

## Architecture

The app follows a clean architecture pattern with the following modules:

- **:app** - Main application module with UI and navigation
- **:domain** - Domain models and interfaces
- **:data:weather** - Weather data sources and API clients
- **:data:location** - Location services
- **:data:db** - Room database and local storage
- **:design** - UI theme, typography, and design system

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK 34
- Min SDK 24

## Setup Instructions

### 1. Clone the repository

```bash
git clone <repository-url>
cd "Weather Widget"
```

### 2. Add OpenWeather API Key

1. Get a free API key from [OpenWeatherMap](https://openweathermap.org/api)
2. Create a `local.properties` file in the root directory if it doesn't exist
3. Add your API key:

```properties
OPENWEATHER_API_KEY=your_api_key_here
```

**Important:** Never commit your API key to version control. The `local.properties` file is already in `.gitignore`.

### 3. Build the project

```bash
./gradlew build
```

### 4. Run the app

```bash
./gradlew installDebug
```

Or use Android Studio:
1. Open the project in Android Studio
2. Sync the project
3. Run the app on a device or emulator

## Project Structure

```
app/
├── src/main/java/com/example/wisp/
│   ├── MainActivity.kt
│   ├── WispApplication.kt
│   └── ui/
│       ├── AppNav.kt
│       ├── WispApp.kt
│       └── screens/
│           ├── HomeScreen.kt
│           ├── LocationsScreen.kt
│           ├── SearchScreen.kt
│           └── SettingsScreen.kt

domain/
└── src/main/java/com/example/wisp/domain/
    └── (domain models and interfaces)

data/
├── weather/
│   └── src/main/java/com/example/wisp/data/weather/
│       └── (weather API implementation)
├── location/
│   └── src/main/java/com/example/wisp/data/location/
│       └── (location services)
└── db/
    └── src/main/java/com/example/wisp/data/db/
        └── (Room database)

design/
└── src/main/java/com/example/wisp/design/
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Dependencies

- **Jetpack Compose** - Modern UI toolkit
- **Hilt** - Dependency injection
- **Retrofit** - HTTP client
- **Room** - Local database
- **Coil** - Image loading
- **Navigation Compose** - Navigation
- **Material 3** - Design system

## Development

### Running Tests

```bash
./gradlew test
```

### Code Style

The project uses ktlint for code formatting. Run:

```bash
./gradlew ktlintCheck
./gradlew ktlintFormat
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for weather data
- [Material Design](https://material.io/) for design guidelines
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI framework
