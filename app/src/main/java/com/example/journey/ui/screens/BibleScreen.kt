package com.example.journey.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.journey.data.repository.BibleBook
import com.example.journey.data.repository.BibleFootnote
import com.example.journey.data.repository.BibleRepository
import com.example.journey.data.repository.BibleVerse
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.CardLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.LightSage
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    bibleRepository: BibleRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val biblePrefs = remember { context.getSharedPreferences("bible_prefs", Context.MODE_PRIVATE) }

    // Load last read book & chapter or defaults
    var selectedBookOsis by remember { mutableStateOf(biblePrefs.getString("last_read_book_osis", "Gen") ?: "Gen") }
    var selectedBookHuman by remember { mutableStateOf(biblePrefs.getString("last_read_book_human", "Genesis") ?: "Genesis") }
    var selectedBookChapters by remember { mutableStateOf(biblePrefs.getInt("last_read_book_chapters", 50)) }
    var selectedChapter by remember { mutableStateOf(biblePrefs.getInt("last_read_chapter", 1)) }

    // Books list
    var books by remember { mutableStateOf<List<BibleBook>>(emptyList()) }
    // Verses list
    var verses by remember { mutableStateOf<List<BibleVerse>>(emptyList()) }
    // Footnotes list for current chapter
    var footnotes by remember { mutableStateOf<List<BibleFootnote>>(emptyList()) }

    // Dialog flags
    var showBookDialog by remember { mutableStateOf(false) }
    var showChapterDialog by remember { mutableStateOf(false) }

    // Bottom sheet state for clicked verse details
    var selectedVerseForSheet by remember { mutableStateOf<BibleVerse?>(null) }
    var noteTextState by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Load initial data
    LaunchedEffect(Unit) {
        books = bibleRepository.getBooks()
    }

    // Reload verses when book or chapter changes
    LaunchedEffect(selectedBookOsis, selectedChapter) {
        verses = bibleRepository.getVerses(selectedBookOsis, selectedChapter)
        footnotes = bibleRepository.getAnnotations(selectedBookOsis, selectedChapter)
        listState.scrollToItem(0)

        // Save last read info
        biblePrefs.edit().apply {
            putString("last_read_book_osis", selectedBookOsis)
            putString("last_read_book_human", selectedBookHuman)
            putInt("last_read_book_chapters", selectedBookChapters)
            putInt("last_read_chapter", selectedChapter)
            apply()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showBookDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Bible",
                            tint = WarmSage,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$selectedBookHuman $selectedChapter",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmSage
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmLinen),
                actions = {
                    // Previous chapter button
                    IconButton(
                        onClick = {
                            if (selectedChapter > 1) {
                                selectedChapter--
                            } else {
                                // Go to previous book's last chapter if available
                                val currentBookIndex = books.indexOfFirst { it.osis == selectedBookOsis }
                                if (currentBookIndex > 0) {
                                    val prevBook = books[currentBookIndex - 1]
                                    selectedBookOsis = prevBook.osis
                                    selectedBookHuman = prevBook.human
                                    selectedBookChapters = prevBook.chapters
                                    selectedChapter = prevBook.chapters
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Chapter", tint = TextPrimary)
                    }

                    // Chapter picker shortcut button
                    TextButton(onClick = { showChapterDialog = true }) {
                        Text(
                            text = "CH",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            modifier = Modifier
                                .background(LightSage, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Next chapter button
                    IconButton(
                        onClick = {
                            if (selectedChapter < selectedBookChapters) {
                                selectedChapter++
                            } else {
                                // Go to next book's chapter 1 if available
                                val currentBookIndex = books.indexOfFirst { it.osis == selectedBookOsis }
                                if (currentBookIndex >= 0 && currentBookIndex < books.size - 1) {
                                    val nextBook = books[currentBookIndex + 1]
                                    selectedBookOsis = nextBook.osis
                                    selectedBookHuman = nextBook.human
                                    selectedBookChapters = nextBook.chapters
                                    selectedChapter = 1
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Chapter", tint = TextPrimary)
                    }
                }
            )
        },
        containerColor = WarmLinen,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (verses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WarmSage)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
                ) {
                    items(verses) { verse ->
                        val textParts = remember(verse.text) { verse.text.split("\n", limit = 2) }
                        val heading = if (textParts.size > 1) textParts[0] else null
                        val textContent = if (textParts.size > 1) textParts[1] else textParts[0]

                        // Semi-transparent colors for highlighted background
                        val highlightBgColor = when (verse.highlightColor) {
                            "yellow" -> Color(0x33FBBF24)
                            "green" -> Color(0x3334D399)
                            "blue" -> Color(0x3360A5FA)
                            "pink" -> Color(0x33EC4899)
                            else -> Color.Transparent
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    selectedVerseForSheet = verse
                                    noteTextState = verse.userNote ?: ""
                                }
                        ) {
                            if (heading != null) {
                                Text(
                                    text = heading,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = WarmSage,
                                    modifier = Modifier.padding(bottom = 6.dp, top = 8.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(highlightBgColor, RoundedCornerShape(4.dp))
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${verse.verseNumber} ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = textContent,
                                        fontSize = 15.sp,
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                }
                                if (!verse.userNote.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Notes,
                                        contentDescription = "Has Annotation",
                                        tint = WarmSage,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Book Selector Dialog
    if (showBookDialog) {
        Dialog(onDismissRequest = { showBookDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLinen),
                colors = CardDefaults.cardColors(containerColor = CardLinen)
            ) {
                var selectedTab by remember { mutableStateOf(0) } // 0 = OT, 1 = NT

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose Book",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = WarmSage,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = WarmSage
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Old Testament", color = if (selectedTab == 0) WarmSage else TextSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("New Testament", color = if (selectedTab == 1) WarmSage else TextSecondary) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredBooks = if (selectedTab == 0) {
                        books.filter { it.number < 47 }
                    } else {
                        books.filter { it.number >= 47 }
                    }

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filteredBooks) { book ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedBookOsis = book.osis
                                        selectedBookHuman = book.human
                                        selectedBookChapters = book.chapters
                                        selectedChapter = 1
                                        showBookDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = book.human,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedBookOsis == book.osis) WarmSage else TextPrimary
                                )
                            }
                            HorizontalDivider(color = BorderLinen)
                        }
                    }
                }
            }
        }
    }

    // Chapter Selector Dialog
    if (showChapterDialog) {
        Dialog(onDismissRequest = { showChapterDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLinen),
                colors = CardDefaults.cardColors(containerColor = CardLinen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose Chapter: $selectedBookHuman",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        // Display chapters in grids of 5 items per row
                        val itemsPerRow = 5
                        val rowsCount = (selectedBookChapters + itemsPerRow - 1) / itemsPerRow
                        items((0 until rowsCount).toList()) { rowIndex ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (colIndex in 0 until itemsPerRow) {
                                    val chNum = rowIndex * itemsPerRow + colIndex + 1
                                    if (chNum <= selectedBookChapters) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .padding(4.dp)
                                                .background(
                                                    if (selectedChapter == chNum) WarmSage else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (selectedChapter == chNum) WarmSage else BorderLinen,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedChapter = chNum
                                                    showChapterDialog = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = chNum.toString(),
                                                color = if (selectedChapter == chNum) Color.White else TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(44.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Verse Annotation & Footnotes Bottom Sheet
    if (selectedVerseForSheet != null) {
        val verse = selectedVerseForSheet!!
        ModalBottomSheet(
            onDismissRequest = { selectedVerseForSheet = null },
            sheetState = sheetState,
            containerColor = CardLinen,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextSecondary.copy(alpha = 0.5f)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp)
            ) {
                // Verse Label
                Text(
                    text = "$selectedBookHuman ${verse.chapter}:${verse.verseNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmSage
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Verse Preview
                Text(
                    text = verse.text.split("\n").last(),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderLinen)
                Spacer(modifier = Modifier.height(16.dp))

                // Highlight Color Row
                Text(
                    text = "Highlight Verse",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        Triple("none", Color.Transparent, "None"),
                        Triple("yellow", Color(0xFFFBBF24), "Yellow"),
                        Triple("green", Color(0xFF34D399), "Green"),
                        Triple("blue", Color(0xFF60A5FA), "Blue"),
                        Triple("pink", Color(0xFFEC4899), "Pink")
                    )

                    colors.forEach { (colorName, colorVal, label) ->
                        val isColorSelected = (verse.highlightColor ?: "none") == colorName
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                val savedColor = if (colorName == "none") null else colorName
                                bibleRepository.saveHighlight(verse.book, verse.chapter, verse.verseNumber, savedColor)
                                verse.highlightColor = savedColor
                                // Re-query verses to refresh UI state
                                verses = bibleRepository.getVerses(selectedBookOsis, selectedChapter)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(colorVal, CircleShape)
                                    .border(
                                        width = if (isColorSelected) 3.dp else 1.dp,
                                        color = if (isColorSelected) TextPrimary else BorderLinen,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (colorName == "none") {
                                    Text("✕", color = TextSecondary, fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isColorSelected) WarmSage else TextSecondary,
                                fontWeight = if (isColorSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = BorderLinen)
                Spacer(modifier = Modifier.height(16.dp))

                // User Annotation (Personal Note) Section
                Text(
                    text = "Personal Note",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteTextState,
                    onValueChange = { noteTextState = it },
                    placeholder = { Text("Write personal reflections, prayer, or study notes...", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmSage,
                        unfocusedBorderColor = BorderLinen,
                        focusedContainerColor = WarmLinen.copy(alpha = 0.3f),
                        unfocusedContainerColor = WarmLinen.copy(alpha = 0.3f)
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val savedNote = if (noteTextState.isBlank()) null else noteTextState
                        bibleRepository.saveNote(verse.book, verse.chapter, verse.verseNumber, savedNote)
                        verse.userNote = savedNote
                        // Re-query verses to refresh UI state
                        verses = bibleRepository.getVerses(selectedBookOsis, selectedChapter)
                        // Close sheet
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                selectedVerseForSheet = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmSage),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Note", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Prepackaged Catholic Footnotes/Annotations Section
                // We match annotations by verse number
                val matchedFootnotes = remember(footnotes, verse.verseNumber) {
                    footnotes.filter { fn ->
                        // Extract verse number from link fen-RSVCE-Xy
                        val parts = fn.link.split("-")
                        val lastPart = parts.lastOrNull() ?: ""
                        val digitString = lastPart.takeWhile { it.isDigit() }
                        val vNum = digitString.toIntOrNull() ?: 0
                        vNum == verse.verseNumber
                    }
                }

                if (matchedFootnotes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderLinen)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Catholic Translation & Study Notes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                    ) {
                        items(matchedFootnotes) { fn ->
                            val cleanContent = remember(fn.content) {
                                // Strip HTML tags
                                fn.content.replace(Regex("<[^>]*>"), "").trim()
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = WarmLinen.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, BorderLinen)
                            ) {
                                Text(
                                    text = cleanContent,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
