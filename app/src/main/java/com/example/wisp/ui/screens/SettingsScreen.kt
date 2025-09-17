package com.example.wisp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wisp.design.theme.WispTheme
import com.example.wisp.ui.state.RefreshInterval
import com.example.wisp.ui.state.TemperatureUnit
import com.example.wisp.ui.components.AccessibilitySettingsCard
import com.example.wisp.ui.components.DarkModeToggleCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Local state for accessibility and dark mode
    var isDarkMode by remember { mutableStateOf(false) }
    var highContrastEnabled by remember { mutableStateOf(false) }
    var largeTextEnabled by remember { mutableStateOf(false) }
    var screenReaderEnabled by remember { mutableStateOf(false) }
    
    // Show snackbar for save errors
    LaunchedEffect(uiState.hasSaveError) {
        if (uiState.hasSaveError) {
            uiState.saveErrorMessage?.let { message ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Retry"
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (uiState.isSavingSettings) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Temperature Unit Setting
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Temperature Unit",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    
                    var tempUnitExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = tempUnitExpanded,
                        onExpandedChange = { tempUnitExpanded = !tempUnitExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.temperatureUnit.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tempUnitExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = tempUnitExpanded,
                            onDismissRequest = { tempUnitExpanded = false }
                        ) {
                            TemperatureUnit.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.displayName) },
                                    onClick = {
                                        viewModel.updateTemperatureUnit(unit)
                                        tempUnitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Refresh Interval Setting
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Refresh Interval",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    
                    var refreshIntervalExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = refreshIntervalExpanded,
                        onExpandedChange = { refreshIntervalExpanded = !refreshIntervalExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.refreshInterval.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = refreshIntervalExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = refreshIntervalExpanded,
                            onDismissRequest = { refreshIntervalExpanded = false }
                        ) {
                            RefreshInterval.values().forEach { interval ->
                                DropdownMenuItem(
                                    text = { Text(interval.displayName) },
                                    onClick = {
                                        viewModel.updateRefreshInterval(interval)
                                        refreshIntervalExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Notifications Setting
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weather Notifications",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Receive weather alerts and updates",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = viewModel::updateNotificationsEnabled
                        )
                    }
                }
            }
            
            // Location Permission Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Location Permission",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = if (uiState.locationPermissionGranted) {
                            "Location access granted"
                        } else {
                            "Location access required for current location weather"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.locationPermissionGranted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
            
            // Dark Mode Toggle Card
            DarkModeToggleCard(
                isDarkMode = isDarkMode,
                onToggle = { isDarkMode = it },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Accessibility Settings Card
            AccessibilitySettingsCard(
                onHighContrastToggle = { highContrastEnabled = it },
                onLargeTextToggle = { largeTextEnabled = it },
                onScreenReaderToggle = { screenReaderEnabled = it },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Reset to Defaults Button
            Button(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset to Defaults")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WispTheme {
        SettingsScreen(
            onNavigateBack = {}
        )
    }
}
