package com.example.journey.data.repository

import com.example.journey.data.model.BibleReading

object BibleReadingPlan {
    val readings: List<BibleReading> by lazy {
        val list = mutableListOf<BibleReading>()
        var day = 1

        // 1. New Testament Books (260 Chapters)
        val ntBooks = listOf(
            NtBook("Matthew", 28, "Gospels"),
            NtBook("Mark", 16, "Gospels"),
            NtBook("Luke", 24, "Gospels"),
            NtBook("John", 21, "Gospels"),
            NtBook("Acts", 28, "History"),
            NtBook("Romans", 16, "Epistles"),
            NtBook("1 Corinthians", 16, "Epistles"),
            NtBook("2 Corinthians", 13, "Epistles"),
            NtBook("Galatians", 6, "Epistles"),
            NtBook("Ephesians", 6, "Epistles"),
            NtBook("Philippians", 4, "Epistles"),
            NtBook("Colossians", 4, "Epistles"),
            NtBook("1 Thessalonians", 5, "Epistles"),
            NtBook("2 Thessalonians", 3, "Epistles"),
            NtBook("1 Timothy", 6, "Epistles"),
            NtBook("2 Timothy", 4, "Epistles"),
            NtBook("Titus", 3, "Epistles"),
            NtBook("Philemon", 1, "Epistles"),
            NtBook("Hebrews", 13, "Epistles"),
            NtBook("James", 5, "Epistles"),
            NtBook("1 Peter", 5, "Epistles"),
            NtBook("2 Peter", 3, "Epistles"),
            NtBook("1 John", 5, "Epistles"),
            NtBook("2 John", 1, "Epistles"),
            NtBook("3 John", 1, "Epistles"),
            NtBook("Jude", 1, "Epistles"),
            NtBook("Revelation", 22, "Prophecy")
        )

        // Pre-defined key verses for major chapters to make it look premium
        val customPassages = mapOf(
            "Matthew 1" to Pair("The Family Tree of Jesus", "She will give birth to a son, and you are to give him the name Jesus, because he will save his people from their sins. (v. 21)"),
            "Matthew 2" to Pair("The Visit of the Magi", "Where is the one who has been born king of the Jews? We saw his star when it rose and have come to worship him. (v. 2)"),
            "Matthew 3" to Pair("John the Baptist & Jesus' Baptism", "And a voice from heaven said, 'This is my Son, whom I love; with him I am well pleased.' (v. 17)"),
            "Matthew 4" to Pair("Temptation of Jesus & Calling Disciples", "Jesus answered, 'It is written: Man shall not live on bread alone, but on every word that comes from the mouth of God.' (v. 4)"),
            "Matthew 5" to Pair("The Beatitudes / Sermon on the Mount", "In the same way, let your light shine before others, that they may see your good deeds and glorify your Father in heaven. (v. 16)"),
            "Matthew 6" to Pair("The Lord's Prayer & Worry", "But seek first his kingdom and his righteousness, and all these things will be given to you as well. (v. 33)"),
            "Matthew 7" to Pair("Ask, Seek, Knock & Building on Rock", "Ask and it will be given to you; seek and you will find; knock and the door will be opened to you. (v. 7)"),
            "John 1" to Pair("The Word Became Flesh", "In the beginning was the Word, and the Word was with God, and the Word was God. (v. 1)"),
            "John 3" to Pair("Nicodemus & Born Again", "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life. (v. 16)"),
            "John 14" to Pair("The Way, the Truth, the Life", "Jesus answered, 'I am the way and the truth and the life. No one comes to the Father except through me.' (v. 6)"),
            "Romans 8" to Pair("Life in the Spirit", "And we know that in all things God works for the good of those who love him, who have been called according to his purpose. (v. 28)"),
            "Romans 12" to Pair("Living Sacrifices", "Do not conform to the pattern of this world, but be transformed by the renewing of your mind. (v. 2)"),
            "1 Corinthians 13" to Pair("The Love Chapter", "Love is patient, love is kind. It does not envy, it does not boast, it is not proud. (v. 4)"),
            "Galatians 5" to Pair("Fruit of the Spirit", "But the fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness, gentleness and self-control. (v. 22-23)"),
            "Ephesians 6" to Pair("The Armor of God", "Therefore put on the full armor of God, so that when the day of evil comes, you may be able to stand your ground. (v. 13)"),
            "Philippians 4" to Pair("Rejoice & Peace", "I can do all this through him who gives me strength. (v. 13)"),
            "Hebrews 11" to Pair("The Hall of Faith", "Now faith is confidence in what we hope for and assurance about what we do not see. (v. 1)"),
            "James 1" to Pair("Faith & Wisdom", "If any of you lacks wisdom, you should ask God, who gives generously to all without finding fault. (v. 5)"),
            "1 John 4" to Pair("God is Love", "Dear friends, let us love one another, for love comes from God. Everyone who loves has been born of God and knows God. (v. 7)"),
            "Revelation 21" to Pair("New Heaven & New Earth", "He will wipe every tear from their eyes. There will be no more death or mourning or crying or pain. (v. 4)")
        )

        for (book in ntBooks) {
            for (ch in 1..book.chapters) {
                val ref = "${book.name} $ch"
                val custom = customPassages[ref]
                val title = custom?.first ?: "Walking through ${book.name}"
                val preview = custom?.second ?: "Reflect on God's word and write down the scripture that speaks to you today in Chapter $ch."
                
                list.add(
                    BibleReading(
                        dayNumber = day++,
                        passage = ref,
                        title = title,
                        versesPreview = preview,
                        category = book.category
                    )
                )
            }
        }

        // 2. Psalms (105 Chapters to make 365 Days)
        val psalmPassages = mapOf(
            1 to Pair("The Two Paths", "Blessed is the one who does not walk in step with the wicked... but whose delight is in the law of the Lord. (v. 1-2)"),
            19 to Pair("The Glory of Creation", "The heavens declare the glory of God; the skies proclaim the work of his hands. (v. 1)"),
            23 to Pair("The Lord is My Shepherd", "The Lord is my shepherd, I lack nothing. He makes me lie down in green pastures. (v. 1-2)"),
            27 to Pair("Light and Salvation", "The Lord is my light and my salvation—whom shall I fear? (v. 1)"),
            46 to Pair("God is Our Fortress", "God is our refuge and strength, an ever-present help in trouble. (v. 1)"),
            51 to Pair("Create in Me a Clean Heart", "Create in me a pure heart, O God, and renew a steadfast spirit within me. (v. 10)"),
            91 to Pair("Dwelling in the Secret Place", "Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty. (v. 1)"),
            100 to Pair("Make a Joyful Noise", "Enter his gates with thanksgiving and his courts with praise; give thanks to him and praise his name. (v. 4)"),
            103 to Pair("Praise the Lord, My Soul", "Praise the Lord, my soul; all my inmost being, praise his holy name. (v. 1)")
        )

        for (ch in 1..105) {
            val ref = "Psalm $ch"
            val custom = psalmPassages[ch]
            val title = custom?.first ?: "The Worship of Israel"
            val preview = custom?.second ?: "Sing praises and reflect on the heart of the Psalmist in Chapter $ch."
            
            list.add(
                BibleReading(
                    dayNumber = day++,
                    passage = ref,
                    title = title,
                    versesPreview = preview,
                    category = "Psalms"
                )
            )
        }

        list
    }

    fun getReadingForDay(dayNumber: Int): BibleReading {
        val index = (dayNumber - 1).coerceIn(0, readings.size - 1)
        return readings[index]
    }
}

private data class NtBook(val name: String, val chapters: Int, val category: String)
