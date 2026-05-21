package com.example.journey.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.model.JournalEntry
import com.example.journey.data.repository.BibleReadingPlan
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.LightSage
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    dateStr: String,
    repository: JournalRepository,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val entry = remember(dateStr) { repository.getEntryForDate(dateStr) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (entry == null) {
        // Fallback in case of invalid entry request
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(WarmLinen),
            contentAlignment = Alignment.Center
        ) {
            Text("Entry not found", color = TextPrimary)
        }
        return
    }

    val reading = BibleReadingPlan.getReadingForDay(entry.planDay)

    val emojisList = remember { listOf("😢", "😕", "😐", "🙂", "😄") }
    val (feelingEmoji, feelingText) = remember(entry.feeling) {
        val parts = entry.feeling.split("|", limit = 2)
        if (parts.size == 2) {
            Pair(parts[0], parts[1])
        } else if (parts.isNotEmpty() && parts[0] in emojisList) {
            Pair(parts[0], "")
        } else {
            Pair("", entry.feeling)
        }
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
    val formattedDate = try {
        val parsed = inputFormat.parse(entry.date) ?: Date()
        displayFormat.format(parsed)
    } catch (e: Exception) {
        entry.date
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Read Journal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ForestGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmLinen),
                actions = {
                    IconButton(onClick = { onNavigateToEditor(entry.date, entry.planDay) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = ForestGreen
                        )
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        containerColor = WarmLinen,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header information block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(LightSage, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DAY ${entry.planDay} OF 365",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entry.passage,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = reading.title,
                    fontSize = 16.sp,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedDate,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = WarmSage,
                    textAlign = TextAlign.Center
                )
            }

            // F - Feelings Detail Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderLinen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(WarmSage.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, WarmSage, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "F",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmSage
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Feelings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (feelingEmoji.isNotEmpty() || feelingText.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            if (feelingEmoji.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(WarmSage.copy(alpha = 0.1f), CircleShape)
                                        .border(1.dp, WarmSage.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = feelingEmoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Column {
                                val label = when (feelingEmoji) {
                                    "😢" -> "Sad"
                                    "😕" -> "Meh"
                                    "😐" -> "Okay"
                                    "🙂" -> "Good"
                                    "😄" -> "Joyful"
                                    else -> ""
                                }
                                if (label.isNotEmpty()) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmSage
                                    )
                                }
                                if (feelingText.isNotEmpty()) {
                                    Text(
                                        text = feelingText,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No feeling recorded for today.",
                            fontSize = 14.sp,
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            // D - Day Summary Detail
            DetailSoapCard(
                letter = 'D',
                title = "How my day went",
                content = entry.daySummary,
                accentColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // S - Scripture Detail
            DetailSoapCard(
                letter = 'S',
                title = "Scripture",
                content = entry.scriptureText,
                accentColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // O - Observation Detail
            DetailSoapCard(
                letter = 'O',
                title = "Observation",
                content = entry.observation,
                accentColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // A - Application Detail
            DetailSoapCard(
                letter = 'A',
                title = "Application",
                content = entry.application,
                accentColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // P - Prayer Detail
            DetailSoapCard(
                letter = 'P',
                title = "Prayer",
                content = entry.prayer,
                accentColor = WarmSage
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Confirm Delete Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Reflection", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this SOAP reflection entry? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deleteEntry(entry.date)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = WarmLinen
        )
    }
}

@Composable
fun DetailSoapCard(
    letter: Char,
    title: String,
    content: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderLinen),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, accentColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (content.isNotBlank()) content else "No reflection recorded for this section.",
                fontSize = 14.sp,
                color = if (content.isNotBlank()) TextPrimary else TextSecondary.copy(alpha = 0.5f),
                lineHeight = 20.sp,
                fontStyle = if (content.isNotBlank()) FontStyle.Normal else FontStyle.Italic
            )
        }
    }
}
