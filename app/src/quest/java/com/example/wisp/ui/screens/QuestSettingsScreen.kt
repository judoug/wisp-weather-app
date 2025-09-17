package com.example.wisp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wisp.ui.quest.QuestLocationSettings
import com.example.wisp.ui.quest.QuestSpacing
import com.example.wisp.ui.quest.QuestSpacingSize
import com.example.wisp.ui.quest.questButton
import com.example.wisp.ui.quest.questCard
import com.example.wisp.ui.quest.questFocusable
import com.example.wisp.ui.quest.questTouchTarget

/**
 * Quest-specific settings screen with VR/AR optimizations.
 * 
 * Features:
 * - Large touch targets for controller interaction
 * - Focus support for keyboard/controller navigation
 * - Quest-specific location settings
 * - Optimized for panel display (1200-1600px wide)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLocationSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuestSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation events
    LaunchedEffect(uiState.navigateToLocationSearch) {
        if (uiState.navigateToLocationSearch) {
            onNavigateToLocationSearch()
            viewModel.onNavigationHandled()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.questTouchTarget()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(QuestUiUtils.QUEST_LARGE_SPACING)
        ) {
            QuestSpacing(QuestSpacingSize.LARGE)
            
            // Location Settings Section
            Text(
                text = "Location",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.questFocusable()
            )
            
            QuestLocationSettings(
                isIpLocationEnabled = uiState.isIpLocationEnabled,
                onIpLocationToggle = viewModel::onIpLocationToggle,
                onManualLocationClick = viewModel::onManualLocationClick,
                currentLocationName = uiState.currentLocationName,
                modifier = Modifier.questCard()
            )
            
            QuestSpacing(QuestSpacingSize.MEDIUM)
            
            // Additional Quest-specific settings can be added here
            // For example: VR comfort settings, display preferences, etc.
        }
    }
}
