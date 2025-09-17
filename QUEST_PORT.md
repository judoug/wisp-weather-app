# Wisp Weather App - Quest Port Guide

This guide covers the complete migration of the Wisp Weather App from Android to Meta Quest (Horizon OS) as a 2D panel application.

## Overview

The Quest port includes:
- **Quest product flavor** that excludes Google Play Services
- **IP geolocation fallback** for location services
- **VR/AR optimized UI** with large touch targets and controller support
- **Performance optimizations** for 72/90/120Hz displays
- **Quest Store packaging** configuration

## Project Structure

### Build Flavors
- `android` - Standard Android build with Google Play Services
- `quest` - Quest build without Play Services, using IP geolocation

### Key Components Added

#### Location Services
- `IpGeoLocationProvider` - IP-based geolocation fallback
- `ManualLocationProvider` - Manual location selection
- `QuestLocationProvider` - Composite provider for Quest
- `QuestLocationModule` - Quest-specific DI module

#### UI Components
- `QuestUiUtils` - VR/AR UI utilities and modifiers
- `QuestLocationSettings` - Quest-specific location settings
- `QuestSettingsScreen` - VR-optimized settings screen
- `QuestNavigation` - Quest-specific navigation

#### Performance
- `QuestPerformanceManager` - VR performance optimizations
- `QuestAnimationSpecs` - Optimized animation specifications

## Building for Quest

### Prerequisites
1. Android Studio with Quest development tools
2. Meta Quest Developer Hub account
3. Quest device in Developer Mode

### Build Commands

#### Debug Build
```bash
./gradlew assembleQuestDebug
```

#### Release Build
```bash
./gradlew assembleQuestRelease
```

#### Bundle for Store
```bash
./gradlew bundleQuestRelease
```

### Build Configuration

The Quest flavor is configured in:
- `app/build.gradle.kts` - Main app configuration
- `data/location/build.gradle.kts` - Location module configuration
- `app/src/quest/AndroidManifest.xml` - Quest-specific manifest

## Installation & Testing

### Sideloading to Quest

1. **Enable Developer Mode** on your Quest device
2. **Connect via ADB**:
   ```bash
   adb connect <quest-ip-address>:5555
   adb install app-quest-debug.apk
   ```

3. **Launch the app** from the Quest home screen

### Testing Checklist

#### Controller Testing
- [ ] Navigation works with Quest controllers
- [ ] All buttons have 48dp+ touch targets
- [ ] Focus indicators are visible
- [ ] Scrolling works smoothly

#### Hand Tracking Testing
- [ ] Touch targets are large enough for hand tracking
- [ ] No edge gestures that conflict with Quest
- [ ] UI elements are properly spaced

#### Performance Testing
- [ ] App runs smoothly at 72/90/120Hz
- [ ] No frame drops during animations
- [ ] Memory usage is optimized
- [ ] Battery drain is minimal

#### Location Testing
- [ ] IP geolocation works without permissions
- [ ] Manual location selection works
- [ ] Privacy settings are clear
- [ ] Fallback to default location works

## Quest Store Submission

### Store Requirements

#### Manifest Requirements
- `android:screenOrientation="landscape"` - Required for VR panels
- VR-specific features declared
- No Google Play Services dependencies

#### Performance Requirements
- Smooth 72/90/120Hz performance
- Minimal battery usage
- No crashes or ANRs
- Responsive UI interactions

#### Content Requirements
- Clear privacy policy for IP geolocation
- Appropriate content rating
- No prohibited content

### Submission Process

1. **Build Release Bundle**:
   ```bash
   ./gradlew bundleQuestRelease
   ```

2. **Upload to Meta Quest Developer Hub**:
   - Go to [developer.oculus.com](https://developer.oculus.com)
   - Create new app submission
   - Upload the AAB file
   - Fill out store listing details

3. **Store Listing**:
   - App name: "Wisp Weather"
   - Description: Include VR panel optimization details
   - Screenshots: Show app running in VR environment
   - Privacy policy: Detail IP geolocation usage

4. **Review Process**:
   - Meta will review for Quest compatibility
   - Performance testing on Quest devices
   - Content and policy compliance

## Configuration Files

### Quest-Specific Manifest
```xml
<!-- app/src/quest/AndroidManifest.xml -->
<uses-feature android:name="android.hardware.vr.headtracking" />
<uses-feature android:name="android.software.vr.mode" />
<activity android:screenOrientation="landscape" />
```

### Build Configuration
```kotlin
// app/build.gradle.kts
productFlavors {
    create("quest") {
        dimension = "platform"
        applicationIdSuffix = ".quest"
        versionNameSuffix = "-quest"
    }
}
```

## Troubleshooting

### Common Issues

#### Build Failures
- **Issue**: Play Services dependencies in Quest build
- **Solution**: Check flavor-specific dependencies in `data/location/build.gradle.kts`

#### Location Not Working
- **Issue**: App can't get location on Quest
- **Solution**: Verify IP geolocation is enabled in settings

#### Performance Issues
- **Issue**: Frame drops or stuttering
- **Solution**: Check animation durations and background tasks

#### Controller Issues
- **Issue**: Controllers not working properly
- **Solution**: Verify touch targets are 48dp+ and focusable

### Debug Commands

```bash
# Check Quest device connection
adb devices

# View logs
adb logcat | grep Wisp

# Install and launch
adb install -r app-quest-debug.apk
adb shell am start -n com.example.wisp.quest/.MainActivity
```

## Development Tips

### VR Best Practices
1. **Large Touch Targets**: Minimum 48dp for controller interaction
2. **Clear Focus Indicators**: Visual feedback for navigation
3. **Optimized Animations**: Keep under 300ms for smooth performance
4. **Landscape Layout**: Design for 1200-1600px wide panels
5. **Privacy First**: Clear messaging about IP geolocation

### Performance Optimization
1. **Minimize Background Tasks**: Use throttled intervals
2. **Optimize Animations**: Use Quest-specific animation specs
3. **Memory Management**: Regular garbage collection
4. **Battery Optimization**: Reduce wake locks and background work

### Testing Strategy
1. **Regular Testing**: Test on actual Quest devices
2. **Performance Monitoring**: Use Quest performance tools
3. **User Testing**: Get feedback from Quest users
4. **Iterative Improvement**: Regular updates based on feedback

## Support & Resources

### Meta Quest Developer Resources
- [Quest Developer Hub](https://developer.oculus.com)
- [Quest Platform Documentation](https://developer.oculus.com/documentation/)
- [Quest Store Guidelines](https://developer.oculus.com/distribute/)

### Community
- [Meta Quest Developer Forums](https://forums.oculusvr.com/)
- [Quest Reddit Community](https://reddit.com/r/OculusQuest)

### Technical Support
- Check build logs for specific errors
- Use Quest performance profiling tools
- Test on multiple Quest device generations

---

## Quick Start Checklist

- [ ] Clone repository and switch to Quest branch
- [ ] Build Quest debug APK: `./gradlew assembleQuestDebug`
- [ ] Enable Developer Mode on Quest device
- [ ] Install APK via ADB: `adb install app-quest-debug.apk`
- [ ] Test basic functionality and location services
- [ ] Build release bundle: `./gradlew bundleQuestRelease`
- [ ] Submit to Meta Quest Developer Hub
- [ ] Monitor review process and respond to feedback

For detailed implementation, see the source code in the `app/src/quest/` and `data/location/src/quest/` directories.
