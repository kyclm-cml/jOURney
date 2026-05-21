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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SearchScreen(
    repository: JournalRepository,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val searchResults = remember(searchQuery) {
        repository.searchEntries(searchQuery)
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val displayFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmLinen)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Search Journal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = "Find reflections by searching keywords or passage references",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search words (e.g. grace, love, Matthew)", color = TextSecondary.copy(alpha = 0.6f)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = WarmSage) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
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

        Spacer(modifier = Modifier.height(12.dp))

        if (searchQuery.isBlank()) {
            // Idle State
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
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = WarmSage.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enter a search term",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Type any word from your Scripture, Observation, Application, or Prayer logs.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
            // No Results State
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
                    Text(
                        text = "No Reflections Found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We couldn't find any entries containing \"$searchQuery\". Try checking the spelling or searching another word.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Results list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = "FOUND ${searchResults.size} MATCHES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                items(searchResults) { entry ->
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

                            // Snippet helper function to find which field has the match
                            val matchSnippet = findMatchSnippet(entry, searchQuery)
                            
                            Text(
                                text = matchSnippet,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

// Find a snippet containing the query word across the fields of entry
private fun findMatchSnippet(entry: JournalEntry, query: String): String {
    val q = query.lowercase(Locale.US)
    
    if (entry.scriptureText.lowercase(Locale.US).contains(q)) {
        return "Scripture: ...${getSnippet(entry.scriptureText, q)}..."
    }
    if (entry.observation.lowercase(Locale.US).contains(q)) {
        return "Observation: ...${getSnippet(entry.observation, q)}..."
    }
    if (entry.application.lowercase(Locale.US).contains(q)) {
        return "Application: ...${getSnippet(entry.application, q)}..."
    }
    if (entry.prayer.lowercase(Locale.US).contains(q)) {
        return "Prayer: ...${getSnippet(entry.prayer, q)}..."
    }
    
    // Default fallback snippet
    return "Scripture: ${entry.scriptureText}"
}

private fun getSnippet(text: String, query: String): String {
    val index = text.lowercase(Locale.US).indexOf(query)
    if (index == -1) return text.take(50)
    
    val start = (index - 20).coerceAtLeast(0)
    val end = (index + query.length + 30).coerceIn(0, text.length)
    
    var snippet = text.substring(start, end)
    if (start > 0) snippet = "...$snippet"
    if (end < text.length) snippet = "$snippet..."
    return snippet
}
