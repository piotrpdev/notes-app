
import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.XMLSerializer
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import utils.SerializerUtils
import utils.UITables
import java.io.File
import java.time.LocalDateTime
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

val scanner = ScannerInput

private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
//private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))
//private val noteAPI = NoteAPI(YAMLSerializer(File("notes.yaml")))

fun generateNote(old: Note? = null): Note {
    logger.debug { "Generating note" }

    // ? Trying to update with an invalid value just keeps the old one
    // def = default; shortened to save space
    fun def(prop: Any?) = if (old != null) " (${prop})" else ""

    print("Enter note title${def(old?.noteTitle)}: ")
    var noteTitle = readlnOrNull().toString()
    while (noteTitle.isBlank()) {
        noteTitle = if (old != null)
            old.noteTitle
        else {
            println("Error: note title cannot be empty. Please enter a valid note title.")
            readlnOrNull().toString()
        }
    }

    print("Enter note priority${def(old?.notePriority)}: ")
    var notePriority = readln().toIntOrNull()
    while (notePriority == null || notePriority < 0) {
        notePriority = if (old != null)
            old.notePriority
        else {
            println("Error: note priority must be a valid non-negative integer. Please enter a valid note priority.")
            readln().toIntOrNull()
        }
    }

    print("Enter note category${def(old?.noteCategory)}: ")
    var noteCategory = readlnOrNull().toString()
    while (noteCategory.isBlank()) {
        noteCategory = if (old != null)
            old.noteCategory
        else {
            println("Error: note category cannot be empty. Please enter a valid note category.")
            readlnOrNull().toString()
        }
    }

    print("Is note archived? (y/n)${def(if (old?.isNoteArchived == true) "y" else "n")} ")
    var isNoteArchived = readln().toCharArray().getOrNull(0)
    while (isNoteArchived != 'y' && isNoteArchived != 'n') {
        isNoteArchived = if (old != null)
            if (old.isNoteArchived) 'y' else 'n'
        else {
            println("Error: input must be either 'y' or 'n'. Please enter a valid option.")
            readln().toCharArray()[0]
        }
    }

    return Note(noteTitle, notePriority, noteCategory, isNoteArchived == 'y')
}

internal fun getNoteByIndex(): Note? {
    logger.debug { "Trying to get note by index" }

    val allNotes = noteAPI.findAll()
    if (allNotes.isEmpty()) {
        println("No notes found.")
        return null
    }

    print("Enter the index of the note you want to find: ")
    val noteIndex = readln().toIntOrNull()

    if (noteIndex == null || noteIndex < 0 || noteIndex >= allNotes.size) {
        println("Error: Invalid index. Please enter a valid index.")
        return getNoteByIndex()
    }

    val note = allNotes[noteIndex]

    logger.debug { "Note found: $note" }
    logger.debug { "Displaying note" }
    println("\nThe following note was found:")
    println(noteAPI.generateNoteTable(note).renderText(border= TextBorder.ROUNDED))

    return note
}

internal fun getMultipleNotesByIndex(): MutableList<Note>? {
    logger.debug { "Trying to get multiple notes by index" }

    print("Do you want to search for multiple notes using their index? (y/n): ")
    val searchMultiple = readln().toCharArray().getOrNull(0)
    if (searchMultiple != 'y') {
        logger.debug { "User does not want to search for multiple notes using index" }
        return noteAPI.findAll()
    }

    var searching = true
    val noteList: MutableList<Note> = ArrayList()

    fun noteInList(note: Note?): Boolean {
        if (note == null) return false
        return noteList.any { p -> p == note }
    }

    while (searching) {
        val note = getNoteByIndex()

        if (noteInList(note)) {
            println("Note already added to list.")
        }

        if (note != null && !noteInList(note)) {
            noteList.add(note)
        }

        print("Do you want to add another note to the list using their index? (y/n): ")
        val searchAgain = readln().toCharArray().getOrNull(0)
        if (searchAgain != 'y') {
            searching = false
        }
        println()
    }

    logger.debug { "Returning note list (might be empty)." }
    return noteList.ifEmpty { null }
}

