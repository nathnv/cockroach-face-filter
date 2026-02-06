# Testing Guide - Cockroach Face Filter Android App

## Test Environment Setup

### Recommended Test Devices

**High-End Devices** (Optimal Performance)
- Samsung Galaxy S23 Ultra (Snapdragon 8 Gen 2)
- Google Pixel 7 Pro (Tensor)
- OnePlus 11 Pro (Snapdragon 8 Gen 2)

**Mid-Range Devices** (Good Performance)
- Samsung Galaxy A53 (Exynos 1280)
- Google Pixel 6a (Tensor)
- OnePlus 10T (Snapdragon 8+ Gen 1)

**Budget Devices** (Minimum Performance)
- Samsung Galaxy A12 (Exynos 850)
- Motorola Moto G7 (Snapdragon 632)
- Xiaomi Redmi Note 9 (Snapdragon 662)

### Emulator Configuration

```bash
# Create test emulator
avdmanager create avd -n "Pixel_5_API_34" \
  -k "system-images;android-34;google_apis;x86_64" \
  -d "Pixel 5"

# Launch emulator with GPU acceleration
emulator -avd Pixel_5_API_34 -gpu auto
```

## Functional Testing

### 1. Camera & Face Detection

**Test Case 1.1: Camera Permission**
- [ ] App requests camera permission on first launch
- [ ] Permission dialog appears correctly
- [ ] Granting permission enables camera
- [ ] Denying permission shows error message

**Test Case 1.2: Face Detection**
- [ ] Face is detected when visible to camera
- [ ] Face status updates in real-time
- [ ] Multiple faces are handled correctly
- [ ] No face scenario is handled gracefully

**Test Case 1.3: Facial Landmarks**
- [ ] Landmarks are accurately tracked
- [ ] Tracking is smooth and continuous
- [ ] Landmarks follow head movement
- [ ] Landmarks work at various angles

### 2. Cockroach Animation

**Test Case 2.1: Basic Movement**
- [ ] Cockroach appears on face
- [ ] Cockroach moves naturally
- [ ] Movement is within face bounds
- [ ] Cockroach bounces off edges

**Test Case 2.2: Leg Animation**
- [ ] 6 legs animate in walking cycle
- [ ] Leg movement is synchronized
- [ ] Legs move realistically
- [ ] Leg animation speed varies with movement

**Test Case 2.3: Antenna Movement**
- [ ] Antennae wave independently
- [ ] Antenna motion is smooth
- [ ] Antennae respond to animation phase
- [ ] Antenna movement is visible

### 3. Facial Expression Reactions

**Test Case 3.1: Blink Detection**
- [ ] Cockroach reacts when user blinks
- [ ] Fear particles appear
- [ ] Cockroach runs away
- [ ] Reaction is immediate

**Test Case 3.2: Mouth Opening**
- [ ] Cockroach detects mouth opening
- [ ] Speed increases when mouth opens
- [ ] Panic animation plays
- [ ] Reaction is proportional to mouth opening

**Test Case 3.3: Head Movement**
- [ ] Cockroach follows head turn
- [ ] Cockroach adjusts position
- [ ] Cockroach maintains face contact
- [ ] Movement is smooth

### 4. Recording & Screenshots

**Test Case 4.1: Video Recording**
- [ ] Record button starts recording
- [ ] Recording indicator appears
- [ ] Video is saved to gallery
- [ ] Video quality is acceptable
- [ ] Audio is recorded with video
- [ ] Recording stops correctly

**Test Case 4.2: Screenshot Capture**
- [ ] Screenshot button captures image
- [ ] Image is saved to gallery
- [ ] Image quality is good
- [ ] Screenshot includes cockroach
- [ ] Multiple screenshots work

**Test Case 4.3: Gallery Integration**
- [ ] Saved videos appear in gallery
- [ ] Saved screenshots appear in gallery
- [ ] Files can be deleted from app
- [ ] Gallery shows correct file count

### 5. Performance

**Test Case 5.1: FPS Monitoring**
- [ ] FPS counter displays correctly
- [ ] FPS is 30+ on modern devices
- [ ] FPS is 20+ on budget devices
- [ ] Stats button toggles display

**Test Case 5.2: Memory Usage**
- [ ] Memory usage is under 150MB
- [ ] No memory leaks after 5 minutes
- [ ] App recovers from low memory
- [ ] Garbage collection works

**Test Case 5.3: CPU Usage**
- [ ] CPU usage is reasonable
- [ ] App doesn't overheat device
- [ ] Battery drain is acceptable
- [ ] Thermal throttling doesn't occur

### 6. UI Controls

**Test Case 6.1: Filter Toggle**
- [ ] Filter ON/OFF button works
- [ ] AR overlay disappears when off
- [ ] Button text updates correctly
- [ ] Toggle is responsive

