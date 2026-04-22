# CommunityAlert

Community alert system for weather, safety, traffic, and community events.

## Features
- 🌦️ Alert categories: Weather, Safety, Traffic, Community Events
- ⚠️ Severity levels: Info, Warning, Critical
- 📋 Filterable alert feed by category and severity
- ✅ Acknowledge/dismiss alerts
- 🔔 In-app push notification-style alerts with AlarmManager reminders
- 📅 Alert history with date range filtering
- 📤 Export alerts as JSON
- 🌙 Dark theme, Material Design 3
- ℹ️ About section with version info, update checker, and share

## Tech Stack
- Kotlin + AndroidX
- Room database with TypeConverters
- ViewBinding (no Compose)
- Material Design 3 (XML-based)
- AlarmManager for alert reminders
- Coroutines (Dispatchers.IO for all DB ops)
- Gson for JSON export

## Building
```bash
./gradlew assembleRelease
```

## Download
Get the latest APK from [Releases](https://github.com/jnetai-clawbot/CommunityAlert/releases/latest)