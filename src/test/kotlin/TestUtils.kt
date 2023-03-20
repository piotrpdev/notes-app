import controllers.NoteAPI
import models.Note
import persistence.XMLSerializer
import utils.SerializerUtils
import utils.SerializerUtils.ldp
import java.io.File

object TestUtils {
    fun learnKotlin() = Note("Learning Kotlin", 5, "College", false, ldp("2023-03-10T10:00"), ldp("2023-03-10T10:00"))
    fun summerHoliday() = Note("Summer Holiday to France", 1, "Holiday", false, ldp("2023-03-10T20:00"), ldp("2023-03-10T19:30"))
    fun codeApp() = Note("Code App", 4, "Work", false, ldp("2023-03-08T12:15"), ldp("2023-03-08T12:00"))
    fun testApp() = Note("Test App", 4, "Work", false, ldp("2023-03-12T15:30"), ldp("2023-03-11T13:00"))
    fun swim() = Note("Swim - Pool", 3, "Hobby", false, ldp("2023-03-11T14:00"), ldp("2023-03-11T13:45"))
    fun populatedNotes() = NoteAPI(XMLSerializer(File("notes.test.xml")))
    fun emptyNotes() = NoteAPI(XMLSerializer(File("notes.test.xml")))
}