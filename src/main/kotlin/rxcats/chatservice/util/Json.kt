package rxcats.chatservice.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue

object Json {

    val objectMapper: ObjectMapper = jacksonMapperBuilder()
            .addModules(KotlinModule(nullIsSameAsDefault = true), JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

    fun <T : Any> readValue(json: String, type: Class<T>): T {
        return objectMapper.readValue(json, type)
    }

    inline fun <reified T> readValue(json: String): T {
        return objectMapper.readValue(json)
    }

    fun <T : Any> convertValue(json: Any, type: Class<T>): T {
        return objectMapper.convertValue(json, type)
    }

    inline fun <reified T> convertValue(data: Any): T {
        return objectMapper.convertValue(data)
    }

    fun writeValueAsString(obj: Any): String {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}