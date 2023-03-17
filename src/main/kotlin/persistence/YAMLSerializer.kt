package persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import models.Note
import utils.SerializerUtils.isArrayList
import java.io.File


// https://www.mkammerer.de/blog/kotlin-and-yaml-part-2/
class YAMLSerializer(private val file: File) : Serializer {

    @Throws(Exception::class)
    override fun read(): ArrayList<Note>? {
        val mapper: ObjectMapper = YAMLMapper()
        mapper.registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        ).registerModule(JavaTimeModule())

        val obj = mapper.readValue(file, object : TypeReference<ArrayList<Note?>?>() {})!!

        return isArrayList(obj)
    }


    @Throws(Exception::class)
    override fun write(obj: ArrayList<Note>) {
        val mapper: ObjectMapper = YAMLMapper()
        mapper.registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        ).registerModule(JavaTimeModule())

        mapper.writeValue(file, obj)
    }
}