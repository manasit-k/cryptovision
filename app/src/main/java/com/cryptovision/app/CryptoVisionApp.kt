package com.cryptovision.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for CryptoVision.
 * Initializes Hilt dependency injection and Timber logging.
 */
@HiltAndroidApp
class CryptoVisionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, you might want to use a custom tree that logs to Crashlytics
            Timber.plant(ReleaseTree())
        }
        
        Timber.d("CryptoVision App initialized")
    }

    /**
     * Custom Timber tree for release builds.
     * Only logs warnings and errors to prevent sensitive information leakage.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == android.util.Log.ERROR || priority == android.util.Log.WARN) {
                // In a real app, send to crash reporting service like Firebase Crashlytics
                // FirebaseCrashlytics.getInstance().log(message)
                // if (t != null) FirebaseCrashlytics.getInstance().recordException(t)
            }
        }
    }
}
