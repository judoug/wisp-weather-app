# Wisp Weather App - Testing Guide

## Overview

This document provides a comprehensive guide to the testing strategy and implementation for the Wisp Weather App. The app follows a multi-layered testing approach to ensure reliability, performance, and user experience.

## Testing Architecture

### 1. Unit Tests
- **Location**: `app/src/test/java/`
- **Purpose**: Test individual components in isolation
- **Coverage**: ViewModels, business logic, utility functions
- **Tools**: JUnit, MockK, Turbine, Coroutines Test

### 2. UI Tests
- **Location**: `app/src/androidTest/java/`
- **Purpose**: Test UI components and user interactions
- **Coverage**: Compose UI components, screen navigation, user flows
- **Tools**: Compose UI Test, Espresso, UIAutomator

### 3. Integration Tests
- **Location**: `app/src/androidTest/java/com/example/wisp/integration/`
- **Purpose**: Test complete data flows and system integration
- **Coverage**: API to database flow, UI to ViewModel integration
- **Tools**: Hilt Android Test, Room Testing, Retrofit Mock

### 4. Performance Tests
- **Location**: `app/src/androidTest/java/com/example/wisp/performance/`
- **Purpose**: Measure app performance and identify bottlenecks
- **Coverage**: Startup time, memory usage, CPU performance
- **Tools**: Macrobenchmark, Profiler

### 5. Accessibility Tests
- **Location**: `app/src/androidTest/java/com/example/wisp/accessibility/`
- **Purpose**: Ensure app is accessible to all users
- **Coverage**: Screen reader support, touch targets, color contrast
- **Tools**: Accessibility Testing Framework

### 6. Error Handling Tests
- **Location**: `app/src/androidTest/java/com/example/wisp/errorhandling/`
- **Purpose**: Verify robust error handling and recovery
- **Coverage**: Network failures, invalid data, edge cases
- **Tools**: MockK, Test Doubles

## Test Categories

### ViewModel Tests

#### HomeViewModelTest
- **Initial state loading**
- **Weather data loading and error handling**
- **Refresh functionality**
- **Connectivity monitoring**
- **Retry mechanisms**

#### LocationsViewModelTest
- **Place management (add, remove, set primary)**
- **Loading states and error handling**
- **Maximum places validation**
- **Selection and clearing**

#### SearchViewModelTest
- **Search functionality with debouncing**
- **Search history management**
- **Place addition with validation**
- **Error handling for various scenarios**

#### SettingsViewModelTest
- **Settings persistence**
- **Permission checking**
- **Default value handling**
- **Save state management**

### UI Component Tests

#### HomeScreenTest
- **Welcome message display**
- **Weather data display**
- **Loading and error states**
- **Navigation elements**
- **Pull-to-refresh functionality**

#### WeatherContentTest
- **Weather information display**
- **Offline indicator**
- **Last refresh time**
- **Icon and content descriptions**

#### LocationsScreenTest
- **Empty state display**
- **Location list display**
- **Swipe-to-delete functionality**
- **Primary place indicators**

#### SearchScreenTest
- **Search input handling**
- **Search results display**
- **Search history**
- **Add location functionality**

#### SettingsScreenTest
- **Settings sections display**
- **Toggle functionality**
- **Permission status display**
- **Reset to defaults**

### Integration Tests

#### WeatherDataFlowIntegrationTest
- **Complete API to database flow**
- **Caching mechanism validation**
- **Offline mode functionality**
- **Place management operations**
- **Connectivity monitoring**
- **Error handling scenarios**
- **Maximum places limit enforcement**

#### UIFlowIntegrationTest
- **Complete user journey testing**
- **Navigation flow validation**
- **State management across screens**
- **Error state handling**
- **Loading state management**

### Performance Tests

#### PerformanceTest
- **App startup time measurement**
- **Cold start performance**
- **Compilation mode testing**
- **Memory usage monitoring**

### Accessibility Tests

#### AccessibilityTest
- **Content description validation**
- **Semantic structure verification**
- **Touch target size validation**
- **Color contrast verification**
- **Screen reader compatibility**

### Error Handling Tests

#### ErrorHandlingTest
- **Network error scenarios**
- **Location service errors**
- **Place not found errors**
- **Maximum places reached errors**
- **Generic error handling**
- **Retry mechanism validation**
- **Offline state handling**

## Running Tests

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.wisp.ui.screens.HomeViewModelTest"

# Run tests with coverage
./gradlew testDebugUnitTestCoverage
```

### UI Tests
```bash
# Run all UI tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest --tests "com.example.wisp.ui.screens.HomeScreenTest"

# Run tests on specific device
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.wisp.ui.screens.HomeScreenTest
```

### Integration Tests
```bash
# Run integration tests
./gradlew connectedAndroidTest --tests "com.example.wisp.integration.*"

