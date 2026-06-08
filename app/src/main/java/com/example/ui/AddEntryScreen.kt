package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddEntryScreen(
    viewModel: EntryViewModel,
    snackbarHostState: SnackbarHostState,
    onSuccess: () -> Unit,
    onShowPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isPremium by viewModel.isPremium.collectAsState()
    val stats by viewModel.monthlyStats.collectAsState()

    // Form states
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    // Error states
    var amountError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }

    val formattedDate = remember(selectedDateMillis) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        sdf.format(selectedDateMillis)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
            .testTag("add_entry_screen_container"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome and Stylus Helper card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Stylus ready",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Stylus Input Optimized",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Inputs are extra tall with helpful controls so you can comfortably write or tap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Transaction Type Toggle
        Text(
            text = "Transaction Type",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // INCOME BUTTON
            val incomeBg by animateColorAsState(
                targetValue = if (selectedType == TransactionType.INCOME) IncomeGreen else Color.Transparent,
                label = "incomeBg"
            )
            val incomeTextCol by animateColorAsState(
                targetValue = if (selectedType == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "incomeText"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(incomeBg)
                    .clickable { selectedType = TransactionType.INCOME }
                    .testTag("toggle_income_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Paid, 
                        contentDescription = "Income",
                        tint = incomeTextCol,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Income",
                        fontWeight = FontWeight.Bold,
                        color = incomeTextCol,
                        fontSize = 15.sp
                    )
                }
            }

            // EXPENSE BUTTON
            val expenseBg by animateColorAsState(
                targetValue = if (selectedType == TransactionType.EXPENSE) ExpenseRed else Color.Transparent,
                label = "expenseBg"
            )
            val expenseTextCol by animateColorAsState(
                targetValue = if (selectedType == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "expenseText"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(expenseBg)
                    .clickable { selectedType = TransactionType.EXPENSE }
                    .testTag("toggle_expense_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = "Expense",
                        tint = expenseTextCol,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Expense",
                        fontWeight = FontWeight.Bold,
                        color = expenseTextCol,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // 2. Amount Input
        val amountBorderColor = if (amountError != null) ExpenseRed else MaterialTheme.colorScheme.outline
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Amount ($)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    if (amountError != null) amountError = null
                },
                placeholder = { Text("0.00", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("amount_input_field"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = amountBorderColor,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )
            if (amountError != null) {
                Text(
                    text = amountError!!,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // 3. Description Input
        val descBorderColor = if (descriptionError != null) ExpenseRed else MaterialTheme.colorScheme.outline
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = descriptionText,
                onValueChange = {
                    descriptionText = it
                    if (descriptionError != null) descriptionError = null
                },
                placeholder = { Text("What was this for?", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("description_input_field"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = descriptionError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = descBorderColor,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )
            if (descriptionError != null) {
                Text(
                    text = descriptionError!!,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // 4. Date Picker
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Transaction Date",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDateMillis
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val newCal = Calendar.getInstance()
                                newCal.set(y, m, d)
                                selectedDateMillis = newCal.timeInMillis
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .padding(horizontal = 16.dp)
                    .testTag("date_picker_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Pick Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Submit Button
        Button(
            onClick = {
                // Validation Process
                val amount = amountText.toDoubleOrNull()
                var hasError = false

                if (amount == null || amount <= 0.0) {
                    amountError = "Please enter a valid amount greater than 0"
                    hasError = true
                }
                if (descriptionText.trim().isEmpty()) {
                    descriptionError = "Please enter a description"
                    hasError = true
                }

                if (!hasError) {
                    if (!isPremium && stats.filteredEntries.size >= 5) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Free tier is limited to 5 records. Please unlock Pro tracking!")
                        }
                        onShowPaywall()
                        return@Button
                    }

                    viewModel.addEntry(
                        amount = amount!!,
                        description = descriptionText.trim(),
                        date = selectedDateMillis,
                        type = selectedType
                    )

                    // Clear inputs
                    amountText = ""
                    descriptionText = ""
                    selectedDateMillis = System.currentTimeMillis()

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Transaction added successfully!")
                    }
                    onSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("submit_entry_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
