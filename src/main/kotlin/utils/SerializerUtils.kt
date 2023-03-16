package utils

import models.Note

object SerializerUtils {
    @JvmStatic
    fun isArrayList(obj: Any): ArrayList<Note>? = if (obj is ArrayList<*> && obj.all { it is Note }) {
        @Suppress("UNCHECKED_CAST")
        obj as ArrayList<Note>
    } else {
        null
    }
}