# Run specific integration test
./gradlew connectedAndroidTest --tests "com.example.wisp.integration.WeatherDataFlowIntegrationTest"
```

### Performance Tests
```bash
# Run performance tests
./gradlew connectedAndroidTest --tests "com.example.wisp.performance.*"

# Run with specific device configuration
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.wisp.performance.PerformanceTest
```

### Accessibility Tests
```bash
# Run accessibility tests
./gradlew connectedAndroidTest --tests "com.example.wisp.accessibility.*"

# Run with accessibility testing framework
./gradlew connectedAndroidTest --tests "com.example.wisp.accessibility.AccessibilityTest"
```

### Error Handling Tests
```bash
# Run error handling tests
./gradlew connectedAndroidTest --tests "com.example.wisp.errorhandling.*"

# Run specific error scenario tests
./gradlew connectedAndroidTest --tests "com.example.wisp.errorhandling.ErrorHandlingTest"
```

## Test Configuration

### Dependencies
The following testing dependencies are configured in `app/build.gradle.kts`:

```kotlin
// Unit Testing
testImplementation(libs.junit)
testImplementation(libs.mockk)
testImplementation(libs.turbine)
testImplementation(libs.coroutines.test)
testImplementation("androidx.arch.core:core-testing:2.2.0")
testImplementation("androidx.test:core:1.5.0")
testImplementation("androidx.test:rules:1.5.0")
testImplementation("androidx.test:runner:1.5.2")
testImplementation("org.robolectric:robolectric:4.11.1")

// UI Testing
androidTestImplementation(libs.androidx.test.ext.junit)
androidTestImplementation(libs.espresso.core)
androidTestImplementation(platform(libs.compose.bom))
androidTestImplementation(libs.compose.ui.test.junit4)
androidTestImplementation("androidx.test:core:1.5.0")
androidTestImplementation("androidx.test:rules:1.5.0")
androidTestImplementation("androidx.test:runner:1.5.2")
androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.5.1")
```

### Test Rules
- **HiltAndroidRule**: For dependency injection in tests
- **createComposeRule**: For Compose UI testing
- **createAndroidComposeRule**: For Android Compose testing with activity

### Mock Configuration
- **MockK**: For mocking dependencies and verifying interactions
- **Turbine**: For testing Flow emissions
- **Coroutines Test**: For testing coroutine-based code

## Test Data

### Test Fixtures
- **Weather Data**: Mock weather bundles with realistic data
- **Places**: Test places with various coordinates and names
- **Error Scenarios**: Predefined error conditions for testing

### Test Utilities
- **createTestWeatherBundle()**: Creates mock weather data
- **createTestPlace()**: Creates mock place data
- **Mock ViewModels**: Pre-configured mock ViewModels for UI testing

## Best Practices

### 1. Test Organization
- Group tests by functionality
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

### 2. Mocking Strategy
- Mock external dependencies
- Use real objects for domain models
- Verify interactions when necessary

### 3. Test Data
- Use realistic test data
- Create reusable test fixtures
- Avoid hardcoded values

### 4. Error Testing
- Test all error scenarios
- Verify error messages are user-friendly
- Test retry mechanisms

### 5. Performance Testing
- Test on real devices
- Measure key performance metrics
- Monitor memory usage

### 6. Accessibility Testing
- Test with screen readers
- Verify touch target sizes
- Check color contrast ratios

## Continuous Integration

### GitHub Actions
The project includes GitHub Actions workflows for:
- **Unit Test Execution**: Run on every push
- **UI Test Execution**: Run on pull requests
- **Performance Testing**: Run on release builds
- **Accessibility Testing**: Run on UI changes

### Test Reports
- **Coverage Reports**: Generated for unit tests
- **Test Results**: Available in GitHub Actions
- **Performance Metrics**: Tracked over time

## Troubleshooting

### Common Issues

#### 1. Test Failures
- Check device/emulator state
- Verify test data setup
- Review mock configurations

#### 2. Performance Issues
- Use release builds for performance tests
- Test on real devices
- Monitor system resources

#### 3. Accessibility Issues
- Verify content descriptions
- Check touch target sizes
- Test with accessibility services

#### 4. Integration Test Issues
- Ensure proper Hilt setup
- Check database state
- Verify network conditions

## Future Enhancements

### Planned Improvements
1. **Automated UI Testing**: Implement screenshot testing
2. **Load Testing**: Add stress testing for API calls
3. **Security Testing**: Add security vulnerability testing
4. **Localization Testing**: Test with different locales
5. **Device Testing**: Test on various device configurations

### Test Coverage Goals
- **Unit Tests**: 90%+ coverage
- **UI Tests**: 80%+ coverage
- **Integration Tests**: 70%+ coverage
- **Error Scenarios**: 100% coverage

## Conclusion

This comprehensive testing strategy ensures the Wisp Weather App is reliable, performant, and accessible. The multi-layered approach covers all aspects of the application, from individual components to complete user journeys. Regular execution of these tests helps maintain code quality and prevents regressions.

For questions or issues with testing, please refer to the individual test files or contact the development team.
