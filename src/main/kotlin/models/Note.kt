package models

import java.time.LocalDateTime

/**
 * A data class representing a note with a title, priority, category, and archive status.
 *
 * @property noteTitle The title of the note.
 * @property notePriority The priority of the note, where 1 is the lowest and 5 is the highest priority (inclusive).
 * @property noteCategory The category of the note.
 * @property isNoteArchived A flag indicating whether the note is archived or not.
 * @property updatedAt The date and time the note was last updated, defaulting to the current date and time.
 * @property createdAt The date and time the note was created, defaulting to the current date and time.
 */
data class Note(
    var noteTitle: String,
    var notePriority: Int,
    var noteCategory: String,
    var isNoteArchived: Boolean,
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var createdAt: LocalDateTime = LocalDateTime.now()
)