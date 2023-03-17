package controllers

import com.jakewharton.picnic.renderText
import models.Note
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import utils.SerializerUtils.generateSeededFiles
import utils.SerializerUtils.getSeededNotes
import utils.SerializerUtils.isArrayList
import utils.SerializerUtils.ldp
import java.io.File
import java.time.LocalDateTime
import kotlin.test.assertEquals

class NoteAPITest {

    private var learnKotlin: Note? = null
    private var summerHoliday: Note? = null
    private var codeApp: Note? = null
    private var testApp: Note? = null
    private var swim: Note? = null
    private var populatedNotes: NoteAPI? = NoteAPI(XMLSerializer(File("notes.test.xml")))
    private var emptyNotes: NoteAPI? = NoteAPI(XMLSerializer(File("notes.test.xml")))

    @BeforeEach
    fun setup() {
        learnKotlin = Note("Learning Kotlin", 5, "College", false)
        summerHoliday = Note("Summer Holiday to France", 1, "Holiday", false)
        codeApp = Note("Code App", 4, "Work", false)
        testApp = Note("Test App", 4, "Work", false)
        swim = Note("Swim - Pool", 3, "Hobby", false)

        //adding 5 Note to the notes api
        populatedNotes!!.add(learnKotlin!!)
        populatedNotes!!.add(summerHoliday!!)
        populatedNotes!!.add(codeApp!!)
        populatedNotes!!.add(testApp!!)
        populatedNotes!!.add(swim!!)
    }

    @AfterEach
    fun tearDown() {
        learnKotlin = null
        summerHoliday = null
        codeApp = null
        testApp = null
        swim = null
        populatedNotes = null
        emptyNotes = null

        File("notes.test.xml").delete()
        File("notes.test.json").delete()
        File("notes.test.yaml").delete()
    }

