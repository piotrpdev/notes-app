package utils

import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.reflect.KClass

object ValidatorUtils {
    @JvmStatic
    fun stringIsValid(string: String?): Boolean = !string.isNullOrBlank()

    @JvmStatic
    fun intIsValid(int: String?): Boolean = stringIsValid(int) && int!!.toIntOrNull() != null

    @JvmStatic
    fun notePriorityIsValid(priority: String?): Boolean = intIsValid(priority) && priority!!.toInt() in 1..5

    @JvmStatic
    fun yesNoIsValid(isArchived: String?): Boolean = stringIsValid(isArchived) && isArchived!!.toCharArray()[0].lowercase() in arrayOf("y", "n")

    @JvmStatic
    fun localDateTimeIsValid(localDateTime: String?): Boolean = stringIsValid(localDateTime) && isValidLocalDateTime(localDateTime!!)

    @JvmStatic
    fun staleDaysIsValid(staleDays: String?): Boolean = intIsValid(staleDays) && staleDays!!.toInt() >= 0

    // https://stackoverflow.com/a/33968683/19020549
    @JvmStatic
    fun isValidLocalDateTime(inDate: String): Boolean {
        try {
            LocalDateTime.parse(inDate)
        } catch (pe: DateTimeParseException) {
            return false
        }
        return true
    }

    // private fun formatPropertyName(propertyName: String): String = propertyName.split("(?=\\p{Upper})").joinToString(" ").lowercase()

    @JvmStatic
    private fun def(prop: Any?) = if (prop != null) " (${prop})" else ""

    @JvmStatic
    fun propertyNameToPrompt(propertyName: String, oldPropertyValue: Any?): String {
        return when (propertyName) {
            "noteTitle" -> "Enter note title${def(oldPropertyValue)}: "
            "notePriority" -> "Enter note priority (1-low, 2, 3, 4, 5-high)${def(oldPropertyValue)}: "
            "noteCategory" -> "Enter note category${def(oldPropertyValue)}: "
            "isNoteArchived" -> "Enter note archived status (y/n)${def(oldPropertyValue)}: "
            "updatedAt" -> "Enter note updated at (e.g. 2023-03-09T11:30:00)${def(oldPropertyValue)}: "
            "createdAt" -> "Enter note created at (e.g. 2023-03-09T11:30:00)${def(oldPropertyValue)}: "
            "staleDays" -> "Show notes that haven't been updated in this many days: "
            "noteIndex" -> "Enter note index: "
            else -> throw IllegalArgumentException("Invalid property name: $propertyName")
        }
    }

    @JvmStatic
    fun propertyNameToError(propertyName: String): String {
        return when (propertyName) {
            "noteTitle" -> "Error: note title was invalid. Please enter a string"
            "notePriority" -> "Error: note priority was invalid. Please enter an integer between 1 and 5"
            "noteCategory" -> "Error: note category was invalid. Please enter a string"
            "isNoteArchived" -> "Error: note archived status was invalid. Please enter either 'y' or 'n'"
            "updatedAt" -> "Error: note updated at was invalid. Please enter a valid date and time (e.g. 2023-03-09T11:30:00)"
            "createdAt" -> "Error: note created at was invalid. Please enter a valid date and time (e.g. 2023-03-09T11:30:00)"
            "staleDays" -> "Error: invalid number of days. Please enter a valid positive integer."
            "noteIndex" -> "Error: invalid note index. Please enter a valid positive integer."
            "yesNo" -> "Error: invalid input. Please enter either 'y' or 'n'."
            else -> throw IllegalArgumentException("Invalid property name: $propertyName")
        }
    }

    @JvmStatic
    fun propertyNameToValidator(propertyName: String): (String?) -> Boolean {
        return when (propertyName) {
            "noteTitle" -> ::stringIsValid
            "notePriority" -> ::notePriorityIsValid
            "noteCategory" -> ::stringIsValid
            "isNoteArchived" -> ::yesNoIsValid
            "updatedAt" -> ::localDateTimeIsValid
            "createdAt" -> ::localDateTimeIsValid
            "staleDays" -> ::staleDaysIsValid
            "yesNo" -> ::yesNoIsValid
            else -> throw IllegalArgumentException("Invalid property name: $propertyName")
        }
    }

    fun <T : Any> stringToGeneric(string: String?, type: KClass<T>): T {
        return when (type) {
            String::class -> string as T
            Int::class -> string!!.toInt() as T
            Boolean::class -> (string!!.toCharArray()[0].lowercase() == "y") as T
            LocalDateTime::class -> LocalDateTime.parse(string) as T
            else -> throw IllegalArgumentException("Invalid property type: ${type::class}")
        }
    }

    inline fun <reified T : Any> getValidPropertyValue(
        propertyName: String,
        oldPropertyValue: T? = null,
        noinline customValidator: ((String?) -> Boolean)? = null,
        customPrompt: String? = null,
        customError: String? = null
    ): T {
        val isValid = customValidator ?: propertyNameToValidator(propertyName)

        // https://stackoverflow.com/a/3752693/19020549
        print(customPrompt ?: propertyNameToPrompt(propertyName, oldPropertyValue))

        var input: String? = readlnOrNull()

        while (!isValid(input)) {
            input = if (oldPropertyValue != null) {
                oldPropertyValue.toString()
            } else {
                println(customError ?: propertyNameToError(propertyName))
                readlnOrNull()
            }
        }

        return stringToGeneric(input, T::class)
    }
}