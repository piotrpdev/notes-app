package controllers

import com.jakewharton.picnic.Table
import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import models.Note
import persistence.Serializer
import utils.SerializerUtils
import utils.UITables
import java.time.LocalDateTime

class NoteAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean = notes.add(note)

    fun deleteNote(indexToDelete: Int): Note? =
        if (isValidListIndex(indexToDelete, notes)) notes.removeAt(indexToDelete) else null

    fun removeMultipleNotes(noteList: List<Note>) = notes.removeAll(noteList.toSet())

    fun updateNote(indexToUpdate: Int, note: Note): Boolean = findNote(indexToUpdate)?.apply {
        noteTitle = note.noteTitle
        notePriority = note.notePriority
        noteCategory = note.noteCategory
        isNoteArchived = note.isNoteArchived
        updatedAt = LocalDateTime.now()
    } != null

    fun archiveNote(indexToUpdate: Int): Boolean = findNote(indexToUpdate)?.apply {
        isNoteArchived = true
        updatedAt = LocalDateTime.now()
    } != null

    fun findAll(): MutableList<Note> = notes.toMutableList()

    fun listAllNotes(): String = if (notes.isEmpty()) "No notes stored" else
        generateAllNotesTable().renderText(border = TextBorder.ROUNDED)


    fun listActiveNotes(): String = if (notes.isEmpty() || numberOfActiveNotes() == 0) "No active notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> !note.isNoteArchived }).renderText(border = TextBorder.ROUNDED)


    fun listArchivedNotes(): String = if (notes.isEmpty() || numberOfArchivedNotes() == 0) "No archived notes stored"
    else
        generateMultipleNotesTable(notes.filter { note -> note.isNoteArchived }).renderText(border = TextBorder.ROUNDED)

    fun listNotesBySelectedPriority(priority: Int): String =
        if (notes.isEmpty() || numberOfNotesByPriority(priority) == 0) "No notes with priority"
        else
            generateMultipleNotesTable(notes.filter { note -> note.notePriority == priority }).renderText(border = TextBorder.ROUNDED)


    //helper method to determine how many archived notes there are
    fun numberOfArchivedNotes(): Int = notes.count { it.isNoteArchived }

    fun numberOfActiveNotes(): Int = notes.count { !it.isNoteArchived }

    fun numberOfNotesByPriority(priority: Int): Int = notes.count { it.notePriority == priority }

    fun numberOfNotes(): Int = notes.size

    fun findNote(index: Int): Note? = if (isValidListIndex(index, notes))
        notes[index]
    else null

    fun findUsingNote(note: Note): Note? = notes.find { it == note }

    fun findIndexUsingNote(note: Note): Int = notes.indexOf(note)

    //utility method to determine if an index is valid in a list.
    private fun isValidListIndex(index: Int, list: List<Any>): Boolean = (index >= 0 && index < list.size)

    fun isValidIndex(index: Int): Boolean = isValidListIndex(index, notes)

    fun searchByTitle(searchString: String) =
        notes.filter { note -> note.noteTitle.contains(searchString, ignoreCase = true) }
            .joinToString(separator = "\n") { note -> notes.indexOf(note).toString() + ": " + note.toString() }

    private fun noteInfoTemplate(title: String, data: List<Note>, allNotes: Boolean = false): Table {
        return UITables.noteInfoTemplate(title, data, allNotes)
    }

    fun generateNoteTable(note: Note): Table {
        return noteInfoTemplate("Note Information", listOf(note))
    }

    fun generateMultipleNotesTable(notes: List<Note>): Table {
        return noteInfoTemplate("Multiple Note Information", notes)
    }

    fun generateAllNotesTable(): Table {
        return noteInfoTemplate("All Note Information", notes, true)
    }

    fun seedNotes() { notes = SerializerUtils.getSeededNotes() }

    @Throws(Exception::class)
    fun load(): Boolean =
        serializer.read()?.also {
            notes = it
        } != null


    @Throws(Exception::class)
    fun store() = serializer.write(notes)


}