    @Nested
    inner class AddNotes {
        @Test
        fun `adding a Note to a populated list adds to ArrayList`() {
            val newNote = Note("Study Lambdas", 1, "College", false)
            assertEquals(5, populatedNotes!!.numberOfNotes())
            assertTrue(populatedNotes!!.add(newNote))
            assertEquals(6, populatedNotes!!.numberOfNotes())
            assertEquals(newNote, populatedNotes!!.findNote(populatedNotes!!.numberOfNotes() - 1))
        }

        @Test
        fun `adding a Note to an empty list adds to ArrayList`() {
            val newNote = Note("Study Lambdas", 1, "College", false)
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.add(newNote))
            assertEquals(1, emptyNotes!!.numberOfNotes())
            assertEquals(newNote, emptyNotes!!.findNote(emptyNotes!!.numberOfNotes() - 1))
        }
    }

    @Nested
    inner class GenerateTableMethods {
        @Test
        fun `generateAllNotesTable returns a table with the correct number of rows`() {
            val table = populatedNotes!!.generateAllNotesTable()
            assertEquals(5, table.body.rows.size)
        }

        @Test
        fun `generateAllNotesTable contains the correct note titles`() {
            val tableString = populatedNotes!!.generateAllNotesTable().renderText()
            assertTrue(tableString.contains("Learning Kotlin"))
            assertTrue(tableString.contains("Summer Holiday to France"))
            assertTrue(tableString.contains("Code App"))
            assertTrue(tableString.contains("Test App"))
            assertTrue(tableString.contains("Swim - Pool"))
        }

        @Test
        fun `generateNoteTable returns a table with the correct number of rows`() {
            val table = populatedNotes!!.generateNoteTable(learnKotlin!!)
            assertEquals(1, table.body.rows.size)
        }

        @Test
        fun `generateNoteTable contains the note title`() {
            val tableString = populatedNotes!!.generateNoteTable(learnKotlin!!).renderText()
            assertTrue(tableString.contains("Learning Kotlin"))
        }
    }

    @Nested
    inner class FindingMethods {
        @Test
        fun `findAll returns all notes in the ArrayList`() {
            val notes = populatedNotes!!.findAll()
            assertEquals(5, notes.size)
            assertEquals(learnKotlin, notes[0])
            assertEquals(summerHoliday, notes[1])
            assertEquals(codeApp, notes[2])
            assertEquals(testApp, notes[3])
            assertEquals(swim, notes[4])
        }

        @Test
        fun `findUsingNote returns the correct note`() {
            assertEquals(learnKotlin, populatedNotes!!.findUsingNote(learnKotlin!!))
            assertEquals(summerHoliday, populatedNotes!!.findUsingNote(summerHoliday!!))
            assertEquals(codeApp, populatedNotes!!.findUsingNote(codeApp!!))
            assertEquals(testApp, populatedNotes!!.findUsingNote(testApp!!))
            assertEquals(swim, populatedNotes!!.findUsingNote(swim!!))
        }

        @Test
        fun `findIndexUsingNote returns the correct index`() {
            assertEquals(0, populatedNotes!!.findIndexUsingNote(learnKotlin!!))
            assertEquals(1, populatedNotes!!.findIndexUsingNote(summerHoliday!!))
            assertEquals(2, populatedNotes!!.findIndexUsingNote(codeApp!!))
            assertEquals(3, populatedNotes!!.findIndexUsingNote(testApp!!))
            assertEquals(4, populatedNotes!!.findIndexUsingNote(swim!!))
        }
    }

    @Nested
    inner class ValidIndex {
        @Test
        fun `validIndex returns true when index is within range of ArrayList`() {
            assertTrue(populatedNotes!!.isValidIndex(0))
            assertTrue(populatedNotes!!.isValidIndex(4))
        }

        @Test
        fun `validIndex returns false when index is out of range of ArrayList`() {
            assertFalse(populatedNotes!!.isValidIndex(-1))
            assertFalse(populatedNotes!!.isValidIndex(5))
        }
    }

    @Nested
    inner class ListingMethods {

        @Test
        fun `listAllNotes returns No Notes Stored message when ArrayList is empty`() {
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.listAllNotes().lowercase().contains("no notes"))
        }

        @Test
        fun `listAllNotes returns Notes when ArrayList has notes stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            val notesString = populatedNotes!!.listAllNotes().lowercase()
            assertTrue(notesString.contains("learning kotlin"))
            assertTrue(notesString.contains("code app"))
            assertTrue(notesString.contains("test app"))
            assertTrue(notesString.contains("swim"))
            assertTrue(notesString.contains("summer holiday"))
        }

        @Test
        fun `listActiveNotes returns No Active Notes Stored message when ArrayList is empty`() {
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.listActiveNotes().lowercase().contains("no active notes"))
        }

        @Test
        fun `listActiveNotes returns No Active Notes Stored message when ArrayList has no active notes stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            populatedNotes!!.findNote(0)!!.isNoteArchived = true
            populatedNotes!!.findNote(1)!!.isNoteArchived = true
            populatedNotes!!.findNote(2)!!.isNoteArchived = true
            populatedNotes!!.findNote(3)!!.isNoteArchived = true
            populatedNotes!!.findNote(4)!!.isNoteArchived = true
            assertTrue(populatedNotes!!.listActiveNotes().lowercase().contains("no active notes"))
        }

        @Test
        fun `listActiveNotes returns Active Notes when ArrayList has active notes stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            val notesString = populatedNotes!!.listActiveNotes().lowercase()
            assertTrue(notesString.contains("learning kotlin"))
            assertTrue(notesString.contains("code app"))
            assertTrue(notesString.contains("test app"))
            assertTrue(notesString.contains("swim"))
            assertTrue(notesString.contains("summer holiday"))
        }

        @Test
        fun `listArchivedNotes returns No Archived Notes Stored message when ArrayList is empty`() {
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.listArchivedNotes().lowercase().contains("no archived notes"))
        }

        @Test
        fun `listArchivedNotes returns No Archived Notes Stored message when ArrayList has no archived notes stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            assertTrue(populatedNotes!!.listArchivedNotes().lowercase().contains("no archived notes"))
        }

        @Test
        fun `listArchivedNotes returns Archived Notes when ArrayList has archived notes stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            populatedNotes!!.findNote(0)!!.isNoteArchived = true
            populatedNotes!!.findNote(1)!!.isNoteArchived = true
            populatedNotes!!.findNote(2)!!.isNoteArchived = true
            populatedNotes!!.findNote(3)!!.isNoteArchived = true
            populatedNotes!!.findNote(4)!!.isNoteArchived = true
            val notesString = populatedNotes!!.listArchivedNotes().lowercase()
            assertTrue(notesString.contains("learning kotlin"))
            assertTrue(notesString.contains("code app"))
            assertTrue(notesString.contains("test app"))
            assertTrue(notesString.contains("swim"))
            assertTrue(notesString.contains("summer holiday"))
        }

        // Test for findNoteByPriority
        @Test
        fun `listNotesBySelectedPriority returns No Notes with Priority Stored message when ArrayList is empty`() {
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.listNotesBySelectedPriority(1).lowercase().contains("no notes with priority"))
        }

        @Test
        fun `listNotesBySelectedPriority returns No Notes with Priority Stored message when ArrayList has no notes with priority stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            assertTrue(populatedNotes!!.listNotesBySelectedPriority(2).lowercase().contains("no notes with priority"))
        }

        @Test
        fun `listNotesBySelectedPriority returns Notes with Priority when ArrayList has notes with priority stored`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            val notesString = populatedNotes!!.listNotesBySelectedPriority(4).lowercase()
            assertTrue(notesString.contains("code app"))
            assertTrue(notesString.contains("test app"))
        }
    }

    @Nested
    inner class DeleteNotes {

        @Test
        fun `deleting a Note that does not exist, returns null`() {
            assertNull(emptyNotes!!.deleteNote(0))
            assertNull(populatedNotes!!.deleteNote(-1))
            assertNull(populatedNotes!!.deleteNote(5))
        }

        @Test
        fun `deleting a note that exists delete and returns deleted object`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            assertEquals(swim, populatedNotes!!.deleteNote(4))
            assertEquals(4, populatedNotes!!.numberOfNotes())
            assertEquals(learnKotlin, populatedNotes!!.deleteNote(0))
            assertEquals(3, populatedNotes!!.numberOfNotes())
        }

        // Test for `removeMultipleNotes(noteList: List<Note>)`
        @Test
        fun `removeMultipleNotes returns false when noteList is empty`() {
            assertFalse(emptyNotes!!.removeMultipleNotes(emptyList()))
        }

        @Test
        fun `removeMultipleNotes removes notes from the ArrayList`() {
            populatedNotes!!.removeMultipleNotes(listOf(swim!!, codeApp!!))
            assertEquals(3, populatedNotes!!.numberOfNotes())
            val notesString = populatedNotes!!.listAllNotes().lowercase()
            assertFalse(notesString.contains("swim"))
            assertFalse(notesString.contains("code app"))
            assertTrue(notesString.contains("learning kotlin"))
            assertTrue(notesString.contains("test app"))
            assertTrue(notesString.contains("summer holiday"))
        }
    }

    @Nested
    inner class UpdateNotes {
        @Test
        fun `updating a note that does not exist returns false`() {
            assertFalse(populatedNotes!!.updateNote(6, Note("Updating Note", 2, "Work", false)))
            assertFalse(populatedNotes!!.updateNote(-1, Note("Updating Note", 2, "Work", false)))
            assertFalse(emptyNotes!!.updateNote(0, Note("Updating Note", 2, "Work", false)))
        }

        @Test
        fun `updating a note that exists returns true and updates`() {
            //check note 5 exists and check the contents
            assertEquals(swim, populatedNotes!!.findNote(4))
            assertEquals("Swim - Pool", populatedNotes!!.findNote(4)!!.noteTitle)
            assertEquals(3, populatedNotes!!.findNote(4)!!.notePriority)
            assertEquals("Hobby", populatedNotes!!.findNote(4)!!.noteCategory)

            //update note 5 with new information and ensure contents updated successfully
            assertTrue(populatedNotes!!.updateNote(4, Note("Updating Note", 2, "College", false)))
            assertEquals("Updating Note", populatedNotes!!.findNote(4)!!.noteTitle)
            assertEquals(2, populatedNotes!!.findNote(4)!!.notePriority)
            assertEquals("College", populatedNotes!!.findNote(4)!!.noteCategory)
        }
    }

    @Nested
    inner class PersistenceTests {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            // Saving an empty notes.XML file.
            val storingNotes = NoteAPI(XMLSerializer(File("notes.test.xml")))
            storingNotes.store()

            //Loading the empty notes.test.xml file into a new object
            val loadedNotes = NoteAPI(XMLSerializer(File("notes.test.xml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving and loading an loaded collection in XML doesn't loose data`() {
            // Storing 3 notes to the notes.XML file.
            val storingNotes = NoteAPI(XMLSerializer(File("notes.test.xml")))
            storingNotes.add(testApp!!)
            storingNotes.add(swim!!)
            storingNotes.add(summerHoliday!!)
            storingNotes.store()

            //Loading notes.test.xml into a different collection
            val loadedNotes = NoteAPI(XMLSerializer(File("notes.test.xml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNote(0), loadedNotes.findNote(0))
            assertEquals(storingNotes.findNote(1), loadedNotes.findNote(1))
            assertEquals(storingNotes.findNote(2), loadedNotes.findNote(2))
        }

        @Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
            // Saving an empty notes.test.json file.
            val storingNotes = NoteAPI(JSONSerializer(File("notes.test.json")))
            storingNotes.store()

            //Loading the empty notes.test.json file into a new object
            val loadedNotes = NoteAPI(JSONSerializer(File("notes.test.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the json loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving and loading an loaded collection in JSON doesn't loose data`() {
            // Storing 3 notes to the notes.test.json file.
            val storingNotes = NoteAPI(JSONSerializer(File("notes.test.json")))
            storingNotes.add(testApp!!)
            storingNotes.add(swim!!)
            storingNotes.add(summerHoliday!!)
            storingNotes.store()

            //Loading notes.test.json into a different collection
            val loadedNotes = NoteAPI(JSONSerializer(File("notes.test.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the json loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNote(0), loadedNotes.findNote(0))
            assertEquals(storingNotes.findNote(1), loadedNotes.findNote(1))
            assertEquals(storingNotes.findNote(2), loadedNotes.findNote(2))
        }

        @Test
        fun `saving and loading an empty collection in YAML doesn't crash app`() {
            // Saving an empty notes.test.yaml file.
            val storingNotes = NoteAPI(YAMLSerializer(File("notes.test.yaml")))
            storingNotes.store()

            //Loading the empty notes.test.yaml file into a new object
            val loadedNotes = NoteAPI(YAMLSerializer(File("notes.test.yaml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the yaml loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving and loading an loaded collection in YAML doesn't loose data`() {
            // Storing 3 notes to the notes.test.yaml file.
            val storingNotes = NoteAPI(YAMLSerializer(File("notes.test.yaml")))
            storingNotes.add(testApp!!)
            storingNotes.add(swim!!)
            storingNotes.add(summerHoliday!!)
            storingNotes.store()

            //Loading notes.test.yaml into a different collection
            val loadedNotes = NoteAPI(YAMLSerializer(File("notes.test.yaml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the yaml loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNote(0), loadedNotes.findNote(0))
            assertEquals(storingNotes.findNote(1), loadedNotes.findNote(1))
            assertEquals(storingNotes.findNote(2), loadedNotes.findNote(2))
        }

        @Nested
        inner class SerializerUtilsTests {

            @Test
            fun `getSeededNotes() returns a list of 11 notes`() {
                val seededNotes = getSeededNotes()
                assertEquals(11, seededNotes.size)
                assertEquals(11, seededNotes.distinct().size)
            }

            @Test
            fun `isArrayList() returns null if obj is not an ArrayList`() {
                assertNull(isArrayList("Hello"))
                assertNull(isArrayList(1))
                assertNull(isArrayList(1.0))
                assertNull(isArrayList(true))
                assertNull(isArrayList(false))
                assertNull(isArrayList(testApp!!))
                assertNull(isArrayList(swim!!))
                assertNull(isArrayList(summerHoliday!!))
                assertNull(isArrayList(emptyNotes!!))
                assertNull(isArrayList(populatedNotes!!))
            }

            @Test
            fun `isArrayList() returns an ArrayList if obj is an ArrayList`() {
                val arrayList = ArrayList<Note>()
                arrayList.add(testApp!!)
                arrayList.add(swim!!)
                arrayList.add(summerHoliday!!)
                assertEquals(arrayList, isArrayList(arrayList))
            }

            @Test
            fun `ldp() parses LocalDateTime correctly`() {
                val date = LocalDateTime.of(2020, 1, 1, 1, 1)
                assertEquals(date, ldp("2020-01-01T01:01"))
            }

            @Test
            fun `generateSeededFiles() generates 3 files`() {
                generateSeededFiles()

                assertTrue(File("notes.json").exists())
                assertTrue(File("notes.yaml").exists())
                assertTrue(File("notes.xml").exists())
            }
        }
    }

    @Nested
    inner class ArchiveTests {

        @Test
        fun `archiving a note that does not exist returns false`() {
            assertFalse(emptyNotes!!.archiveNote(0))
            assertFalse(populatedNotes!!.archiveNote(-1))
            assertFalse(populatedNotes!!.archiveNote(5))
        }

        @Test
        fun `archiving a note that exists returns true and archives`() {
            //check note 5 exists and check the contents
            assertEquals(swim, populatedNotes!!.findNote(4))
            assertEquals("Swim - Pool", populatedNotes!!.findNote(4)!!.noteTitle)
            assertEquals(3, populatedNotes!!.findNote(4)!!.notePriority)
            assertEquals("Hobby", populatedNotes!!.findNote(4)!!.noteCategory)
            assertFalse(populatedNotes!!.findNote(4)!!.isNoteArchived)

            //archive note 5 and ensure contents updated successfully
            assertTrue(populatedNotes!!.archiveNote(4))
            assertEquals("Swim - Pool", populatedNotes!!.findNote(4)!!.noteTitle)
            assertEquals(3, populatedNotes!!.findNote(4)!!.notePriority)
            assertEquals("Hobby", populatedNotes!!.findNote(4)!!.noteCategory)
            assertTrue(populatedNotes!!.findNote(4)!!.isNoteArchived)
        }
    }

    @Nested
    inner class CountingMethods {

        @Test
        fun numberOfNotesCalculatedCorrectly() {
            assertEquals(5, populatedNotes!!.numberOfNotes())
            assertEquals(0, emptyNotes!!.numberOfNotes())
        }

        @Test
        fun numberOfArchivedNotesCalculatedCorrectly() {
            assertEquals(0, populatedNotes!!.numberOfArchivedNotes())
            assertEquals(0, emptyNotes!!.numberOfArchivedNotes())
        }

        @Test
        fun numberOfActiveNotesCalculatedCorrectly() {
            assertEquals(5, populatedNotes!!.numberOfActiveNotes())
            assertEquals(0, emptyNotes!!.numberOfActiveNotes())
        }

        @Test
        fun numberOfNotesByPriorityCalculatedCorrectly() {
            assertEquals(1, populatedNotes!!.numberOfNotesByPriority(1))
            assertEquals(0, populatedNotes!!.numberOfNotesByPriority(2))
            assertEquals(1, populatedNotes!!.numberOfNotesByPriority(3))
            assertEquals(2, populatedNotes!!.numberOfNotesByPriority(4))
            assertEquals(1, populatedNotes!!.numberOfNotesByPriority(5))
            assertEquals(0, emptyNotes!!.numberOfNotesByPriority(1))
        }
    }

    @Nested
    inner class SearchMethods {

        @Test
        fun `search notes by title returns no notes when no notes with that title exist`() {
            //Searching a populated collection for a title that doesn't exist.
            assertEquals(5, populatedNotes!!.numberOfNotes())
            val searchResults = populatedNotes!!.searchByTitle("no results expected")
            assertTrue(searchResults.isEmpty())

            //Searching an empty collection
            assertEquals(0, emptyNotes!!.numberOfNotes())
            assertTrue(emptyNotes!!.searchByTitle("").isEmpty())
        }

        @Test
        fun `search notes by title returns notes when notes with that title exist`() {
            assertEquals(5, populatedNotes!!.numberOfNotes())

            //Searching a populated collection for a full title that exists (case matches exactly)
            var searchResults = populatedNotes!!.searchByTitle("Code App")
            assertTrue(searchResults.contains("Code App"))
            assertFalse(searchResults.contains("Test App"))

            //Searching a populated collection for a partial title that exists (case matches exactly)
            searchResults = populatedNotes!!.searchByTitle("App")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))

            //Searching a populated collection for a partial title that exists (case doesn't match)
            searchResults = populatedNotes!!.searchByTitle("aPp")
            assertTrue(searchResults.contains("Code App"))
            assertTrue(searchResults.contains("Test App"))
            assertFalse(searchResults.contains("Swim - Pool"))
        }
    }


}
