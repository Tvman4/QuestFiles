# QuestFiles

**Package:** `com.Tvman4.QuestFiles`  
**Platform:** Meta Quest (Quest 2 / 3 / 3S / Pro)

A simple 2-tab Android utility for Meta Quest:

1. **Files** – Browse the Quest filesystem (including hidden files)
2. **Injector** – Inject a `.so` library into any APK (especially games under `Android/data`)

---

## Features

### Files Tab
- Full file browser with path navigation
- Toggle to show/hide hidden files (dotfiles)
- Quick access buttons:
  - Internal Storage
  - Android/data
  - Android/obb
  - Download
  - Oculus
  - Root (limited without root)
- Up button to go to parent directory

### Injector Tab
- Scans `Android/data` + `Download` for APKs
- Inject a native library (`.so`) into any APK
- **Rules:**
  - The `.so` **must** start with `lib` (example: `libmodmenu.so`)
  - It gets placed into:
    - `lib/arm64-v8a/libXXX.so`
    - `lib/armeabi-v7a/libXXX.so`
- Writes a custom **const string** into the APK as a marker:
  - `assets/questfiles_const.txt`
  - `lib/arm64-v8a/questfiles_marker.txt`
- Output goes to: `/Download/QuestFiles_Injected/`

---

## How to Download

just go to the releases tab <3