**Test Case 6.2: Button Responsiveness**
- [ ] All buttons respond to touch
- [ ] No delayed responses
- [ ] Buttons don't stick
- [ ] Visual feedback appears

**Test Case 6.3: Status Indicators**
- [ ] Camera status shows correctly
- [ ] Face status updates in real-time
- [ ] FPS display is accurate
- [ ] Status colors are visible

## Performance Testing

### Benchmark Tests

**Test: 30-Minute Continuous Usage**
```
- Start recording
- Let app run for 30 minutes
- Monitor: FPS, Memory, Temperature
- Expected: No crashes, stable performance
```

**Test: Rapid Face Detection Changes**
```
- Move face in and out of frame rapidly
- Expected: Smooth transitions, no lag
```

**Test: Low Light Conditions**
```
- Test in dark environment
- Expected: Face detection still works
```

**Test: Multiple Faces**
```
- Have 2-3 people in frame
- Expected: App handles gracefully
```

### Memory Leak Detection

```bash
# Using Android Studio Profiler
1. Open Android Studio
2. Run > Attach Debugger to Android Process
3. Open Profiler tab
4. Monitor Memory graph
5. Look for continuous increase
```

### Battery Drain Test

```bash
# Measure battery usage
1. Fully charge device
2. Run app for 1 hour
3. Measure battery percentage drop
4. Expected: <15% drain per hour
```

## Compatibility Testing

### Android Version Testing

- [ ] Android 7.0 (API 24) - Minimum
- [ ] Android 8.0 (API 26)
- [ ] Android 9.0 (API 28)
- [ ] Android 10 (API 29)
- [ ] Android 11 (API 30)
- [ ] Android 12 (API 31)
- [ ] Android 13 (API 33)
- [ ] Android 14 (API 34) - Target

### Screen Size Testing

- [ ] 5.0" phone (small)
- [ ] 5.5" phone (standard)
- [ ] 6.5" phone (large)
- [ ] 7.0" tablet (small)
- [ ] 10.0" tablet (large)

### Orientation Testing

- [ ] Portrait mode
- [ ] Landscape mode
- [ ] Orientation changes during recording
- [ ] Orientation changes during face detection

## Security Testing

### Permission Testing

- [ ] Camera permission required
- [ ] Storage permission required
- [ ] No unnecessary permissions
- [ ] Permissions can be revoked

### Data Privacy

- [ ] No data sent to external servers
- [ ] Local processing only
- [ ] No user tracking
- [ ] No analytics collection

## Stress Testing

### High Load Scenarios

**Test: Rapid Blinking**
```
- User blinks rapidly (10+ times)
- Expected: Smooth reactions, no lag
```

**Test: Extreme Head Movement**
```
- User moves head rapidly
- Expected: Cockroach follows smoothly
```

**Test: Continuous Recording**
```
- Record for 30+ minutes
- Expected: No crashes, stable quality
```

## Bug Reporting Template

```markdown
## Bug Report

**Title**: [Brief description]

**Device**: [Model, Android version]

**Steps to Reproduce**:
1. [First step]
2. [Second step]
3. [Third step]

**Expected Behavior**:
[What should happen]

**Actual Behavior**:
[What actually happened]

**Screenshots/Video**:
[Attach if applicable]

**Logs**:
[Include relevant logs]
```

## Test Results Template

```markdown
## Test Results - Version 1.0.0

**Date**: [Date]
**Tester**: [Name]
**Device**: [Model, Android version]

### Functional Tests
- [ ] Camera & Face Detection: PASS/FAIL
- [ ] Cockroach Animation: PASS/FAIL
- [ ] Facial Reactions: PASS/FAIL
- [ ] Recording: PASS/FAIL
- [ ] Screenshots: PASS/FAIL

### Performance Tests
- FPS: [Average FPS]
- Memory: [Peak memory usage]
- CPU: [Average CPU usage]

### Issues Found
1. [Issue 1]
2. [Issue 2]

### Recommendations
- [Recommendation 1]
- [Recommendation 2]
```

## Automated Testing

### Unit Tests

```kotlin
// Example unit test
@Test
fun testCockroachAnimatorUpdate() {
    val animator = CockroachAnimator(100f, 100f)
    val state = animator.update(500f, 500f, 50f, 50f)
    
    assertNotNull(state)
    assertTrue(state.x >= 50f && state.x <= 550f)
    assertTrue(state.y >= 50f && state.y <= 550f)
}
```

### Instrumented Tests

```kotlin
// Example instrumented test
@RunWith(AndroidJUnit4::class)
class CameraTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testCameraPermission() {
        // Test camera permission
    }
}
```

## Continuous Integration Testing

```yaml
# GitHub Actions workflow
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
      - run: ./gradlew test
      - run: ./gradlew connectedAndroidTest
```

---

**Last Updated**: February 2026
