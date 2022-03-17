package rxcats.chatservice.ws

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonValue
import rxcats.chatservice.extensions.toIso8601
import java.lang.reflect.Method
import java.time.ZonedDateTime

data class FridayCommandEntity(val bean: Any, val method: Method)

@JsonAutoDetect
data class UserProfile(
    val userId: String,
    val username: String,
    val userColor: String,
    val loginDateTime: String = ZonedDateTime.now().toIso8601()
)

enum class ResultCode(@JsonValue val code: Int) {
    OK(0),
    ERROR(10000)
}
