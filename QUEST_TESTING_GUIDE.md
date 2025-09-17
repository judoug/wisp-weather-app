# Quest Testing Guide

This guide covers testing the Wisp Weather App on Meta Quest devices for VR/AR panel applications.

## Prerequisites

### Hardware Requirements
- Meta Quest 2, Quest 3, or Quest Pro
- Quest controllers
- USB-C cable for ADB connection
- WiFi network for IP geolocation testing

### Software Requirements
- Android Studio with Quest development tools
- Meta Quest Developer Hub account
- ADB (Android Debug Bridge)
- Quest device in Developer Mode

## Setup

### 1. Enable Developer Mode
1. Open Quest mobile app
2. Go to Settings > Device > Developer Mode
3. Toggle Developer Mode ON
4. Restart your Quest device

### 2. Enable USB Debugging
1. Put on Quest headset
2. Go to Settings > System > Developer
3. Enable "USB Connection Dialog"
4. Enable "USB Debugging"

### 3. Connect via ADB
```bash
# Connect Quest to computer via USB-C
adb devices

# If device shows as "unauthorized", accept the prompt in Quest
# Device should show as "device" when properly connected
```

## Testing Procedures

### 1. Build and Install

#### Debug Build
```bash
# Build debug APK
./build-quest.sh debug

# Install to Quest
./build-quest.sh install
```

#### Release Build
```bash
# Build release APK
./build-quest.sh release

# Install manually
adb install -r app/build/outputs/apk/quest/release/app-quest-release.apk
```

### 2. Controller Testing

#### Navigation Testing
- [ ] **Home Screen Navigation**: Use controller to navigate between screens
- [ ] **Button Focus**: Verify focus indicators appear on buttons
- [ ] **Touch Targets**: Ensure all buttons are 48dp+ in size
- [ ] **Scrolling**: Test vertical scrolling with controller
- [ ] **Back Navigation**: Test back button functionality

#### Interaction Testing
- [ ] **Settings Toggle**: Test IP location toggle switch
- [ ] **Manual Location**: Test manual location selection
- [ ] **Search Function**: Test location search with controller
- [ ] **Refresh Action**: Test pull-to-refresh functionality

### 3. Hand Tracking Testing

#### Touch Interaction
- [ ] **Touch Targets**: Verify buttons are large enough for hand tracking
- [ ] **Gesture Recognition**: Test basic touch gestures
- [ ] **Edge Gestures**: Ensure no conflicting edge gestures
- [ ] **Precision**: Test precise touch interactions

#### Hand Tracking Specific
- [ ] **Hand Visibility**: Ensure hands are visible during interaction
- [ ] **Tracking Accuracy**: Test tracking accuracy with different hand positions
- [ ] **Gesture Conflicts**: Verify no conflicts with Quest system gestures

### 4. Location Services Testing

#### IP Geolocation
- [ ] **IP Detection**: Verify IP geolocation works without permissions
- [ ] **Accuracy**: Check location accuracy (should be approximate)
- [ ] **Privacy Notice**: Verify privacy messaging is clear
- [ ] **Toggle Function**: Test IP location enable/disable

#### Manual Location
- [ ] **Search Function**: Test location search functionality
- [ ] **Selection**: Test manual location selection
- [ ] **Persistence**: Verify selected location persists
- [ ] **Fallback**: Test fallback to default location

#### Privacy Testing
- [ ] **Permission Requests**: Verify no location permission requests
- [ ] **Privacy Copy**: Check privacy messaging is clear
- [ ] **Data Usage**: Verify minimal data usage
- [ ] **User Control**: Test user control over location settings

### 5. Performance Testing

#### Frame Rate Testing
- [ ] **72Hz Mode**: Test performance at 72Hz refresh rate
- [ ] **90Hz Mode**: Test performance at 90Hz refresh rate
- [ ] **120Hz Mode**: Test performance at 120Hz refresh rate
- [ ] **Frame Drops**: Monitor for frame drops during animations

#### Memory Testing
- [ ] **Memory Usage**: Monitor memory usage during app usage
- [ ] **Memory Leaks**: Test for memory leaks during extended use
- [ ] **Garbage Collection**: Verify proper garbage collection
- [ ] **Low Memory**: Test behavior under low memory conditions

#### Battery Testing
- [ ] **Battery Drain**: Monitor battery usage during app usage
- [ ] **Background Activity**: Verify minimal background activity
- [ ] **Wake Locks**: Check for unnecessary wake locks
- [ ] **Thermal Management**: Monitor device temperature

### 6. UI/UX Testing

