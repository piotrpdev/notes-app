package utils

import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.TextBorder
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table

object UITables {
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
                cell("Delete Multiple Notes")
            }
            row {
                cell("")
                cell("")
            }
            row {
                cell("8")
                cell("List All Notes")
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
    }.renderText(border= TextBorder.ROUNDED)

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
    }.renderText(border= TextBorder.ROUNDED)
}