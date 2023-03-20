package controllers

import com.jakewharton.picnic.Table
import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import models.Note
import persistence.Serializer
import utils.SerializerUtils
import utils.UITables
import java.time.LocalDateTime

/**
 * A class representing a Note API that allows for managing notes using a provided serializer.
 *
 * @param serializerType The serializer used for storing and retrieving notes.
 */
class NoteAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var notes = ArrayList<Note>()

    /**
     * Adds a note to the list of notes.
     *
     * @param note The note to add.
     * @return True if the note was added successfully, false otherwise.
     */
    fun add(note: Note): Boolean = notes.add(note)

    /**
     * Deletes a note from the list of notes by index.
     *
     * @param indexToDelete The index of the note to delete.
     * @return The deleted note if the index was valid, null otherwise.
     */
    fun deleteNote(indexToDelete: Int): Note? =
        if (isValidListIndex(indexToDelete, notes)) notes.removeAt(indexToDelete) else null

    /**
     * Removes multiple notes from the list of notes.
     *
     * @param noteList The list of notes to remove.
     */
    fun removeMultipleNotes(noteList: List<Note>) = notes.removeAll(noteList.toSet())

    /**
     * Updates a note at a given index with new note data.
     *
     * @param indexToUpdate The index of the note to update.
     * @param note The new note data.
     * @return True if the note was updated successfully, false otherwise.
     */
    fun updateNote(indexToUpdate: Int, note: Note): Boolean = findNote(indexToUpdate)?.apply {
        noteTitle = note.noteTitle
        notePriority = note.notePriority
        noteCategory = note.noteCategory
        isNoteArchived = note.isNoteArchived
        updatedAt = LocalDateTime.now()
    } != null

    /**
     * Archives a note at a given index.
     *
     * @param indexToUpdate The index of the note to archive.
     * @return True if the note was archived successfully, false otherwise.
     */
    fun archiveNote(indexToUpdate: Int): Boolean = findNote(indexToUpdate)?.apply {
        isNoteArchived = true
        updatedAt = LocalDateTime.now()
    } != null

    /**
     * Retrieves all notes as a mutable list.
     *
     * @return A mutable list containing all notes.
     */
    fun findAll(): MutableList<Note> = notes.toMutableList()

    /**
     * Lists all notes in a formatted string.
     *
     * @return A formatted string with all notes or a message indicating no notes stored.
     */
    fun listAllNotes(): String = if (notes.isEmpty()) "No notes stored" else
        generateAllNotesTable().renderText(border = TextBorder.ROUNDED)

    /**
     * Lists all active (non-archived) notes in a formatted string.
     *
     * @return A formatted string with all active notes or a message indicating no active notes stored.
     */
    fun listActiveNotes(): String = if (notes.isEmpty() || numberOfActiveNotes() == 0) "No active notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> !note.isNoteArchived }).renderText(border = TextBorder.ROUNDED)


    /**
     * Lists all archived notes in a formatted string.
     *
     * @return A formatted string with all archived notes or a message indicating no archived notes stored.
     */
    fun listArchivedNotes(): String = if (notes.isEmpty() || numberOfArchivedNotes() == 0) "No archived notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> note.isNoteArchived }).renderText(border = TextBorder.ROUNDED)

    /**
     * Lists all notes with a given priority in a formatted string.
     *
     * @param priority The priority to filter notes by.
     * @return A formatted string with all notes of the selected priority or a message indicating no notes with the specified priority.
     */
    fun listNotesBySelectedPriority(priority: Int): String =
        if (notes.isEmpty() || numberOfNotesByPriority(priority) == 0) "No notes with priority"
        else
            generateMultipleNotesTable(notes.filter { note -> note.notePriority == priority }).renderText(border = TextBorder.ROUNDED)

    /**
     * Lists all notes that haven't been updated in a given number of days.
     *
     * @param days The number of days to filter notes by.
     * @return A formatted string with all stale notes or a message indicating no stale notes stored.
     */
    fun listStaleNotes(days: Int): String = if (notes.isEmpty() || numberOfStaleNotes(days) == 0) "No stale notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> note.updatedAt.isBefore(LocalDateTime.now().minusDays(days.toLong())) }.sortedBy { it.updatedAt }).renderText(border = TextBorder.ROUNDED)

    /**
     * Lists notes with a priority of 1.
     *
     * @return A formatted string with all important notes or a message indicating no important notes stored.
     */
    fun listImportantNotes(): String = if (notes.isEmpty() || numberOfImportantNotes() == 0) "No important notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> note.notePriority == 1 }).renderText(border = TextBorder.ROUNDED)

    /**
     * Retrieves the number of archived notes.
     *
     * @return The number of archived notes.
     */
    fun numberOfArchivedNotes(): Int = notes.count { it.isNoteArchived }

    /**
     * Retrieves the number of active (non-archived) notes.
     *
     * @return The number of active notes.
     */
    fun numberOfActiveNotes(): Int = notes.count { !it.isNoteArchived }

    /**
     * Retrieves the number of notes with a given priority.
     *
     * @param priority The priority to filter notes by.
     * @return The number of notes with the specified priority.
     */
    fun numberOfNotesByPriority(priority: Int): Int = notes.count { it.notePriority == priority }

    /**
     * Retrieves the number of notes that haven't been updated in a given number of days.
     *
     * @param days The number of days to filter notes by.
     * @return The number of stale notes.
     */
    fun numberOfStaleNotes(days: Int): Int = notes.count { it.updatedAt.isBefore(LocalDateTime.now().minusDays(days.toLong())) }

    /**
     * Retrieves the number of notes with a priority of 1.
     *
     * @return The number of important notes.
     */
    fun numberOfImportantNotes(): Int = notes.count { it.notePriority == 1 }

    /**
     * Retrieves the total number of notes.
     *
     * @return The number of notes.
     */
    fun numberOfNotes(): Int = notes.size

    /**
     * Retrieves a note at a given index.
     *
     * @param index The index of the note to retrieve.
     * @return The note at the given index if the index is valid, null otherwise.
     */
    fun findNote(index: Int): Note? = if (isValidListIndex(index, notes))
        notes[index]
    else null

    /**
     * Finds a note in the list of notes.
     *
     * @param note The note to search for.
     * @return The note if found, null otherwise.
     */
    fun findUsingNote(note: Note): Note? = notes.find { it == note }

    /**
     * Finds the index of a note in the list of notes.
     *
     * @param note The note to search for.
     * @return The index of the note if found, -1 otherwise.
     */
    fun findIndexUsingNote(note: Note): Int = notes.indexOf(note)

    /**
     * Determines if a given index is valid in a list.
     *
     * @param index The index to validate.
     * @param list The list in which to check the index.
     * @return True if the index is valid, false otherwise.
     */
    private fun isValidListIndex(index: Int, list: List<Any>): Boolean = (index >= 0 && index < list.size)

    /**
     * Determines if a given index is valid in the list of notes.
     *
     * @param index The index to validate.
     * @return True if the index is valid, false otherwise.
     */
    fun isValidIndex(index: Int): Boolean = isValidListIndex(index, notes)

    fun isValidIndex(index: String?): Boolean = !index.isNullOrBlank() && index.toIntOrNull() != null && isValidIndex(index.toInt())

    /**
     * Searches for notes with a title containing a given search string.
     *
     * @param searchString The string to search for in note titles.
     * @return A formatted string with matching notes and their indices or an empty string if no matches are found.
     */
    fun searchByTitle(searchString: String) =
        notes.filter { note -> note.noteTitle.contains(searchString, ignoreCase = true) }
            .joinToString(separator = "\n") { note -> notes.indexOf(note).toString() + ": " + note.toString() }

    /**
     * Generates a table containing note information, using a predefined template.
     *
     * @param title The title to display in the table.
     * @param data The list of notes to display in the table.
     * @param allNotes An optional parameter to indicate whether to display all notes (default is false).
     * @return A table containing the note information.
     */
    private fun noteInfoTemplate(title: String, data: List<Note>, allNotes: Boolean = false): Table =
        UITables.noteInfoTemplate(title, data, allNotes)

    /**
     * Generates a table containing a single note's information.
     *
     * @param note The note to display.
     * @return A table containing the note's information.
     */
    fun generateNoteTable(note: Note): Table = noteInfoTemplate("Note Information", listOf(note))


    /**
     * Generates a table containing multiple notes' information.
     *
     * @param notes The list of notes to display.
     * @return A table containing the notes' information.
     */
    fun generateMultipleNotesTable(notes: List<Note>): Table = noteInfoTemplate("Multiple Note Information", notes)

    /**
     * Generates a table containing all notes' information.
     *
     * @return A table containing all notes' information.
     */
    fun generateAllNotesTable(): Table = noteInfoTemplate("All Note Information", notes, true)


    /**
     * Seeds the notes with a predefined set of notes.
     */
    fun seedNotes() {
        notes = SerializerUtils.getSeededNotes()
    }

    /**
     * Loads the notes from the serializer.
     *
     * @return True if the notes were loaded successfully, false otherwise.
     * @throws Exception if an error occurs while loading the notes.
     */
    @Throws(Exception::class)
    fun load(): Boolean =
        serializer.read()?.also {
            notes = it
        } != null

    /**
     * Stores the notes using the serializer.
     *
     * @throws Exception if an error occurs while storing the notes.
     */
    @Throws(Exception::class)
    fun store() = serializer.write(notes)
}