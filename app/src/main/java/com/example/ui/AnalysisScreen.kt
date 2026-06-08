package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextSecondarySlate
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

@Composable
fun AnalysisScreen(
    stats: MonthlyStats,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Daily Average Dynamic Calculation
    val expenseEntries = stats.filteredEntries.filter { it.type == TransactionType.EXPENSE }
    val totalSpent = stats.totalExpenses
    val daysCount = if (expenseEntries.isNotEmpty()) {
        val cal = Calendar.getInstance()
        expenseEntries.map {
            cal.timeInMillis = it.date
            cal.get(Calendar.DAY_OF_MONTH)
        }.distinct().size
    } else {
        1
    }
    val dailySpendingAvg = totalSpent / max(1, daysCount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("analysis_screen_container"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        
        // 1. Sleek Hero Balance Card (Main Balance Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("net_balance_hero_card"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF112240) // CardNavySurface
            ),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Net Balance",
                    fontSize = 14.sp,
                    color = TextSecondarySlate,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale.US, "$%,.2f", stats.netBalance),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    
                    // Surplus trend tag
                    val isSurplus = stats.netBalance >= 0.0
                    val trendText = if (isSurplus) {
                        if (stats.savingsPercentage > 0.0) {
                            String.format(Locale.US, "+%.0f%%", stats.savingsPercentage)
                        } else {
                            "SURPLUS"
                        }
                    } else {
                        "DEFICIT"
                    }
                    val trendColor = if (isSurplus) IncomeGreen else ExpenseRed
                    val trendBgColor = if (isSurplus) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(trendBgColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSurplus) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = trendColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = trendText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = trendColor
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Income vs Expenses side-by-side details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Income",
                            fontSize = 11.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "$%,.2f", stats.totalIncome),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IncomeGreen
                        )
                    }
                    
                    // Vertical Separator
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(Color(0xFF334155)) // slate-700
                    )
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp)
                    ) {
                        Text(
                            text = "Expenses",
                            fontSize = 11.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "$%,.2f", stats.totalExpenses),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ExpenseRed
                        )
                    }
                }
            }
        }
        
        // 2. Savings & Stats (Grid Section)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SAVINGS RATE CARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("savings_rate_card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF112240)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Percent,
                            contentDescription = null,
                            tint = TextSecondarySlate,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Savings Rate",
                            fontSize = 13.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.US, "%.0f%%", stats.savingsPercentage.coerceIn(0.0, 100.0)),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2196F3)
                    )
                }
            }
            
            // DAILY AVG CARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("daily_avg_card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF112240)
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = TextSecondarySlate,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Daily Avg.",
                            fontSize = 13.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.US, "$%,.2f", dailySpendingAvg),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
        
        // 3. Weekly Comparison (Bar Chart) Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weekly_comparison_chart_box"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF112240)
            ),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Weekly Comparison",
                    fontSize = 12.sp,
                    color = TextSecondarySlate,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Calculate weekly breakdowns for the 4 periods
                val cal = Calendar.getInstance()
                val weeklyBreakdown = (1..4).map { weekIndex ->
                    val daysRange = when (weekIndex) {
                        1 -> 1..7
                        2 -> 8..14
                        3 -> 15..21
                        else -> 22..31
                    }
                    val weekEntries = stats.filteredEntries.filter { entry ->
                        cal.timeInMillis = entry.date
                        val day = cal.get(Calendar.DAY_OF_MONTH)
                        day in daysRange
                    }
                    val incomeSum = weekEntries.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expenseSum = weekEntries.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    Pair(incomeSum, expenseSum)
                }
                
                // Render comparison bars
                if (stats.filteredEntries.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TextSecondarySlate.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Chart Data For This Month",
                            fontSize = 13.sp,
                            color = TextSecondarySlate,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Max value calculation for height scale
                        val maxWeeklySum = weeklyBreakdown.flatMap { listOf(it.first, it.second) }.maxOrNull() ?: 1.0
                        val capMax = if (maxWeeklySum <= 0.0) 100.0 else maxWeeklySum

                        // Trigger animations
                        var chartAnimate by remember { mutableStateOf(false) }
                        LaunchedEffect(stats) {
                            chartAnimate = true
                        }

                        weeklyBreakdown.forEachIndexed { idx, pair ->
                            val income = pair.first
                            val expense = pair.second
                            
                            val animatedIncFactor by animateFloatAsState(
                                targetValue = if (chartAnimate) (income / capMax).toFloat() else 0f,
                                animationSpec = tween(600),
                                label = "inc_height_${idx}"
                            )
                            val animatedExpFactor by animateFloatAsState(
                                targetValue = if (chartAnimate) (expense / capMax).toFloat() else 0f,
                                animationSpec = tween(600),
                                label = "exp_height_${idx}"
                            )

                            val isW4 = idx == 3 // W4 represents active or current week in HTML sample, with ring focus and highlighted text

                            // Column for a single week's block
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Draw double bars container
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        // W4 glows with secondary primary outline
                                        .then(
                                            if (isW4) {
                                                Modifier
                                                    .border(2.dp, Color(0xFF2196F3), RoundedCornerShape(8.dp))
                                                    .padding(6.dp)
                                            } else {
                                                Modifier.padding(6.dp)
                                            }
                                        ),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        // Income Bar (Green)
                                        val incHeightPct = animatedIncFactor.coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight(fraction = max(0.01f, incHeightPct))
                                                .background(IncomeGreen, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        )
                                        
                                        // Expense Bar (Red)
                                        val expHeightPct = animatedExpFactor.coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight(fraction = max(0.01f, expHeightPct))
                                                .background(ExpenseRed, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Label (W1, W2, etc.)
                                Text(
                                    text = "W${idx + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isW4) Color(0xFF2196F3) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
