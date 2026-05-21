package com.example.journey.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.model.BibleReading
import com.example.journey.data.repository.BibleReadingPlan
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.theme.AccomplishedGreen
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.LightSage
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    repository: JournalRepository,
    onNavigateToEditor: (String, Int) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Gospels", "History", "Epistles", "Prophecy", "Psalms")
    
    // Filter readings based on search and category tab
    val filteredReadings = remember(searchQuery, selectedCategory) {
        BibleReadingPlan.readings.filter { reading ->
            val matchesCategory = selectedCategory == "All" || reading.category == selectedCategory
            val matchesSearch = reading.passage.lowercase(Locale.US).contains(searchQuery.lowercase(Locale.US)) ||
                    reading.title.lowercase(Locale.US).contains(searchQuery.lowercase(Locale.US))
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmLinen)
    ) {
        // Title Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Bible Reading Plan",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = "365-day plan through the New Testament & Psalms",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Search Bar inside plan
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search passages (e.g. Matthew 5)", color = TextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = WarmSage) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = BorderLinen,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
            indicator = {},
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            categories.forEach { category ->
                val selected = selectedCategory == category
                Tab(
                    selected = selected,
                    onClick = { selectedCategory = category },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (selected) ForestGreen else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) ForestGreen else BorderLinen,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (selected) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }
        }

        // Readings list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(filteredReadings) { reading ->
                val targetDateStr = repository.getDateForPlanDay(reading.dayNumber)
                val entry = repository.getEntryForDate(targetDateStr)
                val isCompleted = entry != null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            if (isCompleted) {
                                onNavigateToDetail(targetDateStr)
                            } else {
                                onNavigateToEditor(targetDateStr, reading.dayNumber)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderLinen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Check Circle Indicator
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) AccomplishedGreen else Color.Transparent)
                                .border(
                                    width = if (isCompleted) 0.dp else 1.5.dp,
                                    color = if (isCompleted) Color.Transparent else WarmSage,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = reading.dayNumber.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Text content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = reading.passage,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Day ${reading.dayNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmSage
                                )
                            }
                            Text(
                                text = reading.title,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontStyle = FontStyle.Italic
                            )
                            if (isCompleted && entry != null) {
                                Text(
                                    text = "Completed on ${entry.date}",
                                    fontSize = 11.sp,
                                    color = AccomplishedGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
