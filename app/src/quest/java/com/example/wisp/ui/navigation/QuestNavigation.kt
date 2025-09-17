package com.example.wisp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wisp.ui.screens.QuestSettingsScreen
import com.example.wisp.ui.screens.HomeScreen
import com.example.wisp.ui.screens.SearchScreen
import com.example.wisp.ui.screens.LocationsScreen

/**
 * Quest-specific navigation with VR/AR optimizations.
 * 
 * Features:
 * - Simplified navigation for VR panels
 * - Large touch targets
 * - Controller-friendly navigation
 * - Quest-optimized screen transitions
 */
@Composable
fun QuestNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToLocations = {
                    navController.navigate("locations")
                },
                onNavigateToSearch = {
                    navController.navigate("search")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            QuestSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLocationSearch = {
                    navController.navigate("search")
                }
            )
        }
        
        composable("search") {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("locations") {
            LocationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSearch = {
                    navController.navigate("search")
                }
            )
        }
    }
}
