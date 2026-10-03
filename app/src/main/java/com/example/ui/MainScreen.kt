package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SupplyFlowViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showCurrencyMenu by remember { mutableStateOf(false) }

    // When on a sub-screen, pressing Back navigates back to Dashboard
    if (currentTab != AppNavTab.DASHBOARD) {
        BackHandler {
            viewModel.navigateToTab(AppNavTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_supplyflow_logo),
                            contentDescription = "SupplyFlow Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SupplyFlow",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Autonomous Supply Chain & E-Commerce",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Currency Selector
                    Box {
                        TextButton(
                            onClick = { showCurrencyMenu = true },
                            modifier = Modifier.testTag("currency_selector_button")
                        ) {
                            Text(
                                text = selectedCurrency,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Currency",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showCurrencyMenu,
                            onDismissRequest = { showCurrencyMenu = false }
                        ) {
                            viewModel.currencyRates.keys.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text("$curr (${viewModel.currencySymbols[curr]})") },
                                    onClick = {
                                        viewModel.setCurrency(curr)
                                        showCurrencyMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Dark/Light Mode Toggle
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("dark_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                ScrollableTabRow(
                    selectedTabIndex = AppNavTab.values().indexOf(currentTab),
                    edgePadding = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val navItems = listOf(
                        Triple(AppNavTab.DASHBOARD, Icons.Default.Dashboard, "Home"),
                        Triple(AppNavTab.VIDEO_INSPECT, Icons.Default.VideoLibrary, "Video AI"),
                        Triple(AppNavTab.AUDIO_TRANSCRIBE, Icons.Default.Mic, "Transcribe"),
                        Triple(AppNavTab.THINKING_BRAIN, Icons.Default.Psychology, "AI Brain"),
                        Triple(AppNavTab.CHAT_ASSISTANT, Icons.Default.Chat, "Chat AI"),
                        Triple(AppNavTab.CREATIVE_STUDIO, Icons.Default.AutoAwesome, "Studio"),
                        Triple(AppNavTab.INVENTORY, Icons.Default.Inventory2, "Stock"),
                        Triple(AppNavTab.ORDERS, Icons.Default.LocalShipping, "Orders"),
                        Triple(AppNavTab.AUTH_PROFILE, Icons.Default.AccountCircle, "Account")
                    )

                    navItems.forEach { (tab, icon, label) ->
                        val isSelected = currentTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.navigateToTab(tab) },
                            text = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp)) },
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppNavTab.VIDEO_INSPECT -> VideoInspectionScreen(viewModel = viewModel)
                AppNavTab.AUDIO_TRANSCRIBE -> AudioTranscriptionScreen(viewModel = viewModel)
                AppNavTab.THINKING_BRAIN -> ThinkingModeScreen(viewModel = viewModel)
                AppNavTab.CHAT_ASSISTANT -> ChatAssistantScreen(viewModel = viewModel)
                AppNavTab.CREATIVE_STUDIO -> CreativeStudioScreen(viewModel = viewModel)
                AppNavTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                AppNavTab.ORDERS -> OrdersScreen(viewModel = viewModel)
                AppNavTab.AUTH_PROFILE -> ProfileAuthScreen(viewModel = viewModel)
            }
        }
    }
}
