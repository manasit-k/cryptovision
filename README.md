# 🪙 CryptoVision (Finance Portfolio Showcase)

**Senior Android Developer Portfolio Project** A high-performance, offline-first cryptocurrency tracker built with **Clean Architecture**, **Multi-module**, and **Jetpack Compose**. This project serves as a showcase for scalable app architecture, reactive programming, and automated quality assurance.

---

## 🚀 Technical Stack & Tools
- **Language:** Kotlin 100% (Coroutines, Flow)
- **UI:** Jetpack Compose (Material 3, Single Activity)
- **Architecture:** MVI (Model-View-Intent) + Clean Architecture
- **Dependency Injection:** Hilt
- **Network:** Retrofit + OkHttp (Custom Interceptors & Error Handling)
- **Local Storage:** Room Database (Single Source of Truth)
- **CI/CD:** GitHub Actions
- **Testing:** JUnit5, MockK, Turbine (Flow testing), MockWebServer

---

## 🏗 Modularization Strategy
To ensure separation of concerns and optimized build times, the project is structured into functional and layer-based modules:

```
cryptovision-android/
├── app/                          # Main entry point and DI composition
├── core/
│   ├── ui/                       # Design system, shared Compose components
│   ├── network/                  # API client, Interceptors, Connectivity
│   ├── database/                 # Room persistence and migrations
│   └── common/                   # Shared utilities and extensions
├── data/                         # Repository implementations
├── domain/                       # Business logic, UseCases, Entities
└── feature/
    ├── dashboard/                # Main dashboard feature
    ├── details/                  # Crypto detail screen
    └── favorites/                # Favorites management
```

---

## 🌟 Senior-Level Engineering Highlights

### 1. Robust CI/CD & Quality Gate
I implemented a comprehensive **GitHub Actions** pipeline to ensure code integrity:
- **Static Analysis:** Uses `detekt` and `ktlint` to enforce code quality and style standards.
- **Automated Testing:** Runs Unit Tests on every Pull Request to prevent regressions.
- **Build Validation:** Verifies compilation for all build variants (Debug/Release).

### 2. Offline-First & Data Reliability
- Uses a **Single Source of Truth** pattern where the UI only observes the local database.
- Implements `BigDecimal` for financial data to prevent precision loss.
- Custom **Network Bound Resource** logic for seamless data synchronization.

### 3. Reactive State Management (MVI)
- Unidirectional Data Flow (UDF) to handle complex UI states and side effects.
- Lifecycle-aware data collection using `repeatOnLifecycle` to optimize resource usage.

### 4. Custom Graphics
- Built historical price charts using **Jetpack Compose Canvas** to demonstrate proficiency in custom drawing and performance tuning without relying on 3rd-party heavy libraries.

---

## 🧪 Testing Strategy
- **Unit Tests:** Focused on Domain UseCases and ViewModels.
- **Integration Tests:** Verifying Repository logic with `MockWebServer`.
- **UI Tests:** Snapshot testing and Compose UI testing for critical flows.

---

## 🛠 Setup & Contribution

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Gradle 8.2+

### Getting Started
1. Clone the repository:
   ```bash
   git clone https://github.com/manasit-k/cryptovision.git
   cd cryptovision-android
   ```

2. Add your API key in `local.properties`:
   ```properties
   COINGECKO_API_KEY=CG-uSkDaBN3Ss2LJvKJyzCHKuBJ
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run tests:
   ```bash
   ./gradlew test
   ```

5. Run the app:
   - Open in Android Studio
   - Select a device/emulator
   - Click Run

---

## 📱 Features

### Dashboard
- Real-time cryptocurrency prices
- Market cap and 24h change indicators
- Search and filter functionality
- Pull-to-refresh for data updates

### Details Screen
- Detailed coin information
- Historical price charts (7D, 30D, 1Y)
- Market statistics
- Add to favorites

### Favorites
- Manage favorite cryptocurrencies
- Quick access to tracked coins
- Offline support

---

## 🏛 Architecture Deep Dive

### Clean Architecture Layers

#### Domain Layer (`:domain`)
- Pure Kotlin/Java module
- Contains business logic and entities
- No Android dependencies
- UseCase pattern for business operations

#### Data Layer (`:data`)
- Repository implementations
- Data source coordination (Remote + Local)
- Network Bound Resource pattern
- DTO to Domain mapping

#### Presentation Layer (`:feature:*`)
- MVI architecture
- Jetpack Compose UI
- ViewModel with StateFlow
- Side effects handling

### Dependency Flow
```
:app → :feature:* → :domain ← :data
                      ↑
                   :core:*
```

---

## 🔧 Gradle Version Catalog

The project uses **Gradle Version Catalog** (`libs.versions.toml`) for centralized dependency management, ensuring consistency across all modules.

---

## 📊 Code Quality

### Static Analysis
- **detekt:** Kotlin code analysis
- **ktlint:** Code style enforcement
- Custom rule sets for project-specific conventions

### Code Coverage
- Minimum 80% coverage for domain layer
- Integration tests for critical paths
- UI tests for main user flows

---

## 🚀 CI/CD Pipeline

### GitHub Actions Workflows

#### Pull Request Checks
- Code style validation (ktlint)
- Static analysis (detekt)
- Unit tests
- Build verification

#### Release Pipeline
- Automated versioning
- Release notes generation
- APK/AAB generation
- Distribution to Play Store (Beta track)

---

## 📈 Performance Optimizations

- **Lazy Loading:** Pagination for large datasets
- **Image Caching:** Coil with custom caching strategy
- **Database Indexing:** Optimized Room queries
- **Compose Performance:** Remember, derivedStateOf, and key usage
- **Build Performance:** Gradle build cache and configuration cache

---

## 🔐 Security

- API keys stored in `local.properties` (not committed)
- ProGuard/R8 rules for release builds
- Certificate pinning for API calls
- Encrypted local storage for sensitive data

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Manasit Kittiyotthada**  
Senior Android Developer

- 📧 Email: manasit.cs@gmail.com
- 💼 LinkedIn: [Manasit Kittiyotthada](https://www.linkedin.com/in/manasit-kittiyotthada-88060a202/)
- 🐙 GitHub: [@manasit.k](https://github.com/manasit-k)

---

## 🙏 Acknowledgments

- CoinGecko API for cryptocurrency data
- Android community for amazing libraries and tools
- Jetpack Compose team for the modern UI toolkit

---

**⭐ If you find this project helpful, please consider giving it a star!**
