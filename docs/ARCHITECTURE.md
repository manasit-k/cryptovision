# 🏗️ Architecture Documentation

## Overview

CryptoVision follows **Clean Architecture** principles combined with **MVI (Model-View-Intent)** pattern for the presentation layer. The project is structured into multiple modules to ensure separation of concerns, testability, and build performance.

---

## Module Structure

### Layer-Based Modules

#### 1. **Domain Layer** (`:domain`)
- **Purpose**: Pure business logic, independent of Android framework
- **Language**: Pure Kotlin/Java
- **Dependencies**: None (except Kotlin stdlib and coroutines)
- **Contents**:
  - Domain models (entities)
  - Repository interfaces
  - Use cases (business logic)
  - Result wrapper for error handling

**Key Principles**:
- No Android dependencies
- Fully testable with unit tests
- Defines contracts (interfaces) for data layer

#### 2. **Data Layer** (`:data`)
- **Purpose**: Implementation of domain repository interfaces
- **Dependencies**: `:domain`, `:core:network`, `:core:database`, `:core:common`
- **Contents**:
  - Repository implementations
  - Data source coordinators
  - DTO to Domain mapping
  - Network Bound Resource pattern

**Key Patterns**:
- **Single Source of Truth**: Database is the single source of truth
- **Network Bound Resource**: Coordinates network and database operations
- **Offline-First**: Always emit cached data first, then fetch from network

#### 3. **Presentation Layer** (`:feature:*`)
- **Purpose**: UI and user interaction handling
- **Architecture**: MVI (Model-View-Intent)
- **Dependencies**: `:domain`, `:core:ui`, `:core:common`
- **Contents**:
  - Composable screens
  - ViewModels
  - UI state models
  - UI events/intents

**MVI Components**:
```kotlin
// State: Represents the UI state
data class DashboardState(
    val coins: List<Coin> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Intent: User actions
sealed class DashboardIntent {
    object LoadCoins : DashboardIntent()
    object RefreshCoins : DashboardIntent()
    data class SearchCoins(val query: String) : DashboardIntent()
}

// ViewModel: Processes intents and updates state
class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    fun processIntent(intent: DashboardIntent) {
        // Handle intent and update state
    }
}
```

### Core Modules

#### 1. **:core:ui**
- **Purpose**: Shared UI components and design system
- **Contents**:
  - Material 3 theme
  - Color palette
  - Typography
  - Reusable Compose components
  - Custom UI utilities

#### 2. **:core:network**
- **Purpose**: Network layer configuration
- **Contents**:
  - Retrofit configuration
  - OkHttp client
  - API services
  - Network interceptors
  - DTO models

#### 3. **:core:database**
- **Purpose**: Local persistence
- **Contents**:
  - Room database
  - DAOs (Data Access Objects)
  - Database entities
  - Database migrations

#### 4. **:core:common**
- **Purpose**: Shared utilities and extensions
- **Contents**:
  - Extension functions
  - Utility classes
  - Constants
  - Dispatcher provider for testing

---

## Data Flow

### Unidirectional Data Flow (UDF)

```
┌─────────────────────────────────────────────────────────┐
│                         UI Layer                         │
│  ┌──────────┐    ┌──────────┐    ┌──────────────────┐  │
│  │  Screen  │───▶│ViewModel │───▶│  StateFlow<State>│  │
│  │(Compose) │◀───│   (MVI)  │◀───│                  │  │
│  └──────────┘    └──────────┘    └──────────────────┘  │
│       │               │                                  │
│    Intent         UseCase                               │
└───────┼───────────────┼──────────────────────────────────┘
        │               │
        │               ▼
        │       ┌──────────────┐
        │       │   Domain     │
        │       │  Repository  │
        │       │  Interface   │
        │       └──────────────┘
        │               │
        │               ▼
        │       ┌──────────────┐
        │       │ Repository   │
        │       │     Impl     │
        │       └──────────────┘
        │          │        │
        │          ▼        ▼
        │    ┌────────┐ ┌────────┐
        │    │Network │ │Database│
        │    │ Source │ │ Source │
        │    └────────┘ └────────┘
        │          │        │
        └──────────┴────────┘
```

### Network Bound Resource Pattern

