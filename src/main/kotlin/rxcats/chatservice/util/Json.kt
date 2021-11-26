package rxcats.chatservice.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*

object Json {

    val objectMapper: ObjectMapper by lazy {
        jsonMapper {
            addModule(
                KotlinModule.Builder()
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    .build()
            )
            addModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun <T> decode(json: String, type: Class<T>): T {
        return objectMapper.readValue(json, type)
    }

    inline fun <reified T> decode(json: String): T {
        return objectMapper.readValue(json)
    }

    fun <T> convert(from: Any, type: Class<T>): T {
        return objectMapper.convertValue(from, type)
    }

    inline fun <reified T> convert(from: Any): T {
        return objectMapper.convertValue(from)
    }

    fun encode(o: Any): String {
        return objectMapper.writeValueAsString(o)
    }
}