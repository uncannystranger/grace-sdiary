<div align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/uncannystranger/grace-sdiary/android.yml?branch=main&label=Build&style=flat-square" alt="Build">
  <img src="https://img.shields.io/github/v/release/uncannystranger/grace-sdiary?style=flat-square&color=E2A33D" alt="Version">
  <img src="https://img.shields.io/badge/Android-15-34A853?style=flat-square&logo=android" alt="Android 15">
  <img src="https://img.shields.io/badge/Kotlin-2.1-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin">
  <img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square" alt="License">
  <br><br>
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="Grace's Diary Logo" style="border-radius:24px;">
  <h1>Grace's Diary</h1>
  <p>A premium offline personal learning dashboard for Android — track vocabulary, habits, routines, moods, focus sessions, and goals.</p>
  <br>
  <a href="#-features">Features</a> •
  <a href="#-screenshots">Screenshots</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-building">Building</a> •
  <a href="#-ci-cd">CI/CD</a> •
  <a href="#-google-play">Google Play</a>
  <br><br>
  <a href="https://github.com/uncannystranger/grace-sdiary/releases/latest/download/app-release.apk">
    <img src="https://img.shields.io/badge/Download-APK-34A853?style=for-the-badge&logo=android" alt="Download APK">
  </a>
  <a href="https://github.com/uncannystranger/grace-sdiary/releases/latest/download/app-release.aab">
    <img src="https://img.shields.io/badge/Download-AAB-FF6D00?style=for-the-badge&logo=googleplay" alt="Download AAB">
  </a>
  <a href="https://github.com/uncannystranger/grace-sdiary/actions">
    <img src="https://img.shields.io/badge/CI-CD-purple?style=for-the-badge&logo=githubactions" alt="CI/CD">
  </a>
  <br>
  <a href="https://github.com/uncannystranger/grace-sdiary/actions/workflows/android.yml">
    <img src="https://github.com/uncannystranger/grace-sdiary/actions/workflows/android.yml/badge.svg" alt="Build Status">
  </a>
  <a href="https://github.com/uncannystranger/grace-sdiary/releases">
    <img src="https://img.shields.io/github/v/release/uncannystranger/grace-sdiary?style=flat-square&color=E2A33D" alt="Latest Release">
  </a>
</div>

<br>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| **📖 Vocabulary** | Build your vocabulary with words, definitions, examples, difficulty levels, and spaced-repetition tracking |
| **📝 Diary** | Daily diary entries with mood tracking (5 emoji levels), search, and history |
| **🔥 Habits** | Track daily habits with streaks, frequency settings, and visual progress |
| **⏱️ Focus Timer** | Pomodoro timer with configurable focus/break durations and session tracking |
| **📅 Calendar** | Monthly calendar with events, planner integration, and day-specific views |
| **🎯 Goals** | Weekly, monthly, and yearly goals with progress bars and completion tracking |
| **🌅 Routine** | Morning, afternoon, evening, night routine builder with enabled/disabled toggles |
| **⏰ Reminders** | Local notifications with custom types, repeat intervals, and snooze |
| **📊 Analytics** | Progress dashboard with XP level system, streaks, charts, and stats |
| **🔍 Search** | Instant offline search across vocabulary, diary, planner, and notes |
| **🎨 Themes** | Light and dark mode with glassmorphism design, system theme support |
| **💾 Backup** | Full database backup/restore to JSON, export to CSV and JSON |

## 💻 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin 2.1 |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository + Clean Architecture |
| **Database** | Room (SQLite) |
| **DI** | Hilt |
| **Navigation** | Navigation Compose |
| **Preferences** | DataStore |
| **Background** | WorkManager |
| **Notifications** | Local notifications (no server) |
| **Widgets** | Glance (Home Screen Widget) |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 (Android 15) |

## 🏗 Architecture

```
com.grace.sdiary/
├── data/
│   ├── local/
│   │   ├── db/           # Room: 10 entities, 10 DAOs, AppDatabase
│   │   └── datastore/    # UserPreferences (theme, XP, streak)
│   ├── repository/       # 9 repositories + ProgressRepository
│   ├── backup/           # Full JSON backup/restore
│   ├── export/           # CSV/JSON export
│   └── model/            # Shared domain models
├── di/                   # Hilt modules (Database, Repository, App)
├── ui/
│   ├── theme/            # Colors, Typography, Shapes, Glassmorphism
│   ├── components/       # Reusable composables (GlassCard, RingProgress, etc.)
│   ├── navigation/       # NavGraph (13 routes)
│   └── screens/          # 13 feature screens + ViewModels
├── notification/         # NotificationHelper, BootReceiver
├── worker/               # ReminderCheckWorker, DailyResetWorker
└── util/                 # Constants, DateUtils
```

## 📱 Screenshots

