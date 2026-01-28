# 🚀 Getting Started Guide

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Version 17
- **Gradle**: 8.2+ (included with Android Studio)
- **Git**: For version control

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/cryptovision-android.git
cd cryptovision-android
```

### 2. Configure API Key

1. Copy the template file:
   ```bash
   cp local.properties.template local.properties
   ```

2. Get your free API key from [CoinGecko](https://www.coingecko.com/en/api)

3. Add your API key to `local.properties`:
   ```properties
   COINGECKO_API_KEY=your_actual_api_key_here
   ```

**Important**: Never commit `local.properties` to version control!

### 3. Open in Android Studio

1. Launch Android Studio
2. Select **File → Open**
3. Navigate to the cloned repository
4. Click **OK**
5. Wait for Gradle sync to complete

### 4. Build the Project

```bash
./gradlew build
```

Or use Android Studio:
- **Build → Make Project** (Cmd+F9 / Ctrl+F9)

### 5. Run the App

#### Using Android Studio
1. Select a device or emulator
2. Click the **Run** button (▶️) or press Shift+F10

#### Using Command Line
```bash
# Install debug build
./gradlew installDebug

# Run on connected device
adb shell am start -n com.cryptovision.app.debug/.MainActivity
```

---

## Project Structure

```
cryptovision-android/
├── app/                      # Main application module
├── core/
│   ├── common/              # Shared utilities
│   ├── database/            # Room database
│   ├── network/             # Retrofit & OkHttp
│   └── ui/                  # Design system & components
├── data/                    # Repository implementations
├── domain/                  # Business logic & use cases
├── feature/
│   ├── dashboard/           # Main screen
│   ├── details/             # Coin details
│   └── favorites/           # Favorites screen
├── docs/                    # Documentation
├── .github/workflows/       # CI/CD pipelines
└── gradle/                  # Gradle configuration
```

---

## Development Workflow

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew :domain:test

# Run tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Code Quality Checks

```bash
# Run ktlint
./gradlew ktlintCheck

# Auto-fix ktlint issues
./gradlew ktlintFormat

# Run detekt
./gradlew detekt

# Run all quality checks
./gradlew ktlintCheck detekt test
```

### Building APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing configuration)
./gradlew assembleRelease

# Find APKs in:
# app/build/outputs/apk/debug/
# app/build/outputs/apk/release/
```

### Building AAB (Android App Bundle)

```bash
# Debug AAB
./gradlew bundleDebug

# Release AAB
./gradlew bundleRelease

# Find AABs in:
# app/build/outputs/bundle/debug/
# app/build/outputs/bundle/release/
```

---

## Common Tasks

### Adding a New Feature Module

1. Create module directory:
   ```bash
   mkdir -p feature/newfeature/src/main/java/com/cryptovision/feature/newfeature
   ```

2. Add to `settings.gradle.kts`:
   ```kotlin
   include(":feature:newfeature")
   ```

3. Create `build.gradle.kts` in the module
4. Add module dependency to `:app` module

### Adding a New Dependency

1. Add to `gradle/libs.versions.toml`:
   ```toml
   [versions]
   newlib = "1.0.0"
   
   [libraries]
   newlib = { module = "com.example:newlib", version.ref = "newlib" }
   ```

2. Use in module's `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.newlib)
   }
   ```

### Database Migrations

When changing Room entities:

1. Increment version in `CryptoDatabase`:
   ```kotlin
   @Database(version = 2)
   ```

2. Add migration:
   ```kotlin
   val MIGRATION_1_2 = object : Migration(1, 2) {
       override fun migrate(database: SupportSQLiteDatabase) {
           // Migration SQL
       }
   }
   ```

3. Add to database builder in `DatabaseModule`

---

## Troubleshooting

### Gradle Sync Failed

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies

# Clear Gradle cache
rm -rf ~/.gradle/caches/
```

### Build Failed - API Key Missing

**Solution**: Ensure `local.properties` exists with valid API key

### Tests Failing

**Solution**:
```bash
# Clean test results
./gradlew cleanTest

# Run with stack trace
./gradlew test --stacktrace
```

### Emulator Issues

**Solution**:
1. Wipe emulator data: **Tools → AVD Manager → Wipe Data**
2. Create new emulator with latest system image
3. Ensure hardware acceleration is enabled

---

## IDE Configuration

### Recommended Android Studio Plugins

- **Kotlin**: Built-in
- **Compose Preview**: Built-in
- **Database Inspector**: Built-in
- **Detekt**: For static analysis
- **Rainbow Brackets**: For better code readability

### Code Style

The project uses ktlint for code formatting. Configure Android Studio:

1. **Settings → Editor → Code Style → Kotlin**
2. Click **Set from...** → **Predefined Style** → **Kotlin Style Guide**

---

## Useful Commands

```bash
# List all tasks
./gradlew tasks

# List dependencies
./gradlew :app:dependencies

# Check for dependency updates
./gradlew dependencyUpdates

# Generate dependency graph
./gradlew :app:dependencies --configuration debugCompileClasspath

# Clean build
./gradlew clean

# Assemble all variants
./gradlew assemble
```

---

## Next Steps

1. **Read Architecture Docs**: Check `docs/ARCHITECTURE.md`
2. **Explore Code**: Start with `:app` module
3. **Run Tests**: Familiarize yourself with test structure
4. **Make Changes**: Try adding a new feature
5. **Submit PR**: Follow contribution guidelines

---

## Getting Help

- **Issues**: [GitHub Issues](https://github.com/yourusername/cryptovision-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/cryptovision-android/discussions)
- **Documentation**: Check `docs/` directory

---

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

Happy coding! 🚀
