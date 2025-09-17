package com.example.wisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wisp.design.theme.WispTheme
// import com.example.wisp.performance.StartupOptimizationManager
import com.example.wisp.ui.WispApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // @Inject
    // lateinit var startupOptimizationManager: StartupOptimizationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Record activity creation time
        // startupOptimizationManager.recordFirstActivityCreate()
        
        setContent {
            WispTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WispApp()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // Record activity resume time and log startup metrics
        // startupOptimizationManager.recordFirstActivityResume()
        // startupOptimizationManager.logStartupMetrics()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WispTheme {
        WispApp()
    }
}

