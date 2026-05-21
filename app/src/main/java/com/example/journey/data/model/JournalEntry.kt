package com.example.journey.data.model

import java.util.UUID

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val planDay: Int, // The reading plan day (1 to 365)
    val passage: String, // e.g. "Matthew 1"
    val feeling: String = "",
    val daySummary: String = "",
    val scriptureText: String, // "S" - Scripture
    val observation: String,   // "O" - Observation
    val application: String,   // "A" - Application
    val prayer: String,        // "P" - Prayer
    val createdAt: Long = System.currentTimeMillis()
)
