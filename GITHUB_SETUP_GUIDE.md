# GitHub Actions Cloud Build Setup Guide

## 🚀 Build APK in the Cloud (Completely Free!)

This guide shows you how to use GitHub Actions to automatically build your APK in the cloud without needing Android Studio on your computer.

---

## Step 1: Create a GitHub Account (If You Don't Have One)

1. Go to [github.com](https://github.com)
2. Click "Sign up"
3. Enter your email and create a password
4. Verify your email
5. You're done! (GitHub Actions is free for public repositories)

---

## Step 2: Create a New Repository

1. Log in to [github.com](https://github.com)
2. Click the "+" icon in top-right corner
3. Select "New repository"
4. Fill in:
   - **Repository name**: `cockroach-face-filter`
   - **Description**: "Cockroach Face Filter AR App for Android"
   - **Visibility**: Select "Public" (free GitHub Actions)
   - **Initialize**: Leave unchecked
5. Click "Create repository"

---

## Step 3: Push Code to GitHub

### On Windows:

1. **Install Git** from [git-scm.com](https://git-scm.com)
2. **Open Command Prompt** and navigate to the project:
   ```cmd
   cd C:\path\to\cockroach_face_filter_android
   ```
3. **Initialize Git**:
   ```cmd
   git init
   git add .
   git commit -m "Initial commit: Cockroach Face Filter Android App"
   git branch -M main
   ```
4. **Add Remote** (replace YOUR_USERNAME):
   ```cmd
   git remote add origin https://github.com/YOUR_USERNAME/cockroach-face-filter.git
   ```
5. **Push Code**:
   ```cmd
   git push -u origin main
   ```
6. **Enter your GitHub credentials** when prompted

### On macOS/Linux:

1. **Open Terminal** and navigate to the project:
   ```bash
   cd /path/to/cockroach_face_filter_android
   ```
2. **Initialize Git**:
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Cockroach Face Filter Android App"
   git branch -M main
   ```
3. **Add Remote** (replace YOUR_USERNAME):
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/cockroach-face-filter.git
   ```
4. **Push Code**:
   ```bash
   git push -u origin main
   ```
5. **Enter your GitHub credentials** when prompted

---

## Step 4: GitHub Actions Builds Automatically

Once you push the code, GitHub Actions will automatically start building!

1. Go to your repository on GitHub: `https://github.com/YOUR_USERNAME/cockroach-face-filter`
2. Click the **"Actions"** tab
3. You'll see **"Build Android APK"** workflow running
4. Wait for it to complete (usually 5-10 minutes)

---

## Step 5: Download Your APK

### When Build Completes:

1. Go to your repository on GitHub
2. Click the **"Actions"** tab
3. Click the **"Build Android APK"** workflow (green checkmark means success)
4. Scroll down to **"Artifacts"** section
5. You'll see two options:
   - **debug-apk**: For testing (recommended)
   - **release-apk**: For distribution (requires signing)
6. Click **"debug-apk"** to download
7. Unzip the downloaded file
8. You'll find: `app-debug.apk`

---

## Step 6: Install APK on Your Phone

### Using ADB (Command Line):

1. **Install ADB** (Android Debug Bridge):
   - Windows: Download from [developer.android.com](https://developer.android.com/studio/releases/platform-tools)
   - macOS: `brew install android-platform-tools`
   - Linux: `sudo apt-get install android-sdk-platform-tools`

2. **Connect your phone** via USB
3. **Enable USB Debugging**:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → Developer Options
   - Enable "USB Debugging"

4. **Install APK**:
   ```bash
   adb install app-debug.apk
   ```

5. **Launch app**:
   - Open "Cockroach Face Filter" from your app drawer
   - Grant camera permission when prompted

### Using File Manager (Easier):

1. **Download APK** to your phone
2. **Open file manager** on your phone
3. **Navigate to Downloads**
4. **Tap the APK file**
5. **Tap "Install"**
6. **Allow installation from unknown sources** if prompted
7. **Done!** App will appear in your app drawer

---

## Step 7: Test the App

1. **Launch the app**
2. **Allow camera permission**
3. **Position your face in front of the camera**
4. **You should see a cockroach crawling on your face!**

### Test Features:
- [ ] Face detection works
- [ ] Cockroach animation is smooth
- [ ] Blink and cockroach reacts
- [ ] Open mouth and cockroach runs away
- [ ] Record button captures video
- [ ] Screenshot button saves image
- [ ] Gallery shows saved files

---

## 📊 Build Status Monitoring

### Check Build Status Anytime:

1. Go to your GitHub repository
2. Click **"Actions"** tab
3. See all builds and their status:
   - 🟢 **Green checkmark**: Build successful
   - 🔴 **Red X**: Build failed
   - 🟡 **Yellow dot**: Build in progress

### View Build Logs:

1. Click on a workflow run
2. Click on "Build" job
3. Expand sections to see detailed logs
4. Useful for troubleshooting if build fails

---

## 🔄 Rebuild Anytime

### To Build Again:

1. Go to your repository
2. Click **"Actions"** tab
3. Click **"Build Android APK"** workflow
4. Click **"Run workflow"** button
5. Click **"Run workflow"** again in the popup
6. Wait 5-10 minutes for build to complete
7. Download APK from Artifacts

---

## 🔧 Customizing the Build

### Change App Name:

1. Go to your repository
2. Click **"Code"** tab
3. Navigate to: `app/src/main/res/values/strings.xml`
4. Click the pencil icon to edit
5. Change: `<string name="app_name">Cockroach Face Filter</string>`
6. Scroll down and click **"Commit changes"**
7. GitHub Actions will automatically rebuild!

### Change App Colors:

1. Navigate to: `app/src/main/res/values/colors.xml`
2. Edit color values
3. Commit changes
4. GitHub Actions rebuilds automatically

### Modify Cockroach Behavior:

1. Navigate to: `app/src/main/java/com/example/cockroachfilter/CockroachAnimator.kt`
2. Edit the code
3. Commit changes
4. GitHub Actions rebuilds automatically

---

## ❓ Troubleshooting

### Build Failed: "Java 17 required"
- The workflow already uses Java 17
- If still fails, check the build logs in Actions tab
- Contact GitHub support if issue persists

### Build Failed: "SDK not found"
- The workflow automatically installs Android SDK
- Check build logs for details
- Try running workflow again

### APK Download Shows "Artifact Expired"
- Artifacts expire after 90 days
- Just run the workflow again to rebuild
- Or keep the APK file on your computer

### APK Won't Install
- Enable "Unknown Sources" in phone settings
- Or use: `adb install app-debug.apk`
- Ensure phone has enough storage space

### Face Detection Not Working
- Ensure good lighting
- Position face closer to camera
- Check camera permissions
- Try restarting the app

---

## 📝 Git Workflow (For Updates)

### To Update Code and Rebuild:

1. **Edit files locally** on your computer
2. **Commit and push**:
   ```bash
   git add .
   git commit -m "Description of changes"
   git push
   ```
3. **GitHub Actions automatically rebuilds**
4. **Download updated APK** from Actions

### Example: Update App Name

```bash
# Edit strings.xml locally
# Then:
git add app/src/main/res/values/strings.xml
git commit -m "Update app name"
git push
# GitHub Actions rebuilds automatically!
```

---

## 🎯 Quick Reference

| Task | Steps |
|------|-------|
| **Create Repository** | GitHub.com → New Repository → Fill form |
| **Push Code** | `git init` → `git add .` → `git commit` → `git push` |
| **Start Build** | Push code (automatic) or click "Run workflow" |
| **Download APK** | Actions tab → Completed workflow → Artifacts |
| **Install APK** | Download → `adb install app-debug.apk` |
| **Update Code** | Edit locally → `git push` → Auto-rebuild |

---

## 🚀 Next Steps

1. ✅ Create GitHub account
2. ✅ Create repository
3. ✅ Push code to GitHub
4. ✅ Wait for build to complete
5. ✅ Download APK
6. ✅ Install on phone
7. ✅ Test the app
8. ✅ Share with friends!

---

## 📞 Support

For help:
- Check GitHub Actions logs for build errors
- See BUILD_GUIDE.md for detailed build info
- See TESTING_GUIDE.md for testing procedures
- See README.md for feature documentation

---

## 💡 Pro Tips

- **Keep APK backup**: Download and save APK files locally
- **Use releases**: Tag commits with version numbers for organized releases
- **Monitor builds**: Check Actions tab regularly to see build history
- **Share APK**: Email APK to friends or upload to file sharing service
- **Iterate fast**: Push code changes and GitHub Actions rebuilds automatically

---

**Status**: Ready to Build  
**Build Time**: 5-10 minutes  
**Cost**: FREE (for public repositories)  
**Last Updated**: February 2026
