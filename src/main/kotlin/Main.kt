import utils.ScannerInput
import java.lang.System.exit
import java.util.*
import kotlin.system.exitProcess

val scanner = ScannerInput

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
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">")
    return scanner.readNextInt(menu)
}

fun addNote(){
    println("You chose Add Note")
}

fun listNotes(){
    println("You chose List Notes")
}

fun updateNote(){
    println("You chose Update Note")
}

fun deleteNote(){
    println("You chose Delete Note")
}

fun exitApp(){
    println("Exiting...bye")
    exitProcess(0)
}

fun runMenu() {
    do {
        when (val option = mainMenu()) {
            1  -> addNote()
            2  -> listNotes()
            3  -> updateNote()
            4  -> deleteNote()
            0  -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun main(args: Array<String>) {
    runMenu()
}