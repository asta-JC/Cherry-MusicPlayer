# Project Plan

Offline music player app called Cherry Music Player using Kotlin, Jetpack Compose, Material 3, Media3 ExoPlayer, Room, DataStore, Hilt, Coil, Navigation Compose. Dark only futuristic minimal theme with near-black background (#0A0A0F), dark plum surface (#1A1423), cherry-red borders (#8A1F3D), cherry pink-red accent (#FF2D6F), gradients #FF1744 to #FF2D6F. Features: Library screen (songs/albums/artists/playlists, search, sort), Now Playing screen, mini player, playlists management, settings (equalizer presets, sleep timer, playback speed, theme glow), local audio scanning via MediaStore, background playback with Media3 notification and lock-screen controls, persistence with Room & DataStore.

## Project Brief

# Project Brief: Cherry Music Player

## Features
1. **Local Audio Library & Scanning**: Automatically scan device storage using MediaStore to index and display local music organized by songs, albums, artists, and playlists, equipped with search and sorting functionality.
2. **Now Playing & Background Playback**: Immersive Now Playing screen, persistent mini-player, and background audio playback powered by Jetpack Media3 ExoPlayer, complete with system media notifications and lock-screen controls.
3. **Playlist & Queue Management**: Create, edit, and manage custom music playlists and dynamic playback queues.
4. **Playback Settings & Customization**: Configure playback speed, sleep timers, equalizer presets, and custom dark futuristic theme glow settings.

## High-Level Tech Stack
- **Language & Concurrency**: Kotlin, Coroutines, and Flow for reactive data streams.
- **UI & Theme**: Jetpack Compose, Material 3, and Coil (for image/album art loading). Dark futuristic minimal theme (#0A0A0F background, #1A1423 surface, #8A1F3D borders, #FF2D6F accents).
- **Navigation**: Jetpack Navigation 3 (State-driven navigation).
- **Adaptive Layouts**: Compose Material Adaptive library for responsive screen adaptation.
- **Media Engine**: Jetpack Media3 ExoPlayer for background playback and notification management.
- **Persistence & Storage**: Room database (for local metadata and playlists) and DataStore (for user preferences and settings).
- **Dependency Injection**: Hilt.

## Implementation Steps
**Total Duration:** 1h 32m 43s

### Task_1_CoreArchitectureAndDatabase: Set up Hilt DI, Room database for playlists and metadata, DataStore preferences, and Media3 ExoPlayer service foundation.
- **Status:** COMPLETED
- **Updates:** Successfully set up Hilt DI, Room database, DataStore preferences, and Media3 ExoPlayer service foundation. Verified build with assembleDebug.
- **Acceptance Criteria:**
  - Hilt modules configured
  - Room database entities and DAOs created
  - DataStore preferences manager set up
  - Media3 ExoPlayer service base configured
- **Duration:** 12m 17s

### Task_2_MediaStoreLibraryUI: Implement MediaStore audio scanning repository and Jetpack Compose library UI for songs, albums, and artists with search and Navigation 3.
- **Status:** COMPLETED
- **Updates:** Successfully implemented MediaStore scanning repository, LibraryViewModel, Theme (Color, Type, Theme), LibraryScreen with search, sort, filters, and Navigation 3 routing. Verified build with assembleDebug.
- **Acceptance Criteria:**
  - MediaStore scanner successfully indexes local audio
  - Library UI displays songs, albums, and artists with search
  - Jetpack Navigation 3 routing configured between library views
  - Project builds successfully
- **Duration:** 3m 47s

### Task_3_NowPlayingAndQueue: Implement immersive Now Playing screen, persistent mini-player, Media3 background playback controls, system notifications, and queue management.
- **Status:** COMPLETED
- **Updates:** Successfully implemented PlayerViewModel, MiniPlayer, NowPlayingScreen with album art rotation, red glow, gradient seekbar, playback controls, sleep timer, queue management, and PlaylistsScreen with Favorites and custom playlists. Verified build with assembleDebug.
- **Acceptance Criteria:**
  - Immersive Now Playing UI and persistent mini-player functional
  - Media3 ExoPlayer background playback and system media notification working
  - Playlist and playback queue management fully operational
  - Project builds successfully
- **Duration:** 3m 51s

### Task_4_SettingsAndStyling: Implement playback settings (speed, sleep timer), futuristic cherry-red Material 3 theme (#0A0A0F background, #FF2D6F accents), and custom theme glow UI.
- **Status:** COMPLETED
- **Updates:** Task 4 successfully implemented: SettingsViewModel and SettingsScreen created with Equalizer presets, Sleep timer, Playback speed, Theme glow toggle, and About section with footer 'Cherry Music Player // Asta v1.0'. All user accepted files integrated.
- **Acceptance Criteria:**
  - Playback speed and sleep timer features implemented
  - Futuristic cherry-red Material 3 dark theme and custom glow styling applied across screens
  - Project builds successfully
- **Duration:** 1h 12m 48s

### Task_5_RunAndVerify: Run and verify application build, stability, and adherence to requirements.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - Instruct critic_agent to verify application stability, alignment with user requirements, and report critical UI issues
- **StartTime:** 2026-10-11 22:14:25 GMT+05:00

