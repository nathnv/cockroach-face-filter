# Cockroach Face Filter - Android App

A native Android application that renders a realistic animated cockroach crawling across the user's face using TensorFlow Lite for face detection and AR rendering.

## Features

### Core Functionality

- **Real-Time Face Detection**: TensorFlow Lite-based face landmark detection
- **Realistic Cockroach Animation**: High-quality 2D cockroach with natural movement
- **Leg Animation**: 6-legged walking cycle with realistic motion
- **Antenna Movement**: Wavy antenna animations
- **Facial Expression Reactions**: Responds to blinks, mouth opening, and head movements
- **Performance Optimization**: Adaptive quality rendering based on device performance

### Recording & Sharing

- **Video Recording**: Record AR filter in action (MP4 format)
- **Screenshot Capture**: Save PNG screenshots to gallery
- **Gallery Integration**: Access saved media directly from app
- **Social Media Sharing**: Share to TikTok, Instagram, Facebook, Twitter, WhatsApp
- **Performance Stats**: Real-time FPS and performance monitoring

## Technical Stack

- **Language**: Kotlin
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Face Detection**: TensorFlow Lite
- **Camera**: AndroidX Camera API
- **Video Recording**: MediaRecorder API
- **Media Storage**: Android MediaStore API

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/cockroachfilter/
│   │   ├── MainActivity.kt                 # Main activity
│   │   ├── FaceDetector.kt                 # TensorFlow Lite face detection
│   │   ├── CockroachAnimator.kt            # Cockroach animation system
│   │   ├── ParticleSystem.kt               # Particle effects
│   │   ├── ARSurfaceView.kt                # AR rendering surface
│   │   ├── VideoRecorder.kt                # Video recording manager
│   │   ├── ScreenshotManager.kt            # Screenshot capture
│   │   ├── GalleryManager.kt               # Gallery and sharing
│   │   └── PerformanceMonitor.kt           # Performance tracking
│   └── res/
│       ├── layout/
│       │   └── activity_main.xml           # Main layout
│       ├── drawable/
│       │   ├── button_primary_background.xml
│       │   ├── button_secondary_background.xml
│       │   ├── status_badge_background.xml
│       │   └── control_panel_background.xml
│       ├── values/
│       │   ├── strings.xml
│       │   ├── colors.xml
│       │   └── themes.xml
│       └── xml/
│           ├── file_paths.xml
│           ├── data_extraction_rules.xml
│           └── backup_rules.xml
├── build.gradle.kts
├── proguard-rules.pro
└── AndroidManifest.xml
```

## Installation & Setup

### Prerequisites

- Android Studio Flamingo or later
- Android SDK 34
- Kotlin 1.9.10
- Gradle 8.1.0

### Build Instructions

1. **Clone or extract the project**
   ```bash
   cd cockroach_face_filter_android
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run on device/emulator**
   ```bash
   ./gradlew installDebug
   ```

### Permissions Required

The app requires the following permissions:

- **CAMERA**: Access to front-facing camera
- **READ_EXTERNAL_STORAGE**: Read media from device
- **WRITE_EXTERNAL_STORAGE**: Save videos and screenshots
- **INTERNET**: Download TensorFlow models (if needed)

## How It Works

### Face Detection Pipeline

1. **Camera Input**: Captures frames from front-facing camera at 30 FPS
2. **Face Detection**: TensorFlow Lite detects facial landmarks
3. **Expression Analysis**: Calculates facial expressions (mouth, eyes, head turn)
4. **Cockroach Animation**: Updates cockroach position and animation state
5. **AR Rendering**: Draws cockroach overlay on camera feed
6. **Particle Effects**: Creates dust/fear particles based on reactions

### Cockroach Behavior

- **Walking**: Random movement across face with occasional pauses
- **Leg Animation**: 6-leg walking cycle synchronized with movement
- **Antenna Movement**: Wavy motion independent of body
- **Reactions**:
  - **Blink**: Cockroach runs away with fear particles
  - **Mouth Open**: Cockroach panics and increases speed
  - **Head Turn**: Cockroach adjusts position and direction
  - **Head Shake**: Cockroach becomes curious

