import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import utils.ScannerInput
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.lang.System.exit
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

val scanner = ScannerInput

private val noteAPI = NoteAPI()


fun mainMenu() : Int {
    val menu = """ 
         > ----------------------------------
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | NOTE MENU                      |
         > |   1) Add a note                |
         > |   2) List all notes            |
         > |   3) Update a note             |
         > |   4) Delete a note             |
         > ----------------------------------
         > |   5) List active notes         |
         > |   6) List archived notes       |
         > ----------------------------------
         > |   7) List notes with priority  |
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


fun listNotes(){
    //logger.info { "listNotes() function invoked" }
    println(noteAPI.listAllNotes())
}

fun listNotesByPriority(){
    //logger.info { "listNotesByPriority() function invoked" }
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    println(noteAPI.listNotesBySelectedPriority(notePriority))
}

fun updateNote(){
    logger.debug {"You chose Update Note"}
}

fun deleteNote(){
    logger.debug {"You chose Delete Note"}
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
            5 -> println(noteAPI.listActiveNotes())
            6 -> println(noteAPI.listArchivedNotes())
            7 -> listNotesByPriority()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun main(args: Array<String>) {
    runMenu()
}