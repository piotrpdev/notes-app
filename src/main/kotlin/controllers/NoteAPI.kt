package controllers

import models.Note
import persistence.Serializer

class NoteAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType

    private var notes = ArrayList<Note>()

    private fun formatListString(notesToFormat: List<Note>): String =
        notesToFormat
            .joinToString(separator = "\n") { note ->
                notes.indexOf(note).toString() + ": " + note.toString()
            }


    fun add(note: Note): Boolean = notes.add(note)

    fun deleteNote(indexToDelete: Int): Note? =
        if (isValidListIndex(indexToDelete, notes)) notes.removeAt(indexToDelete) else null


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

    fun listAllNotes(): String = if (notes.isEmpty()) "No notes stored" else
        formatListString(notes)


    fun listActiveNotes(): String = if (notes.isEmpty() || numberOfActiveNotes() == 0) "No active notes stored"
    else
        formatListString(notes.filter { note -> !note.isNoteArchived })


    fun listArchivedNotes(): String = if (notes.isEmpty() || numberOfArchivedNotes() == 0) "No archived notes stored"
    else
        formatListString(notes.filter { note -> note.isNoteArchived })


    //helper method to determine how many archived notes there are
    fun numberOfArchivedNotes(): Int = notes.count { it.isNoteArchived }

    fun numberOfActiveNotes(): Int = notes.count { !it.isNoteArchived }

    fun listNotesBySelectedPriority(priority: Int): String =
        if (notes.isEmpty() || numberOfNotesByPriority(priority) == 0) "No notes with priority"
        else
            formatListString(notes.filter { note -> note.notePriority == priority })


    fun numberOfNotesByPriority(priority: Int): Int = notes.count { it.notePriority == priority }

    fun numberOfNotes(): Int = notes.size

    fun findNote(index: Int): Note? = if (isValidListIndex(index, notes))
        notes[index]
    else null

    //utility method to determine if an index is valid in a list.
    private fun isValidListIndex(index: Int, list: List<Any>): Boolean = (index >= 0 && index < list.size)

    fun isValidIndex(index: Int): Boolean = isValidListIndex(index, notes)

    fun searchByTitle(searchString: String) =
        notes.filter { note -> note.noteTitle.contains(searchString, ignoreCase = true) }
            .joinToString(separator = "\n") { note -> notes.indexOf(note).toString() + ": " + note.toString() }


    @Throws(Exception::class)
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }


}