package controllers

import models.Note
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import persistence.JSONSerializer
import persistence.XMLSerializer
import java.io.File
import kotlin.test.assertEquals

class NoteAPITest {

    private var learnKotlin: Note? = null
    private var summerHoliday: Note? = null
    private var codeApp: Note? = null
    private var testApp: Note? = null
    private var swim: Note? = null
    private var populatedNotes: NoteAPI? = NoteAPI(XMLSerializer(File("notes.xml")))
    private var emptyNotes: NoteAPI? = NoteAPI(XMLSerializer(File("notes.xml")))

    @BeforeEach
    fun setup(){
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
    fun tearDown(){
        learnKotlin = null
        summerHoliday = null
        codeApp = null
        testApp = null
        swim = null
        populatedNotes = null
        emptyNotes = null
    }

    @Test
    fun `adding a Note to a populated list adds to ArrayList`(){
        val newNote = Note("Study Lambdas", 1, "College", false)
        assertEquals(5, populatedNotes!!.numberOfNotes())
        assertTrue(populatedNotes!!.add(newNote))
        assertEquals(6, populatedNotes!!.numberOfNotes())
        assertEquals(newNote, populatedNotes!!.findNote(populatedNotes!!.numberOfNotes() - 1))
    }

    @Test
    fun `adding a Note to an empty list adds to ArrayList`(){
        val newNote = Note("Study Lambdas", 1, "College", false)
        assertEquals(0, emptyNotes!!.numberOfNotes())
        assertTrue(emptyNotes!!.add(newNote))
        assertEquals(1, emptyNotes!!.numberOfNotes())
        assertEquals(newNote, emptyNotes!!.findNote(emptyNotes!!.numberOfNotes() - 1))
    }

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
    }

    @Nested
    inner class UpdateNotes {
        @Test
        fun `updating a note that does not exist returns false`(){
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
            val storingNotes = NoteAPI(XMLSerializer(File("notes.xml")))
            storingNotes.store()

            //Loading the empty notes.xml file into a new object
            val loadedNotes = NoteAPI(XMLSerializer(File("notes.xml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving and loading an loaded collection in XML doesn't loose data`() {
            // Storing 3 notes to the notes.XML file.
            val storingNotes = NoteAPI(XMLSerializer(File("notes.xml")))
            storingNotes.add(testApp!!)
            storingNotes.add(swim!!)
            storingNotes.add(summerHoliday!!)
            storingNotes.store()

            //Loading notes.xml into a different collection
            val loadedNotes = NoteAPI(XMLSerializer(File("notes.xml")))
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
            // Saving an empty notes.json file.
            val storingNotes = NoteAPI(JSONSerializer(File("notes.json")))
            storingNotes.store()

            //Loading the empty notes.json file into a new object
            val loadedNotes = NoteAPI(JSONSerializer(File("notes.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the json loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving and loading an loaded collection in JSON doesn't loose data`() {
            // Storing 3 notes to the notes.json file.
            val storingNotes = NoteAPI(JSONSerializer(File("notes.json")))
            storingNotes.add(testApp!!)
            storingNotes.add(swim!!)
            storingNotes.add(summerHoliday!!)
            storingNotes.store()

            //Loading notes.json into a different collection
            val loadedNotes = NoteAPI(JSONSerializer(File("notes.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the json loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNote(0), loadedNotes.findNote(0))
            assertEquals(storingNotes.findNote(1), loadedNotes.findNote(1))
            assertEquals(storingNotes.findNote(2), loadedNotes.findNote(2))
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
}
