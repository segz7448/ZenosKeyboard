# ⌨ Zenos Keyboard

A fully custom Android IME (Input Method Editor) built for developers and power users.
Dark themed, Termux-ready, with clipboard history and text shortcuts.

---

## Features

| Feature | Details |
|---|---|
| 📋 Clipboard History | Auto-captures everything you copy. Horizontal strip in keyboard, full admin viewer |
| ⚡ Text Shortcuts | Type `@@ty` → expands to "Thank you!". Full CRUD in admin panel |
| 🖥 Termux Row | Dedicated row: `Ctrl+C` · `Ctrl+Z` · `Ctrl+D` · `Ctrl+L` · `Tab` |
| ⚙ Admin Panel | 3-tab admin: Clipboard viewer + clear, Shortcuts manager, Layout switcher |
| 🌐 Layouts | QWERTY · AZERTY · Dvorak |
| 🔢 Number Row | Always-visible number row at top |
| 🌙 Dark Theme | Deep navy + teal accent, built for OLED screens |

---

## Install (Manual)

1. Download the latest APK from [Releases](../../releases)
2. Enable **Unknown Sources** in Settings → Security
3. Install the APK
4. Open **Zenos Keyboard** → follow the 2-step setup
5. Done — start typing!

---

## Default Shortcuts (pre-seeded)

| Type | Gets |
|---|---|
| `@@date` | Today's date |
| `@@brb` | Be right back |
| `@@ty` | Thank you! |
| `@@np` | No problem! |
| `@@omw` | On my way! |

Add your own in **Admin → ⚡ Shortcuts**.

---

## Build Locally

```bash
# Clone
git clone https://github.com/YOUR_USERNAME/ZenosKeyboard.git
cd ZenosKeyboard

# Debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

Requirements: JDK 17+, Android SDK 34

---

## GitHub Actions CI/CD

Every push to `main`/`master`/`dev`:
- ✅ Runs **Lint**
- ✅ Builds **Debug APK** → uploads as artifact

Every `v*` tag push (e.g. `git tag v1.0.0 && git push --tags`):
- ✅ Builds **Release APK** (signed if secrets set)
- ✅ Creates a **GitHub Release** with APK attached

### Setting Up Release Signing

1. Generate a keystore:
   ```bash
   keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias zenoskey
   ```

2. Base64-encode it:
   ```bash
   base64 -w 0 release.jks
   ```

3. Add these **GitHub Secrets** (Settings → Secrets → Actions):
   - `KEYSTORE_BASE64` — the base64 output above
   - `KEYSTORE_PASSWORD` — your keystore password
   - `KEY_ALIAS` — `zenoskey` (or whatever you used)
   - `KEY_PASSWORD` — your key password

4. Push a version tag:
   ```bash
   git tag v1.0.0
   git push --tags
   ```

The action will build a signed release APK and publish it to GitHub Releases.

---

## Project Structure

```
ZenosKeyboard/
├── app/src/main/
│   ├── java/com/zenas/keyboard/
│   │   ├── ime/
│   │   │   ├── ZenosIME.java          # Main InputMethodService
│   │   │   └── KeyboardLayoutBuilder.java  # QWERTY/AZERTY/Dvorak builder
│   │   ├── clipboard/
│   │   │   ├── ClipboardManager.java  # History CRUD + system listener
│   │   │   ├── ClipboardPanel.java    # In-keyboard clipboard UI
│   │   │   ├── ClipboardItem.java     # Room entity
│   │   │   ├── ClipboardDao.java      # Room DAO
│   │   │   ├── ClipboardDatabase.java # Room DB
│   │   │   └── ClipboardService.java  # Background monitor
│   │   ├── shortcuts/
│   │   │   ├── ShortcutManager.java   # Expand + CRUD
│   │   │   ├── ShortcutItem.java      # Room entity
│   │   │   ├── ShortcutDao.java       # Room DAO
│   │   │   └── ShortcutDatabase.java  # Room DB
│   │   └── admin/
│   │       ├── SetupActivity.java         # Launcher / setup guide
│   │       ├── AdminActivity.java         # Tabbed admin
│   │       ├── ClipboardAdminFragment.java
│   │       ├── ShortcutsAdminFragment.java
│   │       └── LayoutAdminFragment.java
│   └── res/
│       ├── layout/   # All layouts
│       ├── drawable/ # Key backgrounds (dark + glow)
│       ├── values/   # Colors, strings, styles
│       └── xml/      # ime_config.xml
└── .github/workflows/build.yml  # CI/CD
```

---

## License

MIT — build it, fork it, flash it on your Termux phone.
