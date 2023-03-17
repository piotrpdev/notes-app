package models

import java.time.LocalDateTime

data class Note(
    var noteTitle: String,
    var notePriority: Int,
    var noteCategory: String,
    var isNoteArchived: Boolean,
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var createdAt: LocalDateTime = LocalDateTime.now()
)