### Performance Optimization

The app includes adaptive quality rendering:

- **FPS Monitoring**: Real-time frame rate tracking
- **Memory Management**: Efficient resource cleanup
- **Adaptive Effects**: Reduces particle count and effects on low-end devices
- **GPU Acceleration**: Leverages TensorFlow Lite GPU delegate when available

## API Reference

### FaceDetector

```kotlin
val detector = FaceDetector(context)
val result = detector.detectFaceLandmarks(bitmap)
if (result.faceDetected) {
    val landmarks = result.landmarks
    val expressions = detector.calculateFacialExpressions(landmarks)
}
detector.release()
```

### CockroachAnimator

```kotlin
val animator = CockroachAnimator(initialX, initialY)
val state = animator.update(faceWidth, faceHeight, faceX, faceY, reactions)
animator.reset(x, y)
```

### ParticleSystem

```kotlin
val particles = ParticleSystem(maxParticles = 100)
particles.createDustParticles(x, y, count = 3)
particles.createFearParticles(x, y, count = 5)
particles.update()
particles.draw(canvas)
```

### VideoRecorder

```kotlin
val recorder = VideoRecorder(context)
val file = recorder.createVideoFile()
recorder.startRecording(file)
// ... record ...
recorder.stopRecording()
recorder.release()
```

### GalleryManager

```kotlin
val gallery = GalleryManager(context)
gallery.shareToSocialMedia(file, GalleryManager.SocialMediaPlatform.TIKTOK)
val videos = gallery.getSavedVideos()
val screenshots = gallery.getSavedScreenshots()
```

## Performance Targets

- **FPS**: 30-60 FPS on modern devices
- **Face Detection Latency**: <100ms
- **Memory Usage**: <100MB
- **Supported Devices**: Android 7.0+ (API 24+)

## Device Compatibility

### Recommended Devices

- Snapdragon 855 or higher
- 4GB+ RAM
- Android 10+

### Minimum Requirements

- Snapdragon 650 or equivalent
- 2GB RAM
- Android 7.0 (API 24)

## Known Limitations

- Face tracking works best with good lighting
- Performance varies on older devices
- Some effects may be disabled on low-end devices
- Requires front-facing camera

## Building for Release

1. **Configure signing**
   ```bash
   # Create keystore
   keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```

2. **Update build.gradle.kts**
   ```kotlin
   signingConfigs {
       release {
           storeFile = file("release.keystore")
           storePassword = "your_password"
           keyAlias = "release"
           keyPassword = "your_password"
       }
   }
   ```

3. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```

## Troubleshooting

### Camera Not Working

- Check camera permissions in app settings
- Ensure no other app is using the camera
- Restart the device
- Try on a different device

### Low Performance

- Check FPS counter (Stats button)
- Close other apps
- Reduce screen brightness
- Improve lighting conditions
- Use a newer device

### Face Not Detected

- Ensure face is clearly visible
- Improve lighting
- Position face closer to camera
- Check camera focus

## Future Enhancements

- [ ] Multiple cockroaches on screen
- [ ] Customizable cockroach colors and sizes
- [ ] Sound effects and audio feedback
- [ ] AR cloud features
- [ ] Multiplayer mode
- [ ] Filter presets and themes
- [ ] Advanced gesture recognition
- [ ] Real-time video filters

## Contributing

This is a demonstration project. For improvements or bug reports, please contact the development team.

## License

MIT License - See LICENSE file for details

## Credits

- **Face Detection**: TensorFlow Lite by Google
- **Camera API**: AndroidX Camera Library
- **UI Components**: Material Design 3

## Support

For issues or questions, please refer to the troubleshooting section or contact support.

---

**Version**: 1.0.0  
**Last Updated**: February 2026  
**Status**: Production Ready
