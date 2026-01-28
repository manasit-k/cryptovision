# 🎨 CryptoVision - Visual Overview

## Module Dependency Graph

```
┌─────────────────────────────────────────────────────────────────┐
│                           :app                                   │
│                    (Application Module)                          │
│  • MainActivity                                                  │
│  • CryptoVisionApp                                              │
│  • Navigation                                                    │
│  • Hilt Entry Point                                             │
└────────────┬────────────┬────────────┬──────────────────────────┘
             │            │            │
             ▼            ▼            ▼
    ┌────────────┐ ┌────────────┐ ┌────────────┐
    │ :feature:  │ │ :feature:  │ │ :feature:  │
    │ dashboard  │ │  details   │ │ favorites  │
    └─────┬──────┘ └─────┬──────┘ └─────┬──────┘
          │              │              │
          └──────────────┼──────────────┘
                         │
                         ▼
              ┌──────────────────┐
              │     :domain      │
              │  (Pure Kotlin)   │
              │  • Models        │
              │  • UseCases      │
              │  • Repositories  │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │      :data       │
              │  • Repository    │
              │    Impl          │
              │  • Mappers       │
              └────┬──────┬──────┘
                   │      │
          ┌────────┘      └────────┐
          ▼                        ▼
   ┌─────────────┐         ┌─────────────┐
   │   :core:    │         │   :core:    │
   │  network    │         │  database   │
   │  • Retrofit │         │  • Room     │
   │  • OkHttp   │         │  • DAOs     │
   └──────┬──────┘         └──────┬──────┘
          │                       │
          └───────────┬───────────┘
                      │
                      ▼
              ┌──────────────┐
              │   :core:ui   │
              │  • Theme     │
              │  • Components│
              └──────┬───────┘
                     │
                     ▼
              ┌──────────────┐
              │ :core:common │
              │  • Utils     │
              │  • Extensions│
              └──────────────┘
```

## Clean Architecture Layers

```
┌───────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                         │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Jetpack Compose UI + ViewModels (MVI Pattern)       │    │
│  │  • Screens                                            │    │
│  │  • State Management                                   │    │
│  │  • User Intents                                       │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────┬───────────────────────────────────┘
                            │
                            ▼
┌───────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                              │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Pure Kotlin Business Logic (Framework Independent)  │    │
│  │  • Domain Models (Entities)                          │    │
│  │  • Use Cases                                          │    │
│  │  • Repository Interfaces                             │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────┬───────────────────────────────────┘
                            │
                            ▼
┌───────────────────────────────────────────────────────────────┐
│                       DATA LAYER                               │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Repository Implementations + Data Sources           │    │
│  │  • Repository Impl                                    │    │
│  │  • Remote Data Source (API)                          │    │
│  │  • Local Data Source (Database)                      │    │
│  │  • DTO ↔ Domain Mapping                              │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────┘
```

## MVI Pattern Flow

```
┌──────────────────────────────────────────────────────────────┐
│                         UI (Screen)                           │
│  • Displays State                                             │
│  • Emits User Intents                                         │
└────────────┬─────────────────────────────────┬───────────────┘
             │                                 │
        User Action                      Observe State
             │                                 │
             ▼                                 │
┌──────────────────────┐                      │
│   Intent (Event)     │                      │
│  • LoadCoins         │                      │
│  • RefreshCoins      │                      │
│  • SearchCoins       │                      │
└──────────┬───────────┘                      │
           │                                  │
           ▼                                  │
┌──────────────────────┐                      │
│     ViewModel        │                      │
│  • Process Intent    │                      │
│  • Call Use Case     │                      │
│  • Update State      │                      │
└──────────┬───────────┘                      │
           │                                  │
           ▼                                  │
┌──────────────────────┐                      │
│   State (Data)       │──────────────────────┘
│  • coins: List       │
│  • isLoading: Bool   │
│  • error: String?    │
└──────────────────────┘
```

## Data Flow (Network Bound Resource)

```
┌─────────────┐
│  ViewModel  │
└──────┬──────┘
       │ 1. Request Data
       ▼
┌──────────────┐
│  Repository  │
└──────┬───────┘
       │
       ├─────────────────────────────────────┐
       │                                     │
       │ 2. Query Cache                      │ 4. Fetch from Network
       ▼                                     ▼
┌──────────────┐                      ┌──────────────┐
│   Database   │                      │  API Service │
│   (Room)     │                      │  (Retrofit)  │
└──────┬───────┘                      └──────┬───────┘
       │                                     │
       │ 3. Emit Cached Data                 │ 5. Save to Cache
       │    (Loading + Success)              │
       │◀────────────────────────────────────┘
       │
       │ 6. Emit Updated Data
       ▼
┌──────────────┐
│  ViewModel   │
│  (StateFlow) │
└──────┬───────┘
       │ 7. Update UI
       ▼
┌──────────────┐
│   UI Screen  │
└──────────────┘
```

## Testing Pyramid

```
                    ┌──────────┐
                    │    UI    │  ← Compose UI Tests
                    │  Tests   │     (Few, Slow)
                    └────┬─────┘
                         │
                ┌────────┴────────┐
                │  Integration    │  ← Repository Tests
                │     Tests       │     MockWebServer
                └────────┬────────┘     (Some, Medium)
                         │
            ┌────────────┴────────────┐
            │     Unit Tests          │  ← UseCase, ViewModel
            │  (Domain + ViewModels)  │     (Many, Fast)
            └─────────────────────────┘
```

