package com.example.journey.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.repository.BibleRepository
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen

sealed interface NavigationDestination {
    object HomeTab : NavigationDestination
    object PlanTab : NavigationDestination
    object BibleTab : NavigationDestination
    object CompilationTab : NavigationDestination
    object SearchTab : NavigationDestination
    data class Editor(val dateStr: String, val planDay: Int) : NavigationDestination
    data class Detail(val dateStr: String) : NavigationDestination
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: JournalRepository,
    bibleRepository: BibleRepository,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf<NavigationDestination>(NavigationDestination.HomeTab) }
    var activeTab by remember { mutableStateOf<NavigationDestination>(NavigationDestination.HomeTab) }

    // Intercept back presses for nested navigation
    BackHandler(enabled = currentDestination != NavigationDestination.HomeTab) {
        when (currentDestination) {
            is NavigationDestination.Editor -> {
                // The Editor has its own confirmation dialog, so we handle it inside EditorScreen
            }
            is NavigationDestination.Detail -> {
                currentDestination = activeTab
            }
            NavigationDestination.PlanTab,
            NavigationDestination.BibleTab,
            NavigationDestination.CompilationTab,
            NavigationDestination.SearchTab -> {
                currentDestination = NavigationDestination.HomeTab
                activeTab = NavigationDestination.HomeTab
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = {
            // Show bottom navigation bar only when on a tab destination
            if (currentDestination == NavigationDestination.HomeTab ||
                currentDestination == NavigationDestination.PlanTab ||
                currentDestination == NavigationDestination.BibleTab ||
                currentDestination == NavigationDestination.CompilationTab ||
                currentDestination == NavigationDestination.SearchTab
            ) {
                NavigationBar(
                    containerColor = WarmLinen,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        TabItem("Calendar", Icons.Default.DateRange, NavigationDestination.HomeTab),
                        TabItem("Reading Plan", Icons.Default.Book, NavigationDestination.PlanTab),
                        TabItem("Bible", Icons.Default.MenuBook, NavigationDestination.BibleTab),
                        TabItem("Notebook", Icons.Default.Bookmark, NavigationDestination.CompilationTab),
                        TabItem("Search", Icons.Default.Search, NavigationDestination.SearchTab)
                    )

                    tabs.forEach { tab ->
                        val isSelected = currentDestination == tab.destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentDestination = tab.destination
                                activeTab = tab.destination
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = if (isSelected) ForestGreen else TextSecondary.copy(alpha = 0.7f)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) ForestGreen else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = ForestGreen.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = WarmLinen,
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = WarmLinen
        ) {
            when (val dest = currentDestination) {
                NavigationDestination.HomeTab -> {
                    HomeScreen(
                        repository = repository,
                        onNavigateToEditor = { dateStr, planDay ->
                            currentDestination = NavigationDestination.Editor(dateStr, planDay)
                        },
                        onNavigateToDetail = { dateStr ->
                            currentDestination = NavigationDestination.Detail(dateStr)
                        }
                    )
                }
                NavigationDestination.PlanTab -> {
                    PlanScreen(
                        repository = repository,
                        onNavigateToEditor = { dateStr, planDay ->
                            currentDestination = NavigationDestination.Editor(dateStr, planDay)
                        },
                        onNavigateToDetail = { dateStr ->
                            currentDestination = NavigationDestination.Detail(dateStr)
                        }
                    )
                }
                NavigationDestination.BibleTab -> {
                    BibleScreen(
                        bibleRepository = bibleRepository,
                        modifier = Modifier
                    )
                }
                NavigationDestination.CompilationTab -> {
                    CompilationScreen(
                        repository = repository,
                        onNavigateToDetail = { dateStr ->
                            currentDestination = NavigationDestination.Detail(dateStr)
                        }
                    )
                }
                NavigationDestination.SearchTab -> {
                    SearchScreen(
                        repository = repository,
                        onNavigateToDetail = { dateStr ->
                            currentDestination = NavigationDestination.Detail(dateStr)
                        }
                    )
                }
                is NavigationDestination.Editor -> {
                    EditorScreen(
                        dateStr = dest.dateStr,
                        planDay = dest.planDay,
                        repository = repository,
                        onNavigateBack = {
                            currentDestination = activeTab
                        }
                    )
                }
                is NavigationDestination.Detail -> {
                    DetailScreen(
                        dateStr = dest.dateStr,
                        repository = repository,
                        onNavigateBack = {
                            currentDestination = activeTab
                        },
                        onNavigateToEditor = { dateStr, planDay ->
                            currentDestination = NavigationDestination.Editor(dateStr, planDay)
                        }
                    )
                }
            }
        }
    }
}

private data class TabItem(
    val label: String,
    val icon: ImageVector,
    val destination: NavigationDestination
)
