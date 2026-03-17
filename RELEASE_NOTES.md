# Release Notes - v1.0.0

**Release Date:** March 8, 2026

We are thrilled to announce the v1.0.0 release of **VTRÆ**! This major release brings a highly polished, robust, and secure experience for our users, underpinned by a fully modernized Android architecture.

## 🚀 Key Features & Architectural Improvements

### Modernized UI & Localization

- **Compose for TV Integration:** Upgraded the visual interface using `androidx.tv` to provide a truly modern, edge-to-edge cinematic experience out-of-the-box.
- **Flix Theme:** Deployed the new cohesive 'Flix' visual design language across the application, standardizing typography, colors, and rounding corners, moving away from the legacy Brutalist design.
- **Complete Localization:** Extracted all hardcoded English strings into `strings.xml` and fully localized the application into Spanish (`strings-es.xml`), ensuring a localized experience for a wider audience.
- **App Launch Icon:** Integrated a dedicated launch icon for a professional home-screen presence.

### Robust Media & P2P Engine

- **TorrentDownloadService Foreground Streaming:** Refactored torrent streaming into a persistent Foreground Service (`TorrentDownloadService.kt`), guaranteeing the OS does not kill background streams during playback.
- **Refined MediaScraperEngine:** Enhanced scraping algorithms for major torrent and DDL sources.
- **Watch History:** Implemented a Room-backed Watch History feature that saves playback position, supporting up to 10 entries contextually on the home screen so users can always 'Continue Watching'.
- **Live TV Stabilization:** Vetted live stream URLs (e.g., El Trece) and cleaned up dysfunctional M3U lists, providing a curated list of reliable Argentina streams.

### Security, Performance & Code Quality

- **Coroutines & Database Audits:** Purged blocking IO calls from the Main thread across the application. Operations in `SyncEngine` and `MediaScraperEngine` now utilize `Dispatchers.IO` and `Dispatchers.Default` for significantly reduced memory pressure.
- **API Security:** Implemented strict HTTP traffic policies and robust validation algorithms for payload ingestion to prevent string injection or unverified external Magnet interactions.
- **CI/CD Integration:** Deployed a GitHub Actions workflow (`build_and_lint.yml`) that automatically runs linting and generates unsigned release APKs for every branch push, ensuring only high-quality code makes it to release.
- **JVM Stabilization:** Investigated and resolved a critical JVM crash in the build environment to ensure reproducible and safe compilation.

## 🛠️ Under the Hood

- Added `androidx.work:work-runtime-ktx` for future periodic synchronization tasks.
- Upgraded Room to `2.6.1` and Media3 (ExoPlayer) to `1.3.1`.

Enjoy your media! Please continue reporting any issues on our GitHub repository.
