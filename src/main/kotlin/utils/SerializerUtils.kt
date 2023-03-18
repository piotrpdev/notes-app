package utils

import controllers.NoteAPI
import models.Note
import persistence.JSONSerializer
import persistence.XMLSerializer
import persistence.YAMLSerializer
import java.io.File
import java.time.LocalDateTime

/**
 * An object containing utility functions for serialization and deserialization of notes, as well as generating seed data.
 */
object SerializerUtils {
    /**
     * Parses a string into a LocalDateTime object.
     *
     * @param s The string to be parsed.
     * @return The LocalDateTime object parsed from the string.
     */
    fun ldp(s: String): LocalDateTime = LocalDateTime.parse(s)

    /**
     * Checks if the given object is an ArrayList of Note objects.
     *
     * @param obj The object to be checked.
     * @return The ArrayList of Note objects if the given object is of the correct type, null otherwise.
     */
    @JvmStatic
    fun isArrayList(obj: Any): ArrayList<Note>? = if (obj is ArrayList<*> && obj.all { it is Note }) {
        @Suppress("UNCHECKED_CAST")
        obj as ArrayList<Note>
    } else {
        null
    }

    /**
     * Returns an ArrayList of Note objects containing seeded data.
     *
     * @return The ArrayList of seeded Note objects.
     */
    @JvmStatic
    fun getSeededNotes(): ArrayList<Note> {
        val notes = ArrayList<Note>()

        notes.add(Note("Grocery list", 3, "Shopping", false, ldp("2023-03-10T10:00"), ldp("2023-03-10T10:00")))
        notes.add(Note("Meeting agenda", 1, "Work", false, ldp("2023-03-12T15:30"), ldp("2023-03-11T13:00")))
        notes.add(Note("Birthday party ideas", 2, "Personal", false, ldp("2023-03-14T18:45"), ldp("2023-03-14T17:30")))
        notes.add(Note("Workout plan", 4, "Health", false, ldp("2023-03-09T11:30"), ldp("2023-03-09T11:00")))
        notes.add(Note("Books to read", 5, "Entertainment", true, ldp("2023-03-08T12:15"), ldp("2023-03-08T12:00")))
        notes.add(Note("Travel itinerary", 1, "Travel", false, ldp("2023-03-10T20:00"), ldp("2023-03-10T19:30")))
        notes.add(Note("Recipes to try", 3, "Cooking", true, ldp("2023-03-11T14:00"), ldp("2023-03-11T13:45")))
        notes.add(Note("Project milestones", 1, "Work", false, LocalDateTime.parse("2023-03-15T16:00"), LocalDateTime.parse("2023-03-15T14:30")))
        notes.add(Note("Weekend trip packing list", 2, "Travel", false, LocalDateTime.parse("2023-03-12T17:15"), LocalDateTime.parse("2023-03-12T16:45")))
        notes.add(Note("Car maintenance schedule", 4, "Personal", false, LocalDateTime.parse("2023-03-14T10:30"), LocalDateTime.parse("2023-03-14T10:00")))
        notes.add(Note("Movies to watch", 5, "Entertainment", true, LocalDateTime.parse("2023-03-10T22:00"), LocalDateTime.parse("2023-03-10T21:30")))

        return notes
    }

    /**
     * Generates and stores seed data as XML, JSON, and YAML files.
     */
    @JvmStatic
    fun generateSeededFiles() {
        val noteAPIs = ArrayList<NoteAPI>()

        noteAPIs.add(NoteAPI(XMLSerializer(File("notes.xml"))))
        noteAPIs.add(NoteAPI(JSONSerializer(File("notes.json"))))
        noteAPIs.add(NoteAPI(YAMLSerializer(File("notes.yaml"))))

        noteAPIs.forEach { it.seedNotes(); it.store() }
    }
}