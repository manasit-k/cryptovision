package com.cryptovision.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cryptovision.app.navigation.CryptoVisionNavHost
import com.cryptovision.core.ui.theme.CryptoVisionTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity for the entire app.
 * Uses Jetpack Compose for UI and Navigation Component for navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CryptoVisionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CryptoVisionNavHost()
                }
            }
        }
    }
}
