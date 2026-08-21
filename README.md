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

## How to Build (GitHub Actions)

1. Push this repository to GitHub
2. Go to the **Actions** tab
3. Select **Build QuestFiles APK**
4. Click **Run workflow**
5. When it finishes, download the artifact named `QuestFiles-APK`

The workflow file is located at:
```
.github/workflows/build.yml
```

---

## Installation on Quest

1. Enable **Developer Mode** on your Quest
2. Install the APK using:
   - SideQuest
   - ADB (`adb install app-debug.apk`)
   - Or any other sideloading method
3. On first launch, grant **All files access** when prompted

---

## Permissions Required

- `MANAGE_EXTERNAL_STORAGE` (All files access)
- Storage read/write

Without All files access the app cannot properly browse `Android/data` or inject into game APKs.

---

## Notes / Limitations

- Full system root access (`/data/data`, `/system`, etc.) requires a rooted Quest or Shizuku.
- On stock Quest you can still access shared storage + `Android/data` / `Android/obb` after granting permissions.
- The injector does **not** re-sign the APK. After injection you still need to sign it yourself (or use a tool that ignores the signature).
- Injecting into online games can result in bans. Use at your own risk (preferably on offline / private builds).

---

## Project Structure

```
QuestFiles/
├── .github/workflows/build.yml
├── app/
│   └── src/main/java/com/Tvman4/QuestFiles/
│       ├── MainActivity.kt
│       ├── file/FileUtils.kt
│       ├── injector/SoInjector.kt
│       └── ui/
│           ├── FilesTab.kt
│           └── InjectorTab.kt
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## License

MIT License
