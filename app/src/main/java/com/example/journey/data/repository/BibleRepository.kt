package com.example.journey.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class BibleRepository(private val context: Context) {

    private val dbName = "rsvce.db"
    private var db: SQLiteDatabase? = null

    init {
        try {
            copyDatabaseFromAssets()
            openDatabase()
            createAnnotationsAndHighlightsTables()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error initializing Bible database", e)
        }
    }

    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(dbName)
        if (!dbPath.exists()) {
            dbPath.parentFile?.mkdirs()
            context.assets.open(dbName).use { input ->
                FileOutputStream(dbPath).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("BibleRepository", "Database successfully copied from assets to ${dbPath.absolutePath}")
        } else {
            Log.d("BibleRepository", "Database already exists at ${dbPath.absolutePath}")
        }
    }

    private fun openDatabase() {
        val dbPath = context.getDatabasePath(dbName)
        db = SQLiteDatabase.openDatabase(dbPath.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
        Log.d("BibleRepository", "Database opened: ${db != null}")
    }

    private fun createAnnotationsAndHighlightsTables() {
        val database = db ?: return
        try {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS user_highlights (
                    book TEXT,
                    chapter INTEGER,
                    verse INTEGER,
                    color TEXT,
                    PRIMARY KEY (book, chapter, verse)
                );
            """)
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS user_annotations (
                    book TEXT,
                    chapter INTEGER,
                    verse INTEGER,
                    note TEXT,
                    updated_at INTEGER,
                    PRIMARY KEY (book, chapter, verse)
                );
            """)
            Log.d("BibleRepository", "User tables verified/created successfully.")
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error creating user tables", e)
        }
    }

    fun getBooks(): List<BibleBook> {
        val database = db ?: return emptyList()
        val list = mutableListOf<BibleBook>()
        try {
            val cursor = database.rawQuery("SELECT number, osis, human, chapters FROM books ORDER BY number ASC", null)
            if (cursor.moveToFirst()) {
                do {
                    val number = cursor.getInt(0)
                    val osis = cursor.getString(1)
                    val human = cursor.getString(2)
                    val chapters = cursor.getInt(3)
                    list.add(BibleBook(number, osis, human, chapters))
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error getting books", e)
        }
        return list
    }

    fun getVerses(bookOsis: String, chapter: Int): List<BibleVerse> {
        val database = db ?: return emptyList()
        val list = mutableListOf<BibleVerse>()
        try {
            // Lower and upper bounds for chapter number float
            val minVerse = chapter.toDouble()
            val maxVerse = (chapter + 1).toDouble()

            // Step 1: Query database verses for this chapter
            val cursor = database.rawQuery(
                "SELECT id, book, verse, unformatted FROM verses WHERE book = ? AND verse >= ? AND verse < ? ORDER BY verse ASC",
                arrayOf(bookOsis, minVerse.toString(), maxVerse.toString())
            )

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val book = cursor.getString(1)
                    val verseFloat = cursor.getDouble(2)
                    val text = cursor.getString(3)

                    // Extract verse number
                    val verseNum = Math.round((verseFloat - Math.floor(verseFloat)) * 1000).toInt()

                    list.add(
                        BibleVerse(
                            id = id,
                            book = book,
                            chapter = chapter,
                            verseNumber = verseNum,
                            text = text
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()

            // Step 2: Attach highlights
            val highlights = getHighlights(bookOsis, chapter)
            list.forEach { verse ->
                verse.highlightColor = highlights[verse.verseNumber]
            }

            // Step 3: Attach user notes
            val notes = getNotes(bookOsis, chapter)
            list.forEach { verse ->
                verse.userNote = notes[verse.verseNumber]
            }

        } catch (e: Exception) {
            Log.e("BibleRepository", "Error getting verses for $bookOsis $chapter", e)
        }
        return list
    }

    private fun getHighlights(bookOsis: String, chapter: Int): Map<Int, String> {
        val database = db ?: return emptyMap()
        val map = mutableMapOf<Int, String>()
        try {
            val cursor = database.rawQuery(
                "SELECT verse, color FROM user_highlights WHERE book = ? AND chapter = ?",
                arrayOf(bookOsis, chapter.toString())
            )
            if (cursor.moveToFirst()) {
                do {
                    val verse = cursor.getInt(0)
                    val color = cursor.getString(1)
                    map[verse] = color
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error querying highlights", e)
        }
        return map
    }

    private fun getNotes(bookOsis: String, chapter: Int): Map<Int, String> {
        val database = db ?: return emptyMap()
        val map = mutableMapOf<Int, String>()
        try {
            val cursor = database.rawQuery(
                "SELECT verse, note FROM user_annotations WHERE book = ? AND chapter = ?",
                arrayOf(bookOsis, chapter.toString())
            )
            if (cursor.moveToFirst()) {
                do {
                    val verse = cursor.getInt(0)
                    val note = cursor.getString(1)
                    map[verse] = note
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error querying notes", e)
        }
        return map
    }

    fun getAnnotations(bookOsis: String, chapter: Int): List<BibleFootnote> {
        val database = db ?: return emptyList()
        val list = mutableListOf<BibleFootnote>()
        try {
            val osisQuery = "$bookOsis.$chapter"
            val cursor = database.rawQuery(
                "SELECT id, osis, link, content FROM annotations WHERE osis = ? ORDER BY id ASC",
                arrayOf(osisQuery)
            )
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(0)
                    val osis = cursor.getString(1)
                    val link = cursor.getString(2)
                    val content = cursor.getString(3)
                    list.add(BibleFootnote(id, osis, link, content))
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error getting annotations", e)
        }
        return list
    }

    fun saveHighlight(book: String, chapter: Int, verse: Int, color: String?) {
        val database = db ?: return
        try {
            if (color.isNullOrEmpty()) {
                database.delete(
                    "user_highlights",
                    "book = ? AND chapter = ? AND verse = ?",
                    arrayOf(book, chapter.toString(), verse.toString())
                )
            } else {
                val cv = ContentValues().apply {
                    put("book", book)
                    put("chapter", chapter)
                    put("verse", verse)
                    put("color", color)
                }
                database.insertWithOnConflict("user_highlights", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error saving highlight for $book $chapter:$verse", e)
        }
    }

    fun saveNote(book: String, chapter: Int, verse: Int, note: String?) {
        val database = db ?: return
        try {
            if (note.isNullOrBlank()) {
                database.delete(
                    "user_annotations",
                    "book = ? AND chapter = ? AND verse = ?",
                    arrayOf(book, chapter.toString(), verse.toString())
                )
            } else {
                val cv = ContentValues().apply {
                    put("book", book)
                    put("chapter", chapter)
                    put("verse", verse)
                    put("note", note)
                    put("updated_at", System.currentTimeMillis())
                }
                database.insertWithOnConflict("user_annotations", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error saving note for $book $chapter:$verse", e)
        }
    }

    fun close() {
        try {
            db?.close()
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error closing database", e)
        }
    }
}

data class BibleBook(
    val number: Int,
    val osis: String,
    val human: String,
    val chapters: Int
)

data class BibleVerse(
    val id: Int,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String,
    var highlightColor: String? = null,
    var userNote: String? = null
)

data class BibleFootnote(
    val id: Int,
    val osis: String,
    val link: String,
    val content: String
)