fun getFilteredNotes(noteList: MutableList<Note>): MutableList<Note>? {
    logger.debug { "Trying to get filtered notes" }

    print("Do you want to filter the notes? (y/n): ")
    if (readln().toCharArray()[0] != 'y') {
        logger.debug { "Not filtering notes" }
        return noteList
    }

    var filtering = true
    while (filtering) {
        print("How do you want to filter the notes? (1 - Title, 2 - Priority, 3 - Category, 4 - Archived, 5 - Updated At, 6 - Created At): ")
        when (readln().toIntOrNull()) {
            1 -> {
                print("Enter the note title to filter by: ")
                val noteTitle = readln()
                noteList.removeIf { it.noteTitle != noteTitle }
            }
            2 -> {
                print("Enter the note priority to filter by: ")
                var notePriority = readln().toIntOrNull()
                while (notePriority == null) {
                    println("Error: note priority must be a valid number. Please enter a valid note priority.")
                    notePriority = readln().toIntOrNull()
                }
                noteList.removeIf { it.notePriority != notePriority }
            }
            3 -> {
                print("Enter the note category to filter by: ")
                val noteCategory = readln()
                noteList.removeIf { it.noteCategory != noteCategory }
            }
            4 -> {
                print("Enter 'true' to filter archived notes, 'false' to filter unarchived notes: ")
                val isNoteArchived = readln().toBooleanStrictOrNull()
                if (isNoteArchived == null) {
                    println("Error: please enter 'true' or 'false' for archived status.")
                } else {
                    noteList.removeIf { it.isNoteArchived != isNoteArchived }
                }
            }
            5 -> {
                print("Enter the note updated at to filter by: ")
                val updatedAt = readln()
                noteList.removeIf { it.updatedAt.compareTo(LocalDateTime.parse(updatedAt)) != 0 }
            }
            6 -> {
                print("Enter the note created at to filter by: ")
                val createdAt = readln()
                noteList.removeIf { it.createdAt.compareTo(LocalDateTime.parse(createdAt)) != 0 }
            }
            else -> {
                println("Error: invalid option. Please enter a valid option.")
                continue
            }
        }

        print("Do you want to filter the notes again? (y/n): ")
        val filterAgain = readln().toCharArray().getOrNull(0)
        if (filterAgain != 'y') {
            filtering = false
        }
        println()
    }

    logger.debug { "Returning filtered note list (might be empty)." }
    return noteList.ifEmpty { null }
}

fun getSortedNotes(noteList: MutableList<Note> = noteAPI.findAll()): MutableList<Note> {
    logger.debug { "Trying to get sorted notes" }

    print("Do you want to sort the notes? (y/n): ")
    if (readln().toCharArray()[0] != 'y') {
        logger.debug { "Not sorting notes" }
        return noteList
    }

    print("How do you want to sort the notes? (1 - Title, 2 - Priority, 3 - Category, 4 - Archived, 5 - Updated At, 6 - Created At): ")

    when (readln().toIntOrNull()) {
        1 -> noteList.sortBy { it.noteTitle }
        2 -> noteList.sortBy { it.notePriority }
        3 -> noteList.sortBy { it.noteCategory }
        4 -> noteList.sortBy { it.isNoteArchived }
        5 -> noteList.sortBy { it.updatedAt }
        6 -> noteList.sortBy { it.createdAt }
        else -> {
            println("Error: Invalid option. Please enter a valid option.")
            return getSortedNotes()
        }
    }

    logger.debug { "Returning sorted note list (might be empty)." }
    return noteList
}

fun mainMenu() : Int {
    logger.debug { "mainMenu() function invoked" }

    println(UITables.mainMenu)

    return scanner.readNextInt("Enter option: ")
}

fun addNote(){
    logger.debug { "addNote() function invoked" }

    val note = generateNote()

    logger.debug { "Adding note: $note" }
    noteAPI.add(note)

    println("\nThe following employee was added successfully:\n")
    println(noteAPI.generateNoteTable(noteAPI.findUsingNote(note) ?: note).renderText(border=TextBorder.ROUNDED))
}

