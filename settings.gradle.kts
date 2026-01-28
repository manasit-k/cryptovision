pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CryptoVision"

// App module
include(":app")

// Core modules
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:common")

// Data layer
include(":data")

// Domain layer
include(":domain")

// Feature modules
include(":feature:dashboard")
include(":feature:details")
include(":feature:favorites")
