# 🍒 Cherry Music Player

A sleek, modern Android music player app built with **Jetpack Compose** and **Media3 ExoPlayer**, featuring a dark futuristic theme with rich plum surfaces and vibrant cherry-red accents.

---

## 🎨 Overview & Design Description

**Cherry Music Player** combines high-performance local audio playback with a striking dark futuristic UI aesthetic. Inspired by neon cyberpunk palettes and modern glassmorphism, the visual interface is built around:

- **Dark Futuristic Theme**: Deep midnight base (`#0A0A0F`) enhanced with ambient radial glow effects.
- **Plum Surfaces**: Multi-tiered plum-tinted card and container surfaces (`#1A1423` / `#140F1C`) providing visual depth and high contrast.
- **Cherry-Red Accents**: Neon cherry primary highlights (`#FF2D6F`) and deep cherry borders (`#8A1F3D`) guiding key interactive elements.
- **Edge-to-Edge Interface**: Full transparent edge-to-edge layout with dynamic glass mini-players and cohesive navigation bars.

---

## ✨ Core Features

- **Local Audio Discovery & Library**: Scans local media storage for audio files, displaying tracks with artwork, artist details, and track durations.
- **Media3 ExoPlayer Engine**: Powered by Android Jetpack Media3 for audio playback, background service lifecycle, media session support, and notification controls.
- **Now Playing & Mini Player**: Immersive full-screen playback UI with track seeking, queue management, shuffle/repeat toggles, and a persistent bottom mini-player.
- **Playlists & Favorites Management**: Create custom playlists, manage track ordering, and instantly bookmark favorite songs.
- **Instant Search & Sorting**: Real-time filtering across tracks, artists, and albums.
- **Customization & Glow Effects**: Built-in settings screen with customizable ambient theme glow, dark mode settings, and preferences saved via DataStore.
- **Jetpack Navigation 3**: Built with state-driven backstack management utilizing `androidx.navigation3`.

---

## 🚀 What's New (Latest Updates)

- **Fluid Now Playing Dock & Expand Animations**:
  - Tapping the bottom MiniPlayer triggers a smooth scale-up expansion from the bottom center into fullscreen Now Playing.
  - Exiting Now Playing (via back button, down arrow, or downward swipe gesture) executes a graceful scale-down and downward glide that docks directly back into the MiniPlayer with custom z-index foreground layering.
- **MiniPlayer Live Progress Fill**:
  - Added an integrated progress indicator line across the MiniPlayer that tracks current playback position in real time.
- **Live Library Track Glow Sync**:
  - The currently playing track in the Library highlights with an active cherry glow that synchronizes immediately when skipping tracks from the MiniPlayer or Now Playing screen.
- **Global MiniPlayer Visibility**:
  - MiniPlayer is now accessible across Playlists, Favorites, and filtered song views.
- **Manual Library Rescan & Audio Format Expansion**:
  - "Scan Device" action in Settings to instantly detect newly added music.
  - Expanded support for `.mp3`, `.m4a`, `.flac`, `.wav`, `.aac`, `.ogg`, and `.opus`.
  - Tuned minimum duration threshold down to 60 seconds.
- **Hide Song Capability**:
  - Long-press any song in the Library to hide it permanently from playback and lists.
- **Album & Artist Drill-Down**:
  - Tap any album or artist card to open a filtered song list view.

---

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/) (2.1.10)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Navigation**: Jetpack Navigation 3 (`androidx.navigation3`)
- **Audio Engine**: [Jetpack Media3 ExoPlayer](https://developer.android.com/guide/topics/media/media3) (`androidx.media3`)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) (Dagger 2.60.1)
- **Database & Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) (2.8.4) & [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/) (2.7.0)
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/library.jpg" width="250" alt="Library Screen" />
  <img src="screenshots/playlists.jpg" width="250" alt="Playlists Screen" />
  <img src="screenshots/settings.jpg" width="250" alt="Settings Screen" />
</p>

| Library | Playlists | Settings |
|---|---|---|
| Song list with search & sorting | Favorites & custom playlists | Glow, speed & equalizer |

### UI Layout Structure

```
  ┌─────────────────────────────────────────────────────────────┐
  │                    🍒 CHERRY MUSIC PLAYER                   │
  ├─────────────────────────────────────────────────────────────┤
  │  [Library Screen]        [Now Playing Screen]   [Playlists] │
  │  • Song List             • Large Artwork Glow   • Favorites │
  │  • Search Bar            • Progress / Seek bar  • Playlists │
  │  • Fast Sorting          • Play / Pause / Skip  • Song Count│
  ├─────────────────────────────────────────────────────────────┤
  │  [Mini Player Overlay]  ▸ Track Title - Artist  | ⏯ | ⏭   │
  ├─────────────────────────────────────────────────────────────┤
  │  [Navigation Bar]      • Library          • Playlists       │
  └─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Instructions to Build & Run

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended
- **JDK**: Version 17+
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/asta-JC/Cherry-MusicPlayer.git
   cd Cherry-MusicPlayer
   ```

2. **Open in Android Studio**:
   Open the root project directory in Android Studio and allow Gradle to sync.

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

5. **Install on Connected Device / Emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 License

This project is developed as part of Asta's project series. Feel free to use and adapt for personal and educational use!

---

<p align="center">
  Made with ❤️ & 🍒 by Asta
</p>
