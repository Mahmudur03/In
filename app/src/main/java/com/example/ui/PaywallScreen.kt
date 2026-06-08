package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextSecondarySlate

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EntryViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedPlanIndex by remember { mutableStateOf(1) } // Default to Lifetime (1)
    val isPremium by viewModel.isPremium.collectAsState()
    
    // Feature item model
    data class FeatureItem(val title: String, val desc: String, val icon: ImageVector)
    
    val features = listOf(
        FeatureItem("Unlimited Transactions", "Remove the 5-transaction free limit to track your finances indefinitely.", Icons.Default.Star),
        FeatureItem("Pro Financial Analysis", "Unlock deep insights, daily average spending graphs, and savings trend percentages.", Icons.Default.Analytics),
        FeatureItem("Dynamic Comparison Charts", "Access interactive monthly and weekly bars with detailed breakdowns.", Icons.Default.TrendingUp),
        FeatureItem("Automatic Cloud Backups", "Securely sync your entire database to the cloud to prevent telemetry loss.", Icons.Default.Check)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F)) // Cosmic Deep Slate Base
            .testTag("paywall_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            // Header dismiss section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shiny custom badge under top bar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2196F3).copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "PREMIUM ACCOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3),
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Smooth close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF112240))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .testTag("paywall_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close paywall",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Promotional Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sleek Budget Pro",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gain full clarity over your personal cash flow and unlock professional grade analysis tracking tools.",
                    fontSize = 14.sp,
                    color = TextSecondarySlate,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Unlocked Benefits List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                features.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF112240).copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2196F3).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.title,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feature.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = feature.desc,
                                fontSize = 12.sp,
                                color = TextSecondarySlate,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Plan Selectors Title
            Text(
                text = "CHOOSE YOUR PLAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2196F3),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Option Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Monthly Plan Option
                val isMonthlySelected = selectedPlanIndex == 0
                val monthlyScale by animateFloatAsState(if (isMonthlySelected) 1.02f else 1.0f, label = "monthly_scale")
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .scale(monthlyScale)
                        .clickable { selectedPlanIndex = 0 }
                        .testTag("plan_monthly_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMonthlySelected) Color(0xFF112240) else Color(0xFF112240).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        width = if (isMonthlySelected) 2.dp else 1.dp,
                        color = if (isMonthlySelected) Color(0xFF2196F3) else Color.White.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Monthly",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextSecondarySlate
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$4.99",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "/ month",
                            fontSize = 11.sp,
                            color = TextSecondarySlate
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isMonthlySelected) Color(0xFF2196F3) else Color.Transparent
                                )
                                .border(
                                    1.dp,
                                    if (isMonthlySelected) Color.Transparent else Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMonthlySelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Lifetime Plan Option (Recommended glow)
                val isLifetimeSelected = selectedPlanIndex == 1
                val lifetimeScale by animateFloatAsState(if (isLifetimeSelected) 1.02f else 1.0f, label = "lifetime_scale")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .scale(lifetimeScale)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlanIndex = 1 }
                            .testTag("plan_lifetime_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLifetimeSelected) Color(0xFF112240) else Color(0xFF112240).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = if (isLifetimeSelected) 2.dp else 1.dp,
                            color = if (isLifetimeSelected) Color(0xFF2196F3) else Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Lifetime",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$19.99",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "one-time payment",
                                fontSize = 11.sp,
                                color = TextSecondarySlate,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLifetimeSelected) Color(0xFF2196F3) else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isLifetimeSelected) Color.Transparent else Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLifetimeSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Popular visual tag overlaying corner
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3))
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "60% OFF",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Pulsing Primary Call To Action Button
            Button(
                onClick = {
                    if (isPremium) {
                        viewModel.lockPremium()
                    } else {
                        viewModel.unlockPremium()
                    }
                    onSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 24.dp)
                    .testTag("activate_premium_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPremium) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isPremium) "Sleek Pro is Active! (Tap to Lock)" else if (selectedPlanIndex == 1) "ACTIVATE LIFETIME PRO" else "SUBSCRIBE MONTHLY PRO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Restore Options & Auxiliary Legal Links
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restore Purchase",
                    fontSize = 11.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            viewModel.unlockPremium()
                            onSuccess()
                        }
                        .testTag("paywall_restore_button")
                )
                
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = TextSecondarySlate
                )

                Text(
                    text = "Terms of Use",
                    fontSize = 11.sp,
                    color = TextSecondarySlate,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )

                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = TextSecondarySlate
                )

                Text(
                    text = "Privacy Policy",
                    fontSize = 11.sp,
                    color = TextSecondarySlate,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}
