package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Entry
import com.example.data.EntryRepository
import com.example.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class MonthlyStats(
    val totalIncome: Double,
    val totalExpenses: Double,
    val netBalance: Double,
    val savingsPercentage: Double,
    val filteredEntries: List<Entry>
)

class EntryViewModel(private val repository: EntryRepository) : ViewModel() {

    private val currentCalendar = Calendar.getInstance()
    
    private val _selectedMonth = MutableStateFlow(currentCalendar.get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(currentCalendar.get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    // Combined state for calculations based on local Room flow + filter states
    val monthlyStats: StateFlow<MonthlyStats> = combine(
        repository.allEntries,
        _selectedMonth,
        _selectedYear
    ) { entries, month, year ->
        val filtered = entries.filter { entry ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = entry.date
            cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
        }

        val income = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val net = income - expenses
        
        // Savings % = ((Income - Expense) / Income) * 100. Ensure non-negative math when appropriate
        val savings = if (income > 0.0) {
            val ratio = (income - expenses) / income
            (ratio * 100.0)
        } else {
            0.0
        }

        MonthlyStats(
            totalIncome = income,
            totalExpenses = expenses,
            netBalance = net,
            savingsPercentage = savings,
            filteredEntries = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyStats(0.0, 0.0, 0.0, 0.0, emptyList())
    )

    // All available unique years present in cumulative database entries so users can filter easily,
    // defaulting to current year and some bounding years if empty.
    val availableYears: StateFlow<List<Int>> = repository.allEntries.combine(_selectedYear) { entries, currentYear ->
        val years = entries.map { entry ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = entry.date
            cal.get(Calendar.YEAR)
        }.distinct().sorted()
        
        if (years.isEmpty() || !years.contains(currentYear)) {
            (years + currentYear).distinct().sorted()
        } else {
            years
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(Calendar.getInstance().get(Calendar.YEAR))
    )

    fun selectMonth(month: Int) {
        if (month in 0..11) {
            _selectedMonth.value = month
        }
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
    }

    fun addEntry(amount: Double, description: String, date: Long, type: TransactionType) {
        viewModelScope.launch {
            val entry = Entry(
                amount = amount,
                description = description,
                date = date,
                type = type
            )
            repository.insert(entry)
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    fun deleteEntryById(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Helper to factory-construct the ViewModel manually in MainActivity
    class Factory(private val repository: EntryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
                return EntryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
