# Quick Start Guide - Build APK Locally

## Option 1: Build Using Android Studio (Easiest)

### Step 1: Install Android Studio
1. Download Android Studio from [developer.android.com/studio](https://developer.android.com/studio)
2. Install on your computer
3. Launch Android Studio

### Step 2: Open the Project
1. Click "File" → "Open"
2. Navigate to the `cockroach_face_filter_android` folder
3. Click "Open"
4. Wait for Gradle to sync (may take 2-3 minutes)

### Step 3: Build APK
**For Debug APK (for testing):**
1. Click "Build" in the menu bar
2. Select "Build Bundle(s) / APK(s)" → "Build APK(s)"
3. Wait for the build to complete
4. A notification will appear with the APK location
5. Click "locate" to find the APK file

**For Release APK (for distribution):**
1. Click "Build" → "Generate Signed Bundle / APK"
2. Select "APK"
3. Create a new keystore or use existing one
4. Fill in the keystore details
5. Click "Next" and "Finish"
6. APK will be generated in `app/build/outputs/apk/release/`

### Step 4: Install on Device
1. Connect your Android phone via USB
2. Enable Developer Mode (tap Build Number 7 times in Settings)
3. Enable USB Debugging
4. In Android Studio, click "Run" → "Run 'app'"
5. Select your device
6. The app will install and launch automatically

---

## Option 2: Build Using Command Line

### Prerequisites
- Java 17+ installed
- Android SDK installed
- Gradle installed (or use the included wrapper)

### Step 1: Set Environment Variables
**On macOS/Linux:**
```bash
export ANDROID_HOME=/path/to/android/sdk
export JAVA_HOME=/path/to/java17
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH
```

**On Windows:**
```cmd
set ANDROID_HOME=C:\path\to\android\sdk
set JAVA_HOME=C:\path\to\java17
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%
```

### Step 2: Navigate to Project
```bash
cd cockroach_face_filter_android
```

### Step 3: Build APK
**Debug APK:**
```bash
./gradlew assembleDebug
```

**Release APK:**
```bash
./gradlew assembleRelease
```

### Step 4: Find APK
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

### Step 5: Install on Device
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Option 3: Build Using GitHub Actions (Cloud Build)

### Step 1: Create GitHub Repository
1. Go to [github.com/new](https://github.com/new)
2. Create a new repository (e.g., `cockroach-face-filter`)
3. Don't initialize with README

### Step 2: Push Code to GitHub
```bash
cd cockroach_face_filter_android
git init
git add .
git commit -m "Initial commit: Cockroach Face Filter Android App"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/cockroach-face-filter.git
git push -u origin main
```

### Step 3: GitHub Actions Builds Automatically
1. Go to your repository on GitHub
2. Click "Actions" tab
3. You'll see "Build Android APK" workflow running
4. Wait for it to complete (5-10 minutes)
5. Click on the completed workflow
6. Scroll down to "Artifacts"
7. Download `debug-apk` or `release-apk`

### Step 4: Extract and Install APK
1. Unzip the downloaded artifact
2. Connect your Android phone
3. Run: `adb install app-debug.apk`

---

## Troubleshooting

### "Gradle sync failed"
- Update Android Studio to latest version
- Delete `.gradle` folder and resync
- Check Java version: `java -version` (should be 17+)

### "SDK location not found"
- Create `local.properties` in project root:
  ```properties
  sdk.dir=/path/to/android/sdk
  ```

### "Build failed: Unsupported class-file format"
- Update Java to version 17 or higher
- Update Android Gradle Plugin to 8.1.0+

### "Permission denied: ./gradlew"
```bash
chmod +x gradlew
```

### "No connected devices"
- Enable USB Debugging on phone
- Install ADB drivers
- Run: `adb devices`

---

## APK File Information

### Debug APK
- **Size**: ~15-20 MB
- **Purpose**: Testing and development
- **Installation**: Direct install on device
- **Signing**: Automatic debug key
- **Performance**: Includes debug symbols

### Release APK
- **Size**: ~12-15 MB (smaller, optimized)
- **Purpose**: Distribution to Google Play Store
- **Installation**: Requires signing
- **Signing**: Custom keystore
- **Performance**: Optimized, code shrunk

---

## Next Steps

### Install on Your Phone
1. Build the APK (debug version recommended for testing)
2. Connect your Android phone
3. Enable USB Debugging
4. Run: `adb install app-debug.apk`
5. Launch "Cockroach Face Filter" from app drawer

### Share to Google Play Store
1. Build release APK
2. Sign with your keystore
3. Go to [play.google.com/console](https://play.google.com/console)
4. Create new app listing
5. Upload APK
6. Fill in store listing details
7. Submit for review

### Customize the App
- Edit `MainActivity.kt` to change behavior
- Modify colors in `app/src/main/res/values/colors.xml`
- Update strings in `app/src/main/res/values/strings.xml`
- Rebuild APK after changes

---

## Support

For detailed information, see:
- **README.md**: Feature overview and API reference
- **BUILD_GUIDE.md**: Comprehensive build instructions
- **TESTING_GUIDE.md**: Testing procedures

---

**Last Updated**: February 2026  
**Status**: Production Ready