## CI/CD Pipeline

```
┌─────────────┐
│  Git Push   │
│  / PR       │
└──────┬──────┘
       │
       ▼
┌──────────────────────────────────────────┐
│         GitHub Actions Workflow           │
├──────────────────────────────────────────┤
│                                           │
│  ┌────────────┐                          │
│  │  1. Lint   │  ktlint + detekt         │
│  └─────┬──────┘                          │
│        │ ✓                                │
│        ▼                                  │
│  ┌────────────┐                          │
│  │  2. Test   │  Unit Tests (JUnit5)     │
│  └─────┬──────┘                          │
│        │ ✓                                │
│        ▼                                  │
│  ┌────────────┐                          │
│  │  3. Build  │  Debug + Release APK     │
│  └─────┬──────┘                          │
│        │ ✓                                │
│        ▼                                  │
│  ┌────────────┐                          │
│  │  4. Report │  Upload Artifacts        │
│  └────────────┘                          │
│                                           │
└──────────────────────────────────────────┘
       │
       ▼
┌──────────────┐
│   Success!   │
│   Merge PR   │
└──────────────┘
```

## Technology Stack

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Jetpack    │  │  Material 3  │  │     Coil     │ │
│  │   Compose    │  │    Theme     │  │ (Images)     │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                   ARCHITECTURE                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │     MVI      │  │    Clean     │  │     Hilt     │ │
│  │   Pattern    │  │ Architecture │  │     (DI)     │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      NETWORKING                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Retrofit   │  │    OkHttp    │  │  Kotlinx     │ │
│  │              │  │  Interceptor │  │Serialization │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      DATABASE                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │     Room     │  │     Flow     │  │  BigDecimal  │ │
│  │   Database   │  │  (Reactive)  │  │  (Finance)   │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                       TESTING                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │    JUnit5    │  │     MockK    │  │    Turbine   │ │
│  │              │  │   (Mocking)  │  │ (Flow Test)  │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    CODE QUALITY                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │    detekt    │  │    ktlint    │  │    GitHub    │ │
│  │  (Analysis)  │  │   (Style)    │  │   Actions    │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## File Structure Overview

```
cryptovision-android/
│
├── 📱 app/                          # Application Module
│   ├── src/main/
│   │   ├── java/com/cryptovision/app/
│   │   │   ├── CryptoVisionApp.kt
│   │   │   ├── MainActivity.kt
│   │   │   └── navigation/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── 🎨 core/ui/                      # Design System
│   └── src/main/java/com/cryptovision/core/ui/
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── Type.kt
│       │   └── Theme.kt
│       └── components/
│           ├── LoadingIndicator.kt
│           ├── ErrorState.kt
│           └── EmptyState.kt
│
├── 🌐 core/network/                 # Network Layer
│   └── src/main/java/com/cryptovision/core/network/
│       └── di/NetworkModule.kt
│
├── 💾 core/database/                # Database Layer
│   └── src/main/java/com/cryptovision/core/database/
│       ├── CryptoDatabase.kt
│       ├── dao/
│       ├── entity/
│       └── di/DatabaseModule.kt
│
├── 🛠️ core/common/                  # Utilities
│   └── src/main/java/com/cryptovision/core/common/
│       ├── util/
│       └── dispatcher/
│
├── 🏛️ domain/                       # Business Logic
│   └── src/main/java/com/cryptovision/domain/
│       ├── model/
│       ├── repository/
│       └── usecase/
│
├── 📊 data/                         # Data Layer
│   └── src/main/java/com/cryptovision/data/
│       ├── repository/
│       └── di/RepositoryModule.kt
│
├── 📱 feature/                      # Feature Modules
│   ├── dashboard/
│   ├── details/
│   └── favorites/
│
├── 📚 docs/                         # Documentation
│   ├── ARCHITECTURE.md
│   ├── GETTING_STARTED.md
│   ├── CHECKLIST.md
│   └── DIAGRAMS.md (this file)
│
├── 🔧 .github/workflows/            # CI/CD
│   └── android-ci.yml
│
├── 📝 gradle/                       # Gradle Config
│   └── libs.versions.toml
│
├── 📄 README.md
├── 📄 LICENSE
├── 📄 .gitignore
├── 📄 settings.gradle.kts
├── 📄 build.gradle.kts
└── 📄 gradle.properties
```

---

## Key Highlights

### 🎯 Architecture Benefits
- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Scalability**: Easy to add new features
- **Reusability**: Shared modules across features

### 🚀 Performance Features
- **Offline-First**: Works without internet
- **Caching**: Smart cache invalidation
- **Lazy Loading**: Efficient data loading
- **BigDecimal**: Financial precision

### 🔒 Quality Assurance
- **Static Analysis**: detekt + ktlint
- **Automated Testing**: CI/CD pipeline
- **Code Coverage**: Comprehensive tests
- **Type Safety**: Kotlin + Flow

---

**This diagram provides a visual overview of the CryptoVision architecture and structure.**
