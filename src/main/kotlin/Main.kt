import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.XMLSerializer
import utils.SerializerUtils
import utils.UITables
import java.io.File
import java.time.LocalDateTime
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
//private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))
//private val noteAPI = NoteAPI(YAMLSerializer(File("notes.yaml")))

/**
 * Prints all notes in a tabular format with rounded borders.
 */
fun printAllNotes() = println(noteAPI.generateAllNotesTable().renderText(border = TextBorder.ROUNDED))

/**
 * Generates a new note instance using user input for note properties.
 * @param old An optional Note instance to update. The new note will use the old note's properties as default values.
 * @return A new Note instance with the provided properties.
 */
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
    while (notePriority == null || notePriority !in 1..5) {
        notePriority = if (old != null)
            old.notePriority
        else {
            println("Error: note priority must be a valid non-negative integer between 1 and 5. Please enter a valid note priority.")
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

/**
 * Retrieves a Note instance based on its index.
 * @return The Note instance found, or null if not found.
 */
internal fun getNoteByIndex(): Note? {
    logger.debug { "Trying to get note by index" }

    if (noteAPI.numberOfNotes() == 0) {
        println("No notes found.")
        return null
    }

    printAllNotes()

    val allNotes = noteAPI.findAll()

    print("Enter the index of the note you want to use: ")
    val noteIndex = readln().toIntOrNull()

    if (noteIndex == null || !noteAPI.isValidIndex(noteIndex)) {
        println("Error: Invalid index. Please enter a valid index.")
        return getNoteByIndex()
    }

    val note = allNotes[noteIndex]

    logger.debug { "Note found: $note" }
    logger.debug { "Displaying note" }
    println("\nThe following note was found:")
    println(noteAPI.generateNoteTable(note).renderText(border = TextBorder.ROUNDED))

    return note
}

/**
 * Retrieves multiple Note instances based on their index.
 * @return A MutableList of Note instances found, or null if none found.
 */
internal fun getMultipleNotesByIndex(): MutableList<Note>? {
    logger.debug { "Trying to get multiple notes by index" }

    if (noteAPI.numberOfNotes() == 0) {
        println("No notes found.")
        return null
    }

    printAllNotes()

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

/**
 * Filters a MutableList of Note instances based on user input.
 * @param noteList The MutableList of Note instances to filter.
 * @return A MutableList of filtered Note instances, or null if none found.
 */
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
                noteList.removeIf { !it.noteTitle.lowercase().contains(noteTitle.lowercase()) }
            }

            2 -> {
                print("Enter the note priority to filter by: ")
                var notePriority = readln().toIntOrNull()
                while (notePriority == null || notePriority !in 1..5) {
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

/**
 * Sorts a MutableList of Note instances based on user input.
 * @param noteList The MutableList of Note instances to sort. Defaults to all notes.
 * @return A MutableList of sorted Note instances.
 */
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

/**
 * Adds a new note to the NoteAPI using user input for properties.
 */
fun addNote() {
    logger.debug { "addNote() function invoked" }

    val note = generateNote()

    logger.debug { "Adding note: $note" }
    noteAPI.add(note)

    println("\nThe following note was added successfully:\n")
    println(noteAPI.generateNoteTable(noteAPI.findUsingNote(note) ?: note).renderText(border = TextBorder.ROUNDED))
}

/**
 * Displays a selected note based on its index.
 */
fun viewNote() {
    logger.debug { "viewNote() function invoked" }

    getNoteByIndex() ?: return
}

/**
 * Updates an existing note with new properties based on user input.
 */
fun updateNote() {
    logger.debug { "updateNote() function invoked" }

    val note = getNoteByIndex() ?: return

    println("\nPlease enter the new details for the note (Enter nothing to keep previous value):")

    val updatedNote = generateNote(note)
    updatedNote.createdAt = note.createdAt

    logger.debug { "Note found, updating note" }
    noteAPI.updateNote(noteAPI.findIndexUsingNote(note), updatedNote)

    println("\nThe note was updated successfully:\n")
    println(noteAPI.generateNoteTable(updatedNote).renderText(border = TextBorder.ROUNDED))
}

/**
 * Deletes a note based on its index.
 */
fun deleteNote() {
    logger.debug { "deleteNote() function invoked" }

    if (noteAPI.numberOfNotes() == 0) {
        println("No notes found.")
        return
    }

    printAllNotes()

    print("Enter the index of the note you want to delete: ")
    val noteIndex = readln().toIntOrNull()

    if (noteIndex == null || !noteAPI.isValidIndex(noteIndex)) {
        println("Error: Invalid index. Please enter a valid index.")
        return deleteNote()
    }

    //pass the index of the note to NoteAPI for deleting and check for success.
    val noteToDelete = noteAPI.deleteNote(noteIndex)
    if (noteToDelete != null) {
        println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
    } else {
        println("Delete NOT Successful")
    }
}

/**
 * Toggles the archive status of a note based on its index.
 */
fun archiveNote() {
    logger.debug { "archiveNote() function invoked" }

    if (noteAPI.numberOfNotes() == 0) {
        println("No notes found.")
        return
    }

    printAllNotes()

    print("Enter the index of the note you want to archive: ")
    val noteIndex = readln().toIntOrNull()

    if (noteIndex == null || !noteAPI.isValidIndex(noteIndex)) {
        println("Error: Invalid index. Please enter a valid index.")
        return archiveNote()
    }

    //pass the index of the note and the new note details to NoteAPI for updating and check for success.
    if (noteAPI.archiveNote(noteIndex)) {
        println("Archive Successful")
    } else {
        println("Archive Failed")
    }
}

/**
 * Searches for notes based on index, filters, and sorts them based on user input.
 */
fun searchNotes() {
    logger.debug { "searchNotes() function invoked" }

    val noteList = getMultipleNotesByIndex() ?: return
    val filteredNoteList = getFilteredNotes(noteList) ?: return
    val sortedNoteList = getSortedNotes(filteredNoteList)

    println("Here are the notes you wanted to view:")
    println(noteAPI.generateMultipleNotesTable(sortedNoteList).renderText(border = TextBorder.ROUNDED))
}

/**
 * Removes multiple notes based on their index.
 */
fun removeMultipleNotes() {
    logger.debug { "removeMultipleNotes() function invoked" }

    val noteList = getMultipleNotesByIndex() ?: return

    println("Here are the notes you wanted to remove:")
    println(noteAPI.generateMultipleNotesTable(noteList).renderText(border = TextBorder.ROUNDED))

    print("Are you sure you want to remove these notes? (y/n): ")
    val delete = readln().toCharArray().getOrNull(0)
    if (delete != 'y') {
        println("Notes not deleted.")
        return
    }

    logger.debug { "Removing multiple notes" }
    noteAPI.removeMultipleNotes(noteList)
    println("Notes deleted.")
}

/**
 * Lists notes based on user-selected criteria.
 */
fun listNotes() {
    logger.debug { "listNotes() function invoked" }

    println(UITables.listNotesMenu)

    print("Enter option: ")

    when (readln().toIntOrNull()) {
        1 -> println(noteAPI.listAllNotes())
        2 -> println(noteAPI.listActiveNotes())
        3 -> println(noteAPI.listArchivedNotes())
        4 -> listNotesByPriority()
        0 -> {} // https://stackoverflow.com/questions/60755131/how-to-handle-empty-in-kotlins-when
        else -> println("Invalid choice")
    }
}

/**
 * Lists notes based on a specified priority.
 */
fun listNotesByPriority() {
    logger.debug { "listNotesByPriority() function invoked" }

    print("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val notePriority = readln().toIntOrNull()

    if (notePriority == null || notePriority !in 1..5) {
        println("Error: Invalid note priority. Please enter a valid one.")
        return listNotesByPriority()
    }

    println(noteAPI.listNotesBySelectedPriority(notePriority))
}

/**
 * Loads notes from an external file.
 */
fun load() {
    logger.debug { "load() function invoked" }

    try {
        if (noteAPI.load()) {
            println("Notes loaded successfully:")

            printAllNotes()
        } else {
            println("Error loading notes, see debug log for more info")
        }
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

/**
 * Saves notes to an external file.
 */
fun save() {
    logger.debug { "save() function invoked" }

    try {
        noteAPI.store()
        println("Notes saved successfully:")

        printAllNotes()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

/**
 * Exits the application.
 */
fun exitApp() {
    logger.debug { "exitApp() function invoked" }

    logger.debug { "Exiting...bye" }
    exitProcess(0)
}

/**
 * Displays the main menu of the application and reads user input for the selected option.
 * @return The user's selected option as an Int, or null if an invalid option was entered.
 */
fun mainMenu(): Int? {
    logger.debug { "mainMenu() function invoked" }

    println(UITables.mainMenu)

    print("Enter option: ")

    return readln().toIntOrNull()
}

/**
 * Show menu and handle user choices.
 */
fun runMenu() {
    logger.debug { "runMenu() function invoked" }

    do {
        when (val option = mainMenu()) {
            1 -> addNote()
            2 -> viewNote() // TODO: Add dedicated ui table e.g. like payslip
            3 -> updateNote()
            4 -> deleteNote()
            5 -> archiveNote()
            6 -> searchNotes()
            7 -> removeMultipleNotes()
            8 -> listNotes()
            9 -> load()
            10 -> save()
            -98 -> SerializerUtils.generateSeededFiles()
            -99 -> noteAPI.seedNotes()
            0 -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

/**
 * Start the Notes App.
 */
fun main() {
    logger.debug { "main() function invoked" }
    // https://patorjk.com/software/taag/
    println(
        """
        .__   __.   ______   .___________. _______     _______.        ___      .______   .______   
        |  \ |  |  /  __  \  |           ||   ____|   /       |       /   \     |   _  \  |   _  \  
        |   \|  | |  |  |  | `---|  |----`|  |__     |   (----`      /  ^  \    |  |_)  | |  |_)  | 
        |  . `  | |  |  |  |     |  |     |   __|     \   \         /  /_\  \   |   ___/  |   ___/  
        |  |\   | |  `--'  |     |  |     |  |____.----)   |       /  _____  \  |  |      |  |      
        |__| \__|  \______/      |__|     |_______|_______/       /__/     \__\ | _|      | _|      

    """.trimIndent()
    )

    load()

    runMenu()
}