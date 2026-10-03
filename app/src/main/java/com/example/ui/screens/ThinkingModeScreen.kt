package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SupplyFlowViewModel
import com.example.ui.theme.TertiaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThinkingModeScreen(
    viewModel: SupplyFlowViewModel,
    modifier: Modifier = Modifier
) {
    val thinkingState by viewModel.thinkingState.collectAsStateWithLifecycle()

    var userQuery by remember {
        mutableStateOf(
            "We have a 12-day shipping delay at the Pacific port affecting 1,500 units of Industrial Controllers. Compare holding penalty cost vs air freight express splitting strategy."
        )
    }

    var showThoughtProcess by remember { mutableStateOf(true) }

    val presetQueries = listOf(
        "Pacific Port 12-Day Bottleneck & Air Split",
        "Flash Sale 500% Surge Buffer Sizing",
        "Cross-Border Landed Cost & HS 8517 Tariff",
        "Cold-Chain Excursion Mitigation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("thinking_mode_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TertiaryLight
                        ) {
                            Text(
                                text = "MODEL: gemini-3.1-pro-preview",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "ThinkingLevel.HIGH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Strategic Supply Chain AI Brain",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mathematical optimization, risk models, multi-modal logistics trade-offs & global tariff algorithms.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Preset Scenarios
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Select Complex Operational Scenario",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetQueries) { preset ->
                            SuggestionChip(
                                onClick = {
                                    userQuery = when (preset) {
                                        "Pacific Port 12-Day Bottleneck & Air Split" ->
                                            "Pacific port congestion is causing a 12-day arrival variance for 1,500 units of SKU-LOG-84920. Calculate Pareto-optimal air freight split ratio vs holding penalty."
                                        "Flash Sale 500% Surge Buffer Sizing" ->
                                            "E-commerce flash promotion will spike demand by 500% over 72 hours. Model Poisson distribution to calculate safety stock for top 3 velocity items."
                                        "Cross-Border Landed Cost & HS 8517 Tariff" ->
                                            "Analyze total landed cost with import tariffs across EU, US, and ASEAN jurisdictions for dual-use communication electronics."
                                        else ->
                                            "Cold-chain temperature excursion detected in transit (+4°C above limit for 2.5 hours). Determine pharmaceutical shelf life impact and quarantine protocol."
                                    }
                                },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Complex Query Input
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Complex Query Specification",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("thinking_query_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Operational Challenge Prompt") }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.runHighThinkingQuery(userQuery)
                        },
                        enabled = !thinkingState.isThinking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("run_thinking_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TertiaryLight)
                    ) {
                        if (thinkingState.isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Deep Reasoning in Progress (ThinkingLevel.HIGH)...", color = Color.White)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Execute Deep Thinking Analysis", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Thinking Output Display
        if (thinkingState.finalStrategy != null || thinkingState.thinkingProcess != null) {
            // Internal Thought Process Accordion
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("thinking_process_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showThoughtProcess = !showThoughtProcess },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Internal Reasoning Path (ThinkingLevel.HIGH)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (showThoughtProcess) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle"
                            )
                        }

                        if (showThoughtProcess) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = thinkingState.thinkingProcess ?: "Analyzing multi-tier dependencies...",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Strategic Execution Plan Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("strategic_solution_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Strategic Operational Blueprint",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TertiaryLight)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        Text(
                            text = thinkingState.finalStrategy ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}
