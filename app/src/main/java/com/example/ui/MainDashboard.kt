package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.TextSecondarySlate
import java.util.Calendar

enum class DashboardTab(val route: String, val label: String, val icon: ImageVector) {
    ADD_ENTRY("add_entry", "Add Entry", Icons.Default.PostAdd),
    LIST("list", "List", Icons.Default.ReceiptLong),
    ANALYSIS("analysis", "Analysis", Icons.Default.Analytics)
}

@Composable
fun MainDashboard(
    viewModel: EntryViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(DashboardTab.ADD_ENTRY) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    // Collect variables synchronously
    val stats by viewModel.monthlyStats.collectAsState()
    val availableYears by viewModel.availableYears.collectAsState()
    val currentMonth by viewModel.selectedMonth.collectAsState()
    val currentYear by viewModel.selectedYear.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    var showPaywall by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            ) {
                // 1. Sleek Header Section mirroring the HTML exactly
                val subtitleLabel = when (activeTab) {
                    DashboardTab.ADD_ENTRY -> "ADD TRANSACTION"
                    DashboardTab.LIST -> "TRANSACTIONS LIST"
                    DashboardTab.ANALYSIS -> "ANALYSIS DASHBOARD"
                }
                
                val monthNames = listOf(
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
                )
                
                val titleLabel = when (activeTab) {
                    DashboardTab.ADD_ENTRY -> "New Record"
                    else -> "${monthNames[currentMonth]} $currentYear"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = subtitleLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF2196F3) // Bright blue matching html template
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = titleLabel,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Interactive Premium Badge matching the design themes exactly
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isPremium) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    else Color(0xFF2196F3).copy(alpha = 0.15f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isPremium) Color(0xFF4CAF50) else Color(0xFF2196F3),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { showPaywall = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("premium_status_badge"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isPremium) "PRO ACTIVE" else "GET PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPremium) Color(0xFF4CAF50) else Color(0xFF2196F3),
                                letterSpacing = 0.5.sp
                            )
                        }

                        // User icon block from Sleek Interface template or generic visual anchor
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color(0xFF112240))
                                .border(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f), RoundedCornerShape(22.dp))
                                .clickable { showPaywall = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                // Conditional Month-Year Filter Bar shown on LIST or ANALYSIS screen
                if (activeTab == DashboardTab.LIST || activeTab == DashboardTab.ANALYSIS) {
                    Spacer(modifier = Modifier.height(16.dp))
                    MonthYearFilterBar(
                        selectedMonth = currentMonth,
                        selectedYear = currentYear,
                        availableYears = availableYears,
                        onMonthSelected = { viewModel.selectMonth(it) },
                        onYearSelected = { viewModel.selectYear(it) }
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_app_nav_bar")
                    .background(MaterialTheme.colorScheme.background),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                DashboardTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = TextSecondarySlate,
                            unselectedIconColor = TextSecondarySlate
                        ),
                        modifier = Modifier
                            .testTag("nav_tab_${tab.route}")
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        // Standard high-performance animated content transitions
        AnimatedContent(
            targetState = activeTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            transitionSpec = {
                fadeIn(animationSpec = tween(120)) togetherWith
                fadeOut(animationSpec = tween(120))
            },
            label = "tab_transition"
        ) { tab ->
            when (tab) {
                DashboardTab.ADD_ENTRY -> {
                    AddEntryScreen(
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState,
                        onSuccess = {
                            activeTab = DashboardTab.LIST // Auto-redirect to list for visual verification
                        },
                        onShowPaywall = {
                            showPaywall = true
                        }
                    )
                }
                DashboardTab.LIST -> {
                    ListScreen(
                        viewModel = viewModel,
                        entries = stats.filteredEntries
                    )
                }
                DashboardTab.ANALYSIS -> {
                    AnalysisScreen(
                        stats = stats
                    )
                }
            }
        }
    }
        
    if (showPaywall) {
        PaywallScreen(
            onDismiss = { showPaywall = false },
            onSuccess = {
                showPaywall = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Welcome to Sleek Pro! Premium features unlocked.")
                }
            },
            viewModel = viewModel
        )
    }
}
}

@Composable
fun MonthYearFilterBar(
    selectedMonth: Int,
    selectedYear: Int,
    availableYears: List<Int>,
    onMonthSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    var monthMenuExpanded by remember { mutableStateOf(false) }
    var yearMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("month_year_filter_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Prev button (decrement month)
        IconButton(
            onClick = {
                if (selectedMonth > 0) {
                    onMonthSelected(selectedMonth - 1)
                } else {
                    onMonthSelected(11)
                    if (availableYears.contains(selectedYear - 1)) {
                        onYearSelected(selectedYear - 1)
                    }
                }
            },
            modifier = Modifier
                .size(48.dp) // Stylus safe target
                .testTag("prev_month_button"),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Month"
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // MONTH DROPDOWN CHIP
            Box {
                Card(
                    onClick = { monthMenuExpanded = true },
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("month_dropdown_chip"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = borderIndicator()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = monthNames[selectedMonth],
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                ) {
                    monthNames.forEachIndexed { idx, name ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = name, 
                                    color = if (idx == selectedMonth) MaterialTheme.colorScheme.primary else Color.White,
                                    fontWeight = if (idx == selectedMonth) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            onClick = {
                                onMonthSelected(idx)
                                monthMenuExpanded = false
                            },
                            modifier = Modifier.testTag("select_month_item_$idx")
                        )
                    }
                }
            }

            // YEAR DROPDOWN CHIP
            Box {
                Card(
                    onClick = { yearMenuExpanded = true },
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("year_dropdown_chip"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = borderIndicator()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = selectedYear.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = yearMenuExpanded,
                    onDismissRequest = { yearMenuExpanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, BorderSlate, RoundedCornerShape(8.dp))
                ) {
                    availableYears.forEach { yr ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = yr.toString(), 
                                    color = if (yr == selectedYear) MaterialTheme.colorScheme.primary else Color.White,
                                    fontWeight = if (yr == selectedYear) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            onClick = {
                                onYearSelected(yr)
                                yearMenuExpanded = false
                            },
                            modifier = Modifier.testTag("select_year_item_$yr")
                        )
                    }
                }
            }
        }

        // Next button (increment month)
        IconButton(
            onClick = {
                if (selectedMonth < 11) {
                    onMonthSelected(selectedMonth + 1)
                } else {
                    onMonthSelected(0)
                    if (availableYears.contains(selectedYear + 1)) {
                        onYearSelected(selectedYear + 1)
                    }
                }
            },
            modifier = Modifier
                .size(48.dp) // Stylus safe target
                .testTag("next_month_button"),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Month"
            )
        }
    }
}

@Composable
fun borderIndicator() = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
