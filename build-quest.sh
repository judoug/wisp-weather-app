#!/bin/bash

# Quest Build Script for Wisp Weather App
# This script builds the Quest flavor for different build types

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [debug|release|bundle]"
    echo ""
    echo "Commands:"
    echo "  debug   - Build debug APK for testing"
    echo "  release - Build release APK for sideloading"
    echo "  bundle  - Build release bundle for Quest Store"
    echo ""
    echo "Examples:"
    echo "  $0 debug"
    echo "  $0 release"
    echo "  $0 bundle"
}

# Function to build debug APK
build_debug() {
    print_status "Building Quest debug APK..."
    ./gradlew assembleQuestDebug
    print_success "Debug APK built successfully!"
    print_status "APK location: app/build/outputs/apk/quest/debug/app-quest-debug.apk"
}

# Function to build release APK
build_release() {
    print_status "Building Quest release APK..."
    ./gradlew assembleQuestRelease
    print_success "Release APK built successfully!"
    print_status "APK location: app/build/outputs/apk/quest/release/app-quest-release.apk"
}

# Function to build release bundle
build_bundle() {
    print_status "Building Quest release bundle..."
    ./gradlew bundleQuestRelease
    print_success "Release bundle built successfully!"
    print_status "Bundle location: app/build/outputs/bundle/questRelease/app-quest-release.aab"
}

# Function to install debug APK to connected Quest device
install_debug() {
    print_status "Installing debug APK to Quest device..."
    
    # Check if device is connected
    if ! adb devices | grep -q "device$"; then
        print_error "No Quest device connected. Please connect your Quest device and enable Developer Mode."
        exit 1
    fi
    
    # Install the APK
    adb install -r app/build/outputs/apk/quest/debug/app-quest-debug.apk
    print_success "Debug APK installed successfully!"
    
    # Launch the app
    print_status "Launching Wisp Weather App..."
    adb shell am start -n com.example.wisp.quest/.MainActivity
    print_success "App launched!"
}

# Function to clean build
clean_build() {
    print_status "Cleaning build..."
    ./gradlew clean
    print_success "Build cleaned!"
}

# Main script logic
case "${1:-}" in
    "debug")
        clean_build
        build_debug
        echo ""
        print_status "To install to Quest device, run: $0 install"
        ;;
    "release")
        clean_build
        build_release
        ;;
    "bundle")
        clean_build
        build_bundle
        ;;
    "install")
        install_debug
        ;;
    "clean")
        clean_build
        ;;
    *)
        show_usage
        exit 1
        ;;
esac

echo ""
print_success "Build completed successfully!"
print_status "For Quest Store submission, use the bundle from: app/build/outputs/bundle/questRelease/"