```kotlin
fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType?) -> Boolean = { true }
): Flow<Result<ResultType>> = flow {
    emit(Result.Loading)
    
    // 1. Emit cached data first
    val data = query().first()
    emit(Result.Success(data))
    
    // 2. Check if we should fetch from network
    if (shouldFetch(data)) {
        try {
            // 3. Fetch from network
            val apiResponse = fetch()
            
            // 4. Save to database
            saveFetchResult(apiResponse)
            
            // 5. Emit updated data from database
            query().collect { newData ->
                emit(Result.Success(newData))
            }
        } catch (e: Exception) {
            // Emit error but keep cached data
            emit(Result.Error(e))
        }
    }
}
```

---

## Dependency Injection

### Hilt Modules

1. **NetworkModule** (`:core:network`)
   - Provides Retrofit, OkHttp, JSON serializer

2. **DatabaseModule** (`:core:database`)
   - Provides Room database and DAOs

3. **RepositoryModule** (`:data`)
   - Binds repository interfaces to implementations

4. **DispatcherModule** (`:core:common`)
   - Provides coroutine dispatchers for testing

### Dependency Graph

```
:app
 ├── :feature:dashboard
 ├── :feature:details
 ├── :feature:favorites
 │    └── :domain ◀── :data
 │         ▲           ├── :core:network
 │         │           ├── :core:database
 │         │           └── :core:common
 │         │
 │         └── :core:ui
 │              └── :core:common
```

---

## Testing Strategy

### Unit Tests
- **Domain Layer**: Test use cases and business logic
- **Data Layer**: Test repository implementations with MockWebServer
- **ViewModel**: Test state management and intent processing

### Integration Tests
- Test repository with real database (in-memory)
- Test API integration with MockWebServer

### UI Tests
- Compose UI testing
- Screenshot/snapshot testing
- User flow testing

### Test Structure Example

```kotlin
@Test
fun `when getCoins is called, should emit loading then success`() = runTest {
    // Given
    val expectedCoins = listOf(/* test data */)
    coEvery { repository.getCoins() } returns flowOf(Result.Success(expectedCoins))
    
    // When
    val states = viewModel.state.test()
    viewModel.processIntent(DashboardIntent.LoadCoins)
    
    // Then
    states.assertValues(
        DashboardState(isLoading = true),
        DashboardState(coins = expectedCoins, isLoading = false)
    )
}
```

---

## Performance Optimizations

### 1. Build Performance
- **Gradle Build Cache**: Enabled in `gradle.properties`
- **Configuration Cache**: Enabled
- **Parallel Execution**: Enabled
- **Incremental Compilation**: Enabled for Kotlin

### 2. Runtime Performance
- **Lazy Loading**: Pagination for large lists
- **Image Caching**: Coil with custom cache strategy
- **Database Indexing**: Indexed columns for faster queries
- **Compose Optimization**: 
  - Use `remember` for expensive calculations
  - Use `derivedStateOf` for derived state
  - Use `key` for list items

### 3. Memory Management
- **BigDecimal for Finance**: Prevents precision loss
- **Flow for Reactive Data**: Lifecycle-aware collection
- **Resource Cleanup**: Proper ViewModel cleanup

---

## Code Quality

### Static Analysis
- **detekt**: Kotlin code analysis
- **ktlint**: Code style enforcement

### Code Coverage Goals
- Domain layer: 80%+
- Data layer: 70%+
- Presentation layer: 60%+

### CI/CD Pipeline
1. **Lint**: ktlint + detekt
2. **Test**: Unit tests
3. **Build**: Debug + Release APK
4. **Report**: Test coverage and static analysis reports

---

## Security Best Practices

1. **API Keys**: Stored in `local.properties`, not committed
2. **ProGuard**: Enabled for release builds
3. **Certificate Pinning**: Implemented for API calls (TODO)
4. **Encrypted Storage**: For sensitive data (TODO)

---

## Future Enhancements

### Planned Features
- [ ] Real-time price updates (WebSocket)
- [ ] Price alerts
- [ ] Portfolio tracking
- [ ] Biometric authentication
- [ ] Widget support
- [ ] Wear OS companion app

### Technical Improvements
- [ ] Implement full API integration
- [ ] Add comprehensive test coverage
- [ ] Implement certificate pinning
- [ ] Add crash reporting (Firebase Crashlytics)
- [ ] Add analytics (Firebase Analytics)
- [ ] Implement in-app updates

---

## Resources

- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVI Architecture](https://hannesdorfmann.com/android/model-view-intent/)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose/performance)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
