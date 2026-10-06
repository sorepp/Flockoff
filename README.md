# FlockAvoidApp 1.1

Community-sourced map for awareness of publicly visible surveillance cameras.

## Map fixes
- Uses OpenStreetMap/osmdroid with a proper application user-agent.
- Enables tile caching for better repeat loading and limited connectivity.
- Starts at a neutral U.S. map view instead of a hard-coded New York location.
- Requests location only when needed.
- Adds a one-tap "my location" button.
- Keeps the map usable when location permission is denied.
- Uses Google Play Services fused location when available.

## Build
Open the project in Android Studio with JDK 17 and let Gradle sync.

The project uses:
- Android Gradle Plugin 8.5.0
- Kotlin 1.9.24
- compileSdk 34
- osmdroid 6.1.18
- Google Play Services Location 21.3.0

## Legal use
Only map cameras that are publicly visible from public places. Never tamper with,
damage, block, or defeat any device.
