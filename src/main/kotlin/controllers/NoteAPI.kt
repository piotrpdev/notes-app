package controllers

import models.Note
import persistence.Serializer

class NoteAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean = notes.add(note)

    fun deleteNote(indexToDelete: Int): Note? = if (isValidListIndex(indexToDelete, notes)) {
        notes.removeAt(indexToDelete)
    } else null


    fun updateNote(indexToUpdate: Int, note: Note?): Boolean {
        //find the note object by the index number
        val foundNote = findNote(indexToUpdate)

        //if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if ((foundNote != null) && (note != null)) {
            foundNote.noteTitle = note.noteTitle
            foundNote.notePriority = note.notePriority
            foundNote.noteCategory = note.noteCategory
            return true
        }

        //if the note was not found, return false, indicating that the update was not successful
        return false
    }

    fun archiveNote(indexToUpdate: Int): Boolean {
        //find the note object by the index number
        val foundNote = findNote(indexToUpdate)

        //if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if (foundNote != null) {
            foundNote.isNoteArchived = true
            return true
        }

        //if the note was not found, return false, indicating that the update was not successful
        return false
    }

    fun listAllNotes(): String = if (notes.isEmpty()) {
        "No notes stored"
    } else {
        notes.mapIndexed { index, note -> "$index: $note" }.joinToString("\n")
    }


    fun listActiveNotes(): String = if (notes.isEmpty() || numberOfActiveNotes() == 0) "No active notes stored"
    else {
        notes.filter { !it.isNoteArchived }.mapIndexed { index, note -> "$index: $note" }.joinToString("\n")
    }

    fun listArchivedNotes(): String = if (notes.isEmpty() || numberOfArchivedNotes() == 0) "No archived notes stored"
    else {
        notes.filter { it.isNoteArchived }.mapIndexed { index, note -> "$index: $note" }.joinToString("\n")
    }

    //helper method to determine how many archived notes there are
    fun numberOfArchivedNotes(): Int = notes.filter { it.isNoteArchived }.size

    fun numberOfActiveNotes(): Int = notes.filter { !it.isNoteArchived }.size

    fun listNotesBySelectedPriority(priority: Int): String =
        if (notes.isEmpty() || numberOfNotesByPriority(priority) == 0) "No notes with priority"
        else {
            notes.filter { it.notePriority == priority }.mapIndexed { index, note -> "$index: $note" }
                .joinToString("\n")
        }

    fun numberOfNotesByPriority(priority: Int): Int = notes.filter { it.notePriority == priority }.size

    fun numberOfNotes(): Int = notes.size

    fun findNote(index: Int): Note? = if (isValidListIndex(index, notes)) {
        notes[index]
    } else null

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean = (index >= 0 && index < list.size)

    fun isValidIndex(index: Int): Boolean = isValidListIndex(index, notes)

    @Throws(Exception::class)
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }


}