fun archiveNote() {
    logger.debug { "archiveNote() function invoked" }

    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note if notes exist
        val indexToUpdate = readNextInt("Enter the index of the note to archive: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {
            //pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (noteAPI.archiveNote(indexToUpdate)){
                println("Archive Successful")
            } else {
                println("Archive Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}

fun listNotes(){
    logger.debug { "listNotes() function invoked" }

    println(UITables.listNotesMenu)

    when (scanner.readNextInt("Enter option: ")) {
        1 -> println(noteAPI.listAllNotes())
        2 -> println(noteAPI.listActiveNotes())
        3 -> println(noteAPI.listArchivedNotes())
        4 -> listNotesByPriority()
        0 -> {} // https://stackoverflow.com/questions/60755131/how-to-handle-empty-in-kotlins-when
        else -> println("Invalid choice")
    }
}

fun listNotesByPriority(){
    logger.debug { "listNotesByPriority() function invoked" }

    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    println(noteAPI.listNotesBySelectedPriority(notePriority))
}

fun updateNote() {
    logger.debug { "updateNote() function invoked" }

    val note = getNoteByIndex() ?: return

    println("\nPlease enter the new details for the note (Enter nothing to keep previous value):")

    val updatedNote = generateNote(note)
    updatedNote.createdAt = note.createdAt

    logger.debug { "Employee found, updating employee" }
    noteAPI.updateNote(noteAPI.findIndexUsingNote(note)!!, updatedNote)

    println("\nThe employee was updated successfully:\n")
    println(noteAPI.generateNoteTable(updatedNote).renderText(border=TextBorder.ROUNDED))
}


fun deleteNote(){
    logger.debug { "deleteNote() function invoked" }

    listNotes()

    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note to delete if notes exist
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        //pass the index of the note to NoteAPI for deleting and check for success.
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun searchNotes() {
    logger.debug { "searchNotes() function invoked" }

    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No notes found")
    } else {
        println(searchResults)
    }
}


fun save() {
    logger.debug { "save() function invoked" }

    try {
        noteAPI.store()
        println("Notes saved successfully:")

        println(noteAPI.generateAllNotesTable().renderText(border=TextBorder.ROUNDED))
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    logger.debug { "load() function invoked" }

    try {
        if (noteAPI.load()) {
            println("Notes loaded successfully:")

            println(noteAPI.generateAllNotesTable().renderText(border=TextBorder.ROUNDED))
        } else {
            println("Error loading notes, see debug log for more info")
        }
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}



fun exitApp(){
    logger.debug { "exitApp() function invoked" }

    logger.debug {"Exiting...bye"}
    exitProcess(0)
}

fun runMenu() {
    logger.debug { "runMenu() function invoked" }

    do {
        when (val option = mainMenu()) {
            1  -> addNote()
            2  -> viewNote()
            3  -> updateNote()
            4  -> deleteNote()
            5 -> archiveNote()
            6 -> searchNotes()
            7 -> deleteMultipleNotes()
            8 -> listNotes()
            9 -> load()
            10 -> save()
            -98 -> SerializerUtils.generateSeededFiles()
            -99 -> noteAPI.seedNotes()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun deleteMultipleNotes() {
    logger.debug { "deleteMultipleNotes() function invoked" }
    TODO("Not yet implemented")
}

fun viewNote() {
    logger.debug { "viewNote() function invoked" }

    getNoteByIndex() ?: return
}

fun main(args: Array<String>) {
    logger.debug { "main() function invoked" }
    // https://patorjk.com/software/taag/
    println("""
        .__   __.   ______   .___________. _______     _______.        ___      .______   .______   
        |  \ |  |  /  __  \  |           ||   ____|   /       |       /   \     |   _  \  |   _  \  
        |   \|  | |  |  |  | `---|  |----`|  |__     |   (----`      /  ^  \    |  |_)  | |  |_)  | 
        |  . `  | |  |  |  |     |  |     |   __|     \   \         /  /_\  \   |   ___/  |   ___/  
        |  |\   | |  `--'  |     |  |     |  |____.----)   |       /  _____  \  |  |      |  |      
        |__| \__|  \______/      |__|     |_______|_______/       /__/     \__\ | _|      | _|      

    """.trimIndent())

    runMenu()
}