package com.example.journey.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    dateStr: String,
    planDay: Int,
    repository: JournalRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val reading = BibleReadingPlan.getReadingForDay(planDay)
    
    // Check if there is an existing entry for this date to pre-populate fields
    val existingEntry = remember(dateStr) { repository.getEntryForDate(dateStr) }

    val emojisList = listOf("😢", "😕", "😐", "🙂", "😄")
    val existingFeeling = existingEntry?.feeling ?: ""
    val (initialEmoji, initialText) = remember(existingFeeling) {
        val parts = existingFeeling.split("|", limit = 2)
        if (parts.size == 2) {
            Pair(parts[0], parts[1])
        } else if (parts.isNotEmpty() && parts[0] in emojisList) {
            Pair(parts[0], "")
        } else {
            Pair("", existingFeeling)
        }
    }
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    var feelingText by remember { mutableStateOf(initialText) }
    var daySummary by remember { mutableStateOf(existingEntry?.daySummary ?: "") }

    var scriptureText by remember { mutableStateOf(existingEntry?.scriptureText ?: "") }
    var observation by remember { mutableStateOf(existingEntry?.observation ?: "") }
    var application by remember { mutableStateOf(existingEntry?.application ?: "") }
    var prayer by remember { mutableStateOf(existingEntry?.prayer ?: "") }

    var showConfirmBackDialog by remember { mutableStateOf(false) }

    val currentFeeling = remember(selectedEmoji, feelingText) {
        if (selectedEmoji.isNotEmpty() || feelingText.isNotEmpty()) "$selectedEmoji|$feelingText" else ""
    }

    val hasChanges = remember(selectedEmoji, feelingText, daySummary, scriptureText, observation, application, prayer) {
        currentFeeling != (existingEntry?.feeling ?: "") ||
        daySummary != (existingEntry?.daySummary ?: "") ||
        scriptureText != (existingEntry?.scriptureText ?: "") ||
        observation != (existingEntry?.observation ?: "") ||
        application != (existingEntry?.application ?: "") ||
        prayer != (existingEntry?.prayer ?: "")
    }

    val handleBackPress = {
        if (hasChanges) {
            showConfirmBackDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Capture system back button press
    BackHandler(onBack = handleBackPress)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SOAP Reflection",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = handleBackPress) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ForestGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmLinen),
                actions = {
                    Button(
                        onClick = {
                            val newEntry = JournalEntry(
                                id = existingEntry?.id ?: java.util.UUID.randomUUID().toString(),
                                date = dateStr,
                                planDay = planDay,
                                passage = reading.passage,
                                feeling = currentFeeling,
                                daySummary = daySummary,
                                scriptureText = scriptureText,
                                observation = observation,
                                application = application,
                                prayer = prayer
                            )
                            repository.saveEntry(newEntry)
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Verse context header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderLinen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reading.passage,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Day $planDay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier
                                .background(LightSage, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = reading.title,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reading.versesPreview,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }

            // F - Feelings Card (Emotion Tracker)
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
                                .size(36.dp)
                                .background(WarmSage.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, WarmSage, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "F",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmSage
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Feelings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "How I felt today",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val emojis = listOf(
                            "😢" to "Sad",
                            "😕" to "Meh",
                            "😐" to "Okay",
                            "🙂" to "Good",
                            "😄" to "Joyful"
                        )
                        emojis.forEach { (emoji, label) ->
                            val isSelected = selectedEmoji == emoji
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        selectedEmoji = if (isSelected) "" else emoji
                                    }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            if (isSelected) WarmSage.copy(alpha = 0.2f) else Color.Transparent,
                                            androidx.compose.foundation.shape.CircleShape
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) WarmSage else BorderLinen,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) WarmSage else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = feelingText,
                        onValueChange = { feelingText = it },
                        placeholder = { Text("Describe how you felt... (optional)", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmSage,
                            unfocusedBorderColor = BorderLinen,
                            focusedContainerColor = WarmLinen.copy(alpha = 0.5f),
                            unfocusedContainerColor = WarmLinen.copy(alpha = 0.5f)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                    )
                }
            }

            // D - Day Summary Card
            SoapInputField(
                letter = 'D',
                title = "How my day went",
                subtitle = "Briefly summarize the events and highlights of your day.",
                placeholder = "What happened today? Write a brief summary...",
                value = daySummary,
                onValueChange = { daySummary = it },
                borderColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // S - Scripture Card
            SoapInputField(
                letter = 'S',
                title = "Scripture",
                subtitle = "Write down the specific verse(s) that stand out to you.",
                placeholder = "Write the scripture reference and the text...",
                value = scriptureText,
                onValueChange = { scriptureText = it },
                borderColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // O - Observation Card
            SoapInputField(
                letter = 'O',
                title = "Observation",
                subtitle = "What do you notice? What is the context? What is God saying?",
                placeholder = "What stands out? Who is speaking? What lessons do you learn here?",
                value = observation,
                onValueChange = { observation = it },
                borderColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // A - Application Card
            SoapInputField(
                letter = 'A',
                title = "Application",
                subtitle = "How does this apply to your life today? What will you do?",
                placeholder = "How will your heart, actions, or words change today based on this?",
                value = application,
                onValueChange = { application = it },
                borderColor = WarmSage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // P - Prayer Card
            SoapInputField(
                letter = 'P',
                title = "Prayer",
                subtitle = "Write a prayer asking God to help you apply this.",
                placeholder = "Pray for strength, wisdom, and write a heart-to-heart message to God...",
                value = prayer,
                onValueChange = { prayer = it },
                borderColor = WarmSage
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Confirm Discard Changes Dialog
    if (showConfirmBackDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmBackDialog = false },
            title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold) },
            text = { Text("You have written reflections that are not saved. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmBackDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Discard", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmBackDialog = false }
                ) {
                    Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = WarmLinen
        )
    }
}

@Composable
fun SoapInputField(
    letter: Char,
    title: String,
    subtitle: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    borderColor: Color
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
                        .size(36.dp)
                        .background(borderColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = borderColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = BorderLinen,
                    focusedContainerColor = WarmLinen.copy(alpha = 0.5f),
                    unfocusedContainerColor = WarmLinen.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
            )
        }
    }
}
