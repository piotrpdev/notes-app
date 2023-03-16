package persistence

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver
import models.Note
import utils.SerializerUtils.isArrayList
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JSONSerializer(private val file: File) : Serializer {
    @Throws(Exception::class)
    override fun read(): ArrayList<Note>? {
        val xStream = XStream(JettisonMappedXmlDriver())
        xStream.allowTypes(arrayOf(Note::class.java))
        val obj = xStream.createObjectInputStream(FileReader(file)).use {
            it.readObject() as Any
        }

        return isArrayList(obj)
    }

    @Throws(Exception::class)
    override fun write(obj: ArrayList<Note>) {
        val xStream = XStream(JettisonMappedXmlDriver())

        xStream.createObjectOutputStream(FileWriter(file)).use {
            it.writeObject(obj)
        }
    }
}
