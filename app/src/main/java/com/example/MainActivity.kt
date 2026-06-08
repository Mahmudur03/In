package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.EntryRepository
import com.example.ui.EntryViewModel
import com.example.ui.MainDashboard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Retrieve database and abstract repository
        val database = AppDatabase.getDatabase(this)
        val repository = EntryRepository(database.entryDao())
        
        // Instantiate ViewModel using the repository factory
        val viewModel: EntryViewModel by viewModels {
            EntryViewModel.Factory(repository)
        }
        
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                MainDashboard(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
