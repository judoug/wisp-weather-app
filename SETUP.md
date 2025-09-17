# Wisp Weather App - Setup Guide

This guide will help you set up the development environment for the Wisp Weather App.

## Prerequisites

### 1. Java Development Kit (JDK) 17 ✅

Java 17 is already installed and configured on your system.

### 2. Android SDK Setup

You need to install the Android SDK. Here are the options:

#### Option A: Install Android Studio (Recommended)

1. Download Android Studio from [https://developer.android.com/studio](https://developer.android.com/studio)
2. Install Android Studio
3. Open Android Studio and follow the setup wizard
4. The SDK will be installed to: `/Users/jeremihad/Library/Android/sdk`

#### Option B: Command Line Tools Only

1. Download Android Command Line Tools from [https://developer.android.com/studio#command-tools](https://developer.android.com/studio#command-tools)
2. Extract to a directory (e.g., `~/android-sdk`)
3. Set up environment variables:

```bash
echo 'export ANDROID_HOME=~/android-sdk' >> ~/.zshrc
echo 'export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH' >> ~/.zshrc
source ~/.zshrc
```

4. Install required SDK components:

```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 3. Update local.properties

After installing the Android SDK, update the `local.properties` file with the correct SDK path:

```properties
sdk.dir=/Users/jeremihad/Library/Android/sdk
```

### 4. Get OpenWeather API Key

1. Go to [https://openweathermap.org/api](https://openweathermap.org/api)
2. Sign up for a free account
3. Get your API key
4. Add it to `local.properties`:

```properties
OPENWEATHER_API_KEY=your_actual_api_key_here
```

## Build the Project

Once everything is set up:

```bash
./gradlew build
```

## Run the App

```bash
./gradlew installDebug
```

Or open the project in Android Studio and run it from there.

## Troubleshooting

### "SDK location not found" Error

Make sure:
1. Android SDK is installed
2. `local.properties` has the correct `sdk.dir` path
3. The path exists and contains the Android SDK

### "JAVA_HOME not set" Error

Java 17 is already configured. If you see this error, restart your terminal or IDE.

### Build Fails with Dependency Errors

Make sure you have:
1. Android SDK Platform 34
2. Android Build Tools 34.0.0
3. Android SDK Command-line Tools

You can install these through Android Studio's SDK Manager or command line:

```bash
sdkmanager "platforms;android-34" "build-tools;34.0.0" "cmdline-tools;latest"
```

## Next Steps

Once the project builds successfully, you can proceed with the next prompts in the development sequence to implement the domain layer, data sources, and UI components.
