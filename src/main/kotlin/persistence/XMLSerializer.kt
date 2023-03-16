package persistence

import java.io.File
import kotlin.Throws
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import models.Note
import utils.SerializerUtils
import utils.SerializerUtils.isArrayList
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class XMLSerializer(private val file: File) : Serializer {

    @Throws(Exception::class)
    override fun read(): ArrayList<Note>? {
        val xStream = XStream(DomDriver())
        xStream.allowTypes(arrayOf(Note::class.java))
        val obj = xStream.createObjectInputStream(FileReader(file)).use {
            it.readObject() as Any
        }

        return isArrayList(obj)
    }


    @Throws(Exception::class)
    override fun write(obj: ArrayList<Note>) {
        val xStream = XStream(DomDriver())

        xStream.createObjectOutputStream(FileWriter(file)).use {
            it.writeObject(obj)
        }
    }
}