#### Layout Testing
- [ ] **Panel Dimensions**: Verify UI works on 1200-1600px wide panels
- [ ] **Landscape Orientation**: Test landscape-only orientation
- [ ] **Responsive Design**: Test UI responsiveness
- [ ] **Content Scaling**: Verify content scales properly

#### Accessibility Testing
- [ ] **Focus Indicators**: Verify focus indicators are visible
- [ ] **Color Contrast**: Test color contrast for readability
- [ ] **Text Size**: Verify text is readable in VR
- [ ] **Navigation**: Test keyboard/controller navigation

#### VR Comfort Testing
- [ ] **Motion Sickness**: Test for motion sickness triggers
- [ ] **Eye Strain**: Monitor for eye strain during use
- [ ] **Comfort Settings**: Test VR comfort settings
- [ ] **Session Length**: Test extended usage sessions

## Test Scenarios

### Scenario 1: First Launch
1. Launch app for first time
2. Verify IP geolocation is enabled by default
3. Check privacy notice is displayed
4. Verify weather data loads
5. Test basic navigation

### Scenario 2: Location Settings
1. Navigate to settings
2. Toggle IP location off
3. Verify fallback to default location
4. Toggle IP location back on
5. Verify IP location works again

### Scenario 3: Manual Location
1. Go to settings
2. Select "Select Location Manually"
3. Search for a city
4. Select the city
5. Verify weather updates for new location

### Scenario 4: Extended Use
1. Use app for 30+ minutes
2. Monitor performance metrics
3. Check for memory leaks
4. Verify battery usage
5. Test app stability

### Scenario 5: Controller Navigation
1. Use only Quest controllers
2. Navigate through all screens
3. Test all interactive elements
4. Verify focus indicators work
5. Test accessibility features

## Performance Metrics

### Target Metrics
- **Frame Rate**: 72/90/120 FPS with <5% frame drops
- **Memory Usage**: <200MB peak usage
- **Battery Drain**: <5% per hour of usage
- **Load Time**: <3 seconds to first weather data
- **Response Time**: <100ms for UI interactions

### Monitoring Tools
- Quest Performance HUD
- Android Studio Profiler
- ADB logcat for debugging
- Quest Developer Hub analytics

## Common Issues & Solutions

### Issue: App Won't Install
**Solution**: 
- Verify Developer Mode is enabled
- Check USB debugging is enabled
- Accept USB debugging prompt in Quest
- Try different USB cable

### Issue: Location Not Working
**Solution**:
- Check WiFi connection
- Verify IP geolocation is enabled
- Test manual location selection
- Check network connectivity

### Issue: Performance Issues
**Solution**:
- Check Quest refresh rate settings
- Monitor memory usage
- Test on different Quest models
- Verify no background apps running

### Issue: Controller Not Working
**Solution**:
- Check controller battery level
- Verify controller pairing
- Test controller in other apps
- Check focus indicators are visible

## Reporting Issues

### Issue Template
```
**Quest Model**: Quest 2/3/Pro
**OS Version**: [Quest OS version]
**App Version**: [App version]
**Issue Description**: [Detailed description]
**Steps to Reproduce**: [Step-by-step instructions]
**Expected Behavior**: [What should happen]
**Actual Behavior**: [What actually happens]
**Screenshots/Logs**: [If applicable]
```

### Log Collection
```bash
# Collect logs during testing
adb logcat -c  # Clear logs
# Reproduce issue
adb logcat > quest_testing_logs.txt
```

## Testing Checklist

### Pre-Release Testing
- [ ] All controller interactions work
- [ ] Hand tracking works properly
- [ ] Location services function correctly
- [ ] Performance meets targets
- [ ] No crashes or ANRs
- [ ] Privacy requirements met
- [ ] UI scales properly for VR panels
- [ ] Battery usage is optimized
- [ ] Memory usage is reasonable
- [ ] App works in all Quest refresh rates

### Store Submission Testing
- [ ] Release build works correctly
- [ ] All features function in release mode
- [ ] Performance is optimized
- [ ] No debug information exposed
- [ ] App meets Quest Store requirements
- [ ] Privacy policy is accurate
- [ ] Content rating is appropriate
- [ ] Store listing is complete

---

## Quick Testing Commands

```bash
# Build and test debug version
./build-quest.sh debug
./build-quest.sh install

# Build release version
./build-quest.sh release

# Build for store submission
./build-quest.sh bundle

# Monitor logs
adb logcat | grep Wisp

# Check device connection
adb devices

# Launch app
adb shell am start -n com.example.wisp.quest/.MainActivity
```

For detailed testing procedures, refer to the main QUEST_PORT.md guide.
