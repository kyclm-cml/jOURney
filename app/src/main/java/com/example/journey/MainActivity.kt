package com.example.journey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.journey.data.repository.BibleRepository
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.screens.MainScreen
import com.example.journey.ui.theme.JOURneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this
            val repository = remember { JournalRepository(context) }
            val bibleRepository = remember { BibleRepository(context) }
            
            JOURneyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        repository = repository,
                        bibleRepository = bibleRepository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}