# Android App Build & Deployment Guide

## Prerequisites

### System Requirements

- **OS**: Windows, macOS, or Linux
- **RAM**: 8GB minimum (16GB recommended)
- **Disk Space**: 10GB minimum
- **Java**: JDK 11 or higher

### Software Installation

1. **Install Android Studio**
   - Download from [developer.android.com](https://developer.android.com/studio)
   - Install Android SDK 34
   - Install Kotlin plugin

2. **Install Android SDK Components**
   ```bash
   # Using Android SDK Manager in Android Studio
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Android SDK Platform-Tools
   ```

3. **Install Gradle** (usually included with Android Studio)
   ```bash
   gradle --version  # Should show 8.1.0 or higher
   ```

## Project Setup

### 1. Clone/Extract Project

```bash
cd cockroach_face_filter_android
```

### 2. Configure Local Properties

Create `local.properties` in project root:

```properties
sdk.dir=/path/to/android/sdk
ndk.dir=/path/to/android/ndk
```

### 3. Sync Gradle

```bash
./gradlew sync
# or in Android Studio: File > Sync Now
```

## Building the App

### Debug Build

```bash
# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build

#### Step 1: Create Keystore

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release_key
```

#### Step 2: Configure Signing in build.gradle.kts

```kotlin
signingConfigs {
    release {
        storeFile = file("release.keystore")
        storePassword = "your_keystore_password"
        keyAlias = "release_key"
        keyPassword = "your_key_password"
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

#### Step 3: Build Release APK

```bash
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release.apk
```

### Build AAB (Android App Bundle)

For Google Play Store submission:

```bash
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

## Testing

### Unit Tests

```bash
./gradlew test
```

### Instrumented Tests

```bash
# Connect device or start emulator
./gradlew connectedAndroidTest
```

### Manual Testing Checklist

- [ ] Camera access permission granted
- [ ] Face detection working
- [ ] Cockroach animation smooth (60 FPS)
- [ ] Recording starts/stops correctly
- [ ] Screenshots saved to gallery
- [ ] Gallery sharing works
- [ ] Performance stats display correctly
- [ ] App handles low memory gracefully
- [ ] No crashes on rapid face detection changes
- [ ] Audio recording with video works

## Deployment

### Local Device/Emulator

```bash
# Install debug build
./gradlew installDebug

# Or use Android Studio: Run > Run 'app'
```

### Google Play Store

1. **Create Google Play Developer Account**
   - Visit [play.google.com/console](https://play.google.com/console)
   - Pay $25 registration fee
   - Complete developer profile

2. **Create App Listing**
   - App name: "Cockroach Face Filter"
   - Category: Entertainment/Filters
   - Content rating: Everyone
   - Privacy policy: Required

3. **Upload AAB**
   - Go to Release > Production
   - Upload app-release.aab
   - Add release notes
   - Submit for review

4. **Configure Store Listing**
   - Add screenshots (min 2, max 8)
   - Add app description
   - Add promotional graphics
   - Set pricing (free recommended)

### Alternative Stores

- **Amazon Appstore**: Similar process to Google Play
- **Samsung Galaxy Store**: For Samsung devices
- **F-Droid**: For open-source distribution

## Performance Optimization

### ProGuard/R8 Configuration

The app includes ProGuard rules in `proguard-rules.pro`:

```bash
# Verify ProGuard is working
./gradlew assembleRelease --info | grep proguard
```

### APK Size Optimization

```bash
# Analyze APK size
./gradlew analyzeReleaseBundle

# Enable code shrinking
android.enableR8=true
```

### Memory Optimization

- Implement image caching
- Use bitmap pooling
- Enable ProGuard code shrinking
- Remove unused resources

## Troubleshooting

### Build Errors

**Error: "Gradle sync failed"**
```bash
# Clean and rebuild
./gradlew clean
./gradlew sync
```

**Error: "SDK location not found"**
```bash
# Create local.properties with SDK path
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

### Runtime Errors

**Error: "Camera permission denied"**
- Check AndroidManifest.xml permissions
- Verify runtime permissions in MainActivity
- Test on device with Android 6.0+

**Error: "TensorFlow Lite model not found"**
- Ensure face_landmarker.tflite is in assets/
- Check model file size and format

**Error: "Out of memory"**
- Reduce image resolution
- Implement bitmap recycling
- Enable memory optimization in ProGuard

### Performance Issues

**Low FPS**
- Check device specifications
- Reduce particle count
- Disable some effects
- Use performance monitor

**High memory usage**
- Clear bitmap cache
- Reduce frame buffer size
- Implement garbage collection

## Continuous Integration

### GitHub Actions Example

```yaml
name: Build Android App

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '11'
      - run: ./gradlew build
      - run: ./gradlew assembleRelease
```

## Version Management

Update version in `build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2  // Increment for each release
    versionName = "1.1.0"  // Semantic versioning
}
```

## Release Checklist

- [ ] Update version code and name
- [ ] Update README.md with new features
- [ ] Test on multiple devices
- [ ] Run ProGuard/R8 optimization
- [ ] Generate release APK/AAB
- [ ] Create release notes
- [ ] Tag git commit with version
- [ ] Upload to distribution platform
- [ ] Monitor crash reports

## Support

For build issues or questions:

1. Check Android Studio logs
2. Review Gradle output
3. Consult Android documentation
4. Check TensorFlow Lite documentation

---

**Last Updated**: February 2026
