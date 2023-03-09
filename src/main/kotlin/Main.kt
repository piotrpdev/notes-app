import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File
import java.lang.System.exit
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

val scanner = ScannerInput

//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
//private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))
private val noteAPI = NoteAPI(YAMLSerializer(File("notes.yaml")))




fun mainMenu() : Int {
    val menu = """ 
         > ----------------------------------
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | NOTE MENU                      |
         > |   1) Add a note                |
         > |   2) List notes                |
         > |   3) Update a note             |
         > |   4) Delete a note             |
         > ----------------------------------
         > |   5) Archive a note            |
         > ----------------------------------
         > |   6) List notes with priority  |
         > ----------------------------------
         > |   7) Save notes to file        |
         > |   8) Load notes from file      |
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">")
    return scanner.readNextInt(menu)
}

fun addNote(){
    //logger.info { "addNote() function invoked" }
    val noteTitle = readNextLine("Enter a title for the note: ")
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine("Enter a category for the note: ")
    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun archiveNote() {
    //logger.info { "archiveNote() function invoked" }
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

//1 -> println(noteAPI.listAllNotes())
//2 -> println(noteAPI.listActiveNotes())
//3 -> println(noteAPI.listArchivedNotes())
fun listNotes(){
    //logger.info { "listNotes() function invoked" }
    val menu = """ 
         > ----------------------------------
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | LIST NOTES MENU                |
         > |   1) List all notes            |
         > |   2) List active notes         |
         > |   3) List archived notes       |
         > ----------------------------------
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">")

    when (scanner.readNextInt(menu)) {
        1 -> println(noteAPI.listAllNotes())
        2 -> println(noteAPI.listActiveNotes())
        3 -> println(noteAPI.listArchivedNotes())
        0 -> {} // https://stackoverflow.com/questions/60755131/how-to-handle-empty-in-kotlins-when
        else -> println("Invalid choice")
    }
}

fun listNotesByPriority(){
    //logger.info { "listNotesByPriority() function invoked" }
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    println(noteAPI.listNotesBySelectedPriority(notePriority))
}

fun updateNote() {
    //logger.info { "updateNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note if notes exist
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine("Enter a title for the note: ")
            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine("Enter a category for the note: ")

            //pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}


fun deleteNote(){
    //logger.info { "deleteNotes() function invoked" }
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

fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}



fun exitApp(){
    logger.debug {"Exiting...bye"}
    exitProcess(0)
}

fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1  -> addNote()
            2  -> listNotes()
            3  -> updateNote()
            4  -> deleteNote()
            5 -> archiveNote()
            6 -> listNotesByPriority()
            7 -> save()
            8 -> load()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun main(args: Array<String>) {
    runMenu()
}