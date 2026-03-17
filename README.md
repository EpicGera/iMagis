# VTRÆ

VTRÆ is a modern Android application (specifically optimized for Android TV) designed for streaming media, managing IP TV playlists, and handling peer-to-peer torrent streaming efficiently.

## 🚀 Tech Stack

VTRÆ is built with modern Android development practices to ensure performance, reliability, and an excellent user experience.

- **Kotlin Coroutines & Flow**: Used extensively for asynchronous tasks like network fetching, database transactions, and background scraping without blocking the Main thread.
- **Room Database**: For robust local caching of playlists, channels, and torrent hashes, providing offline support and fast load times.
- **Edge-to-Edge UI & TV Compose**: Utilizes Jetpack Compose for TV (`androidx.tv`) and Leanback for a seamless, modern, and reactive TV interface.
- **ExoPlayer (Media3)**: For reliable and customizable video playback.
- **GitHub Actions CI/CD**: Automated workflows (`build_and_lint.yml`) ensure code quality via Android Lint and automatically assemble unsigned release APK artifacts on every push or pull request to the `main` branch.

## 🏗️ Architecture Overview

The app's logic is cleanly separated, focusing on background efficiency and resilient media ingestion.

### SyncEngine (`SyncEngine.kt`)

The `SyncEngine` acts as the single source of truth for keeping the local Room Database updated with remote playlist data.

- **Master URL Resolution**: Reads a user-defined or default Master Gist URL to identify remote M3U playlists.
- **Coroutines & Transactions**: Runs network calls and bulky database inserts using `Dispatchers.IO` and `withTransaction` to ensure atomicity.
- **Sanitization**: Parses external M3U sources to local `ChannelEntity` models safely.

### TorrentDownloadService (`TorrentDownloadService.kt`)

Handles the lifecycle of P2P media streaming without being killed by the OS.

- **Foreground Service**: Runs as a persistent Foreground Service with a low-importance Notification channel.
- **Intent Handling**: Safely accepts Magnet URIs via `Intents` (`EXTRA_MAGNET`) and validates them to prevent injection attacks before initializing the underlying torrent engine.

## 🛠️ Build Instructions

### Prerequisites

- Android Studio Iguana (or newer recommended).
- Java 17 toolchain.

### Steps to Build

1. Clone the repository: `git clone https://github.com/your-username/vtrae.git`
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. To build the debug APK, run:
   ```bash
   ./gradlew assembleDebug
   ```
5. To build the release APK (unsigned):
   ```bash
   ./gradlew assembleRelease
   ```
6. To run Android Lint checks:
   ```bash
   ./gradlew lintRelease
   ```

## 🤝 Contributing

We welcome contributions! To contribute to VTRÆ:

1. **Fork the repository** to your own GitHub account.
2. **Create a new branch** for your feature or bug fix (`git checkout -b feature/amazing-feature`).
3. **Write your code** ensuring it adheres to the existing architecture (use Coroutines for async work, Room for caching).
4. **Run the Linting locally** (`./gradlew lintDebug`) and ensure your changes pass.
5. **Commit your changes** (`git commit -m 'Add amazing feature'`).
6. **Push to the branch** (`git push origin feature/amazing-feature`).
7. **Open a Pull Request** against the `main` branch.

Please ensure your code is clean, properly commented, and respects the existing UI themes.
