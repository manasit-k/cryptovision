# ⚡ Quick Start Guide

## 🚀 Get Up and Running in 5 Minutes

### Step 1: Clone or Navigate to Project
```bash
cd /Users/error/.gemini/antigravity/scratch/cryptovision-android
```

### Step 2: Set Up API Key
```bash
# Copy the template
cp local.properties.template local.properties

# Edit local.properties and add your CoinGecko API key
# Get free key from: https://www.coingecko.com/en/api
echo "COINGECKO_API_KEY=your_key_here" > local.properties
```

### Step 3: Open in Android Studio
1. Launch Android Studio
2. **File → Open**
3. Select the `cryptovision-android` folder
4. Wait for Gradle sync

### Step 4: Run the App
- Click the **Run** button (▶️)
- Or press **Shift + F10** (Windows/Linux) / **Control + R** (Mac)

---

## 📱 What You'll See

The app currently shows:
- ✅ Dashboard screen with basic UI
- ✅ Navigation to Details and Favorites
- ✅ Material 3 theme with dark/light mode
- ✅ Proper architecture structure

---

## 🧪 Run Tests
```bash
./gradlew test
```

## 🔍 Run Code Quality Checks
```bash
./gradlew ktlintCheck detekt
```

## 🏗️ Build APK
```bash
./gradlew assembleDebug
```

---

## 📚 Next Steps

1. **Explore the Code**
   - Start with `app/MainActivity.kt`
   - Check out `domain/` for business logic
   - Look at `core/ui/theme/` for design system

2. **Read Documentation**
   - [Architecture Guide](docs/ARCHITECTURE.md)
   - [Project Summary](docs/PROJECT_SUMMARY.md)
   - [Visual Diagrams](docs/DIAGRAMS.md)

3. **Implement Features**
   - Add API integration
   - Complete repository implementation
   - Build ViewModels
   - Create UI screens

---

## 🎯 Project Structure at a Glance

```
cryptovision-android/
├── app/              → Main application
├── core/             → Shared infrastructure
│   ├── ui/          → Design system
│   ├── network/     → API client
│   ├── database/    → Local storage
│   └── common/      → Utilities
├── domain/           → Business logic
├── data/             → Data layer
└── feature/          → UI features
    ├── dashboard/
    ├── details/
    └── favorites/
```

---

## 💡 Tips

- **Gradle Sync Issues?** → `./gradlew clean build --refresh-dependencies`
- **Need Help?** → Check `docs/GETTING_STARTED.md`
- **Want to Contribute?** → See `docs/CHECKLIST.md` for TODOs

---

## ✨ Key Features

- ✅ Clean Architecture
- ✅ Multi-module structure
- ✅ MVI pattern ready
- ✅ Jetpack Compose UI
- ✅ Hilt dependency injection
- ✅ Room database setup
- ✅ Retrofit network layer
- ✅ CI/CD with GitHub Actions
- ✅ Comprehensive documentation

---

**You're all set! Happy coding! 🚀**

For detailed information, see the [full documentation](docs/).
