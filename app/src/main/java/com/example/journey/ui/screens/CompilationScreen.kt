package com.example.journey.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.model.JournalEntry
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.CardLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.LightSage
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CompilationScreen(
    repository: JournalRepository,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = repository.getEntries()
    
    // Date formatter for display (e.g. "Monday, May 19")
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.US)
    val groupFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    // Group entries by Month/Year
    val groupedEntries = remember(entries) {
        entries.groupBy { entry ->
            try {
                val date = inputFormat.parse(entry.date) ?: Date()
                groupFormat.format(date)
            } catch (e: Exception) {
                "Unknown Month"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmLinen)
    ) {
        // Folder Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Journal Folder",
                    tint = ForestGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Reflections Journal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "Your compilation of standard SOAP journal entries",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (entries.isEmpty()) {
            // Empty state placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "No Entries",
                        tint = WarmSage.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Journal is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Read the daily passage and start writing reflections following the SOAP method. Completed days will compile here.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // List of group headers and items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                groupedEntries.forEach { (monthYear, monthEntries) ->
                    // Month Section Header
                    item {
                        Text(
                            text = monthYear.uppercase(Locale.US),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                .padding(top = 8.dp)
                        )
                    }

                    items(monthEntries) { entry ->
                        val formattedDate = try {
                            val parsed = inputFormat.parse(entry.date) ?: Date()
                            displayFormat.format(parsed)
                        } catch (e: Exception) {
                            entry.date
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onNavigateToDetail(entry.date) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, BorderLinen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formattedDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmSage
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(LightSage, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Day ${entry.planDay}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = entry.passage,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // S.O.A.P. mini preview
                                Text(
                                    text = "S: ${entry.scriptureText}",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "O: ${entry.observation}",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Visual indicators for SOAP completion
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SoapIndicator(label = "S", isPresent = entry.scriptureText.isNotBlank())
                                    SoapIndicator(label = "O", isPresent = entry.observation.isNotBlank())
                                    SoapIndicator(label = "A", isPresent = entry.application.isNotBlank())
                                    SoapIndicator(label = "P", isPresent = entry.prayer.isNotBlank())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoapIndicator(label: String, isPresent: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isPresent) ForestGreen else BorderLinen)
            .border(
                width = if (isPresent) 0.dp else 1.dp,
                color = BorderLinen,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPresent) Color.White else TextSecondary.copy(alpha = 0.5f)
        )
    }
}
