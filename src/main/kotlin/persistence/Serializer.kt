package persistence

import models.Note

interface Serializer {
    @Throws(Exception::class)
    fun write(obj: ArrayList<Note>)

    @Throws(Exception::class)
    fun read(): ArrayList<Note>?
}
