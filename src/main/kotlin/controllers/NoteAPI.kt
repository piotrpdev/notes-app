package controllers

import models.Note

class NoteAPI {
    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean {
        return notes.add(note)
    }

    fun listAllNotes(): String {
        return if (notes.isEmpty()) {
            "No notes stored"
        } else {
            var listOfNotes = ""
            for (i in notes.indices) {
                listOfNotes += "${i}: ${notes[i]} \n"
            }
            listOfNotes
        }
    }

    fun listActiveNotes(): String = if (notes.isEmpty() || numberOfActiveNotes() == 0) "No active notes stored"
        else {
            var listOfNotes = ""
            for (i in notes.indices) {
                if (!notes[i].isNoteArchived) {
                    listOfNotes += "${i}: ${notes[i]}\n"
                }
            }
            listOfNotes
        }

    fun listArchivedNotes(): String = if (notes.isEmpty() || numberOfArchivedNotes() == 0) "No archived notes stored"
        else {
            var listOfNotes = ""
            for (i in notes.indices) {
                if (notes[i].isNoteArchived) {
                    listOfNotes += "${i}: ${notes[i]}\n"
                }
            }
            listOfNotes
        }

    //helper method to determine how many archived notes there are
    fun numberOfArchivedNotes(): Int = notes.filter { it -> it.isNoteArchived }.size

    fun numberOfActiveNotes(): Int = notes.filter { it -> !it.isNoteArchived }.size


    fun numberOfNotes(): Int {
        return notes.size
    }

    fun findNote(index: Int): Note? {
        return if (isValidListIndex(index, notes)) {
            notes[index]
        } else null
    }

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

}