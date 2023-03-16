import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import utils.SerializerUtils
import utils.UITables
import java.io.File
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

val scanner = ScannerInput

private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
//private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))
//private val noteAPI = NoteAPI(YAMLSerializer(File("notes.yaml")))

fun mainMenu() : Int {
    logger.debug { "mainMenu() function invoked" }

    println(UITables.mainMenu)

    return scanner.readNextInt("Enter option: ")
}

fun addNote(){
    logger.debug { "addNote() function invoked" }

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
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    logger.debug { "load() function invoked" }

    try {
        if (noteAPI.load()) {
            println("Notes loaded successfully")
        } else {
            println("No notes to load")
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
    TODO("Not yet implemented")
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