<div align="center">
  <table>
    <tr>
      <td><strong>Dashboard</strong></td>
      <td><strong>Vocabulary</strong></td>
      <td><strong>Diary</strong></td>
    </tr>
    <tr>
      <td><em>(screenshots coming soon)</em></td>
      <td><em>(screenshots coming soon)</em></td>
      <td><em>(screenshots coming soon)</em></td>
    </tr>
  </table>
</div>

## 🔧 Building

### Prerequisites

- Android Studio Hedgehog (2024.3.1) or newer
- JDK 17
- Android SDK 35

### Local Build

```bash
# Clone the repository
git clone https://github.com/uncannystranger/grace-sdiary.git
cd grace-sdiary/android

# Debug build (unsigned)
./gradlew assembleDebug

# Release build (requires signing)
# 1. Generate a keystore
bash keystore/generate.sh

# 2. Build signed release
./gradlew assembleRelease

# 3. Build Android App Bundle
./gradlew bundleRelease
```

### Output Locations

| Artifact | Path |
|----------|------|
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Release APK | `app/build/outputs/apk/release/app-release.apk` |
| Release AAB | `app/build/outputs/bundle/release/app-release.aab` |

## 🤖 CI/CD

This project uses **GitHub Actions** for continuous integration and delivery.

### Workflow: `.github/workflows/android.yml`

**Triggers:**
- Push to `main`, `develop`, or `release/**` branches
- Pull requests to `main`
- Tags matching `v*` (e.g., `v1.0.0`)
- Manual trigger via `workflow_dispatch`

**What it does:**
1. Checks out the repository
2. Sets up JDK 17 with caching
3. Decodes the keystore from `KEYSTORE_BASE64` secret
4. Builds: `assembleDebug`, `assembleRelease`, `bundleRelease`
5. Runs lint checks
6. Uploads APK and AAB as artifacts (retained 30-90 days)
7. **On tag push:** creates a GitHub Release with APK + AAB attached

### Artifacts

| Artifact | Description | Retention |
|----------|-------------|-----------|
| `app-debug` | Debug APK (unsigned) | 30 days |
| `app-release-apk` | Signed Release APK | 90 days |
| `app-release-aab` | Signed Android App Bundle | 90 days |
| `lint-report` | Lint HTML report | 14 days |

## 🔐 Release Signing

### GitHub Secrets Setup

Add these secrets to your GitHub repository:
`Settings → Secrets and variables → Actions → New repository secret`

| Secret | Description | How to get |
|--------|-------------|------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file | `base64 -i keystore.jks \| pbcopy` |
| `KEYSTORE_PASSWORD` | Keystore password | Set during keystore generation |
| `KEY_ALIAS` | Key alias | Set during keystore generation |
| `KEY_PASSWORD` | Key password | Set during keystore generation |

### Local Signing

```bash
# 1. Generate a keystore
bash keystore/generate.sh

# 2. Copy the template
cp keystore/keystore.properties.template keystore/keystore.properties

# 3. Edit keystore.properties with your credentials
# (This file is gitignored — safe to store locally)
```

## 📲 Installation

### Direct Download

| Artifact | Link |
|----------|------|
| **Release APK** | [app-release.apk](https://github.com/uncannystranger/grace-sdiary/releases/latest/download/app-release.apk) |
| **Release AAB** | [app-release.aab](https://github.com/uncannystranger/grace-sdiary/releases/latest/download/app-release.aab) |
| **Debug APK** | [app-debug.apk](https://github.com/uncannystranger/grace-sdiary/actions/workflows/android.yml) (click latest run → Artifacts) |
| **Latest Release** | [Releases page](https://github.com/uncannystranger/grace-sdiary/releases) |

### From GitHub Releases

1. Go to the [Releases page](https://github.com/uncannystranger/grace-sdiary/releases)
2. Download the latest `app-release.apk`
3. On your Android device, enable **Install from unknown sources**
4. Open the downloaded APK file

### From GitHub Actions (latest build)

1. Go to the [Actions tab](https://github.com/uncannystranger/grace-sdiary/actions)
2. Click the latest successful workflow run
3. Scroll to **Artifacts** section
4. Download `app-release-apk` or `app-debug`

## 🏪 Google Play Publishing

### Prerequisites

1. A [Google Play Console](https://play.google.com/console/) account ($25 one-time fee)
2. A signed Android App Bundle (`.aab`)

### Steps

1. Generate a keystore (see [Release Signing](#-release-signing))
2. Build the release bundle: `./gradlew bundleRelease`
3. Open [Google Play Console](https://play.google.com/console/)
4. Create a new app
5. Upload `app/build/outputs/bundle/release/app-release.aab`
6. Fill in store listing, content rating, and app pricing
7. Submit for review

> **Note:** The keystore used for signing must be kept safe for the lifetime of the app. Losing it means you cannot publish updates.

## 📄 License

```
MIT License — Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files... (see LICENSE file)
```

---

<div align="center">
  <sub>Built with ❤️ for Sihaam</sub>
  <br>
  <sub>Kotlin • Jetpack Compose • Material 3 • Room • Hilt • WorkManager</sub>
</div>
