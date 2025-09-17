package com.example.wisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.wisp.ui.navigation.QuestNavigation
import com.example.wisp.design.theme.WispTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Quest-specific MainActivity with VR/AR optimizations.
 * 
 * Features:
 * - Landscape orientation lock
 * - Immersive mode for VR panels
 * - Edge-to-edge display
 * - Quest-specific navigation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for Quest panels
        enableEdgeToEdge()
        
        // Configure window for Quest VR panel display
        configureForQuest()
        
        setContent {
            WispTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    QuestNavigation(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
    
    private fun configureForQuest() {
        // Hide system bars for immersive VR experience
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Hide navigation and status bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        // Keep screen on for VR usage
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Set landscape orientation
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    
    override fun onResume() {
        super.onResume()
        // Re-hide system bars when returning to the app
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}
