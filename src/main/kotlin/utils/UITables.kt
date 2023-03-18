package utils

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table
import models.Note

// DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(date)

/**
 * An object containing preconfigured tables for displaying user interface elements related to notes.
 */
object UITables {
    /**
     * The main menu table, displayed as a formatted string.
     */
    val mainMenu = table {
        cellStyle {
            alignment = TextAlignment.MiddleRight
            paddingLeft = 1
            paddingRight = 1
            borderLeft = true
            borderRight = true
        }
        header {
            row {
                cell("Notes Menu") {
                    columnSpan = 2
                    alignment = TextAlignment.MiddleCenter
                    border = true
                }
            }
        }
        body {
            row {
                cell("1")
                cell("Add Note")
            }
            row {
                cell("2")
                cell("View Note")
            }
            row {
                cell("3")
                cell("Update Note")
            }
            row {
                cell("4")
                cell("Delete Note")
            }
            row {
                cell("5")
                cell("Archive Note")
            }
            row {
                cell("")
                cell("")
            }
            row {
                cell("6")
                cell("Search Notes")
            }
            row {
                cell("7")
                cell("Remove Multiple Notes")
            }
            row {
                cell("")
                cell("")
            }
            row {
                cell("8")
                cell("List Notes")
            }
            row {
                cell("")
                cell("")
            }
            row {
                cell("9")
                cell("Load Notes from File")
            }
            row {
                cell("10")
                cell("Save Notes to File")
                cellStyle {
                    borderBottom = true
                }
            }
        }
        footer {
            row {
                cell("0")
                cell("Exit")
                cellStyle {
                    borderBottom = true
                }
            }
        }
    }.renderText(border = TextBorder.ROUNDED)

    /**
     * The list notes menu table, displayed as a formatted string.
     */
    val listNotesMenu = table {
        cellStyle {
            alignment = TextAlignment.MiddleRight
            paddingLeft = 1
            paddingRight = 1
            borderLeft = true
            borderRight = true
        }
        header {
            row {
                cell("List Notes Menu") {
                    columnSpan = 2
                    alignment = TextAlignment.MiddleCenter
                    border = true
                }
            }
        }
        body {
            row {
                cell("1")
                cell("List All Notes")
            }
            row {
                cell("2")
                cell("List Active Notes")
            }
            row {
                cell("3")
                cell("List Archived Notes")
            }
            row {
                cell("4")
                cell("List Notes by Priority")
            }
            row {
                cell("5")
                cell("List Stale Notes")
            }
            row {
                cell("6")
                cell("List Important Notes")
                cellStyle {
                    borderBottom = true
                }
            }
        }
        footer {
            row {
                cell("0")
                cell("Exit")
                cellStyle {
                    borderBottom = true
                }
            }
        }
    }.renderText(border = TextBorder.ROUNDED)

    /**
     * Generates a table containing note information, using a predefined template.
     *
     * @param title The title to display in the table.
     * @param data The list of notes to display in the table.
     * @param allNotes A flag indicating whether to display all notes (default is false).
     * @return A table containing the note information.
     */
    @JvmStatic
    fun noteInfoTemplate(title: String, data: List<Note>, allNotes: Boolean) = table {
        cellStyle {
            alignment = TextAlignment.MiddleRight
            paddingLeft = 1
            paddingRight = 1
            borderLeft = true
            borderRight = true
        }
        header {
            row {
                cell(title) {
                    columnSpan = if (allNotes) 5 else 4
                    alignment = TextAlignment.MiddleCenter
                    border = true
                }
            }
            row {
                cellStyle {
                    border = true
                    alignment = TextAlignment.BottomLeft
                }
                if (allNotes) {
                    cell("Index") {
                        alignment = TextAlignment.MiddleCenter
                    }
                }
                cell("Title") {
                    alignment = TextAlignment.MiddleCenter
                }
                cell("Priority") {
                    alignment = TextAlignment.MiddleCenter
                }
                cell("Category") {
                    alignment = TextAlignment.MiddleCenter
                }
                cell("Archived") {
                    alignment = TextAlignment.MiddleCenter
                }
                cell("Updated At") {
                    alignment = TextAlignment.MiddleCenter
                }
                cell("Created At") {
                    alignment = TextAlignment.MiddleCenter
                }
            }
        }
        body {
            data.forEachIndexed { index, it ->
                row {
                    if (allNotes) {
                        cell(index.toString()) {
                            alignment = TextAlignment.MiddleCenter
                        }
                    }
                    cell(it.noteTitle) {}
                    cell(it.notePriority.toString()) {}
                    cell(it.noteCategory) {}
                    cell(if (it.isNoteArchived) "Yes" else "No") {}
                    cell(it.updatedAt.toString()) {}
                    cell(it.createdAt.toString()) {}
                    if (index == data.size - 1) {
                        cellStyle {
                            borderBottom = true
                        }
                    }
                }
            }
        }
    }
}