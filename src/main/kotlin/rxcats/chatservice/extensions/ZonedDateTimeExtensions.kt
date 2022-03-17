package rxcats.chatservice.extensions

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.toIso8601(): String =
    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))