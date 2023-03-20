package utils

import controllers.NoteAPI
import models.Note
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
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

class SerializerUtilsTest {

    private var learnKotlin: Note? = null
    private var summerHoliday: Note? = null
    private var codeApp: Note? = null
    private var testApp: Note? = null
    private var swim: Note? = null
    private var populatedNotes: NoteAPI? = null
    private var emptyNotes: NoteAPI? = null

    @BeforeEach
    fun buildUp() {
        learnKotlin = TestUtils.learnKotlin()
        summerHoliday = TestUtils.summerHoliday()
        codeApp = TestUtils.codeApp()
        testApp = TestUtils.testApp()
        swim = TestUtils.swim()
        populatedNotes = TestUtils.populatedNotes()
        emptyNotes = TestUtils.emptyNotes()

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
                Assertions.assertEquals(11, seededNotes.size)
                Assertions.assertEquals(11, seededNotes.distinct().size)
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
                Assertions.assertEquals(arrayList, isArrayList(arrayList))
            }

            @Test
            fun `ldp() parses LocalDateTime correctly`() {
                val date = LocalDateTime.of(2020, 1, 1, 1, 1)
                Assertions.assertEquals(date, ldp("2020-01-01T01:01"))
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
}
