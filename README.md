# Cat Café Story — Android

An original, offline-first cat café management game for Android, built with Kotlin and Jetpack Compose. This is an independent recreation concept and does not include assets or source code from the itch.io game.

## Features

- Serve café guests to earn coins and neighborhood reputation.
- Adopt rescue cats, give treats, improve their moods, and grow their bond levels.
- Upgrade the café room, unlock story chapters, and complete daily goals.
- Save coins, cats, story progress, and goals locally on the device.
- Four touch-friendly sections: Café, Cats, Story, and Goals.

## Build

Requirements: Android Studio with JDK 17 and Android SDK 35 installed.

1. Open this repository in Android Studio.
2. Allow Gradle sync to complete.
3. Run the `app` configuration on an Android 8.0+ device or emulator.

To build a debug APK from a machine with Gradle installed:

```bash
gradle assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
