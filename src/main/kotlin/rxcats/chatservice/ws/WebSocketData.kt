package rxcats.chatservice.ws

import com.fasterxml.jackson.annotation.JsonValue
import java.lang.reflect.Method
import java.time.LocalDateTime

data class FridayCommandEntity(val bean: Any, val method: Method)

data class UserSession(
    val userId: String,
    val username: String,
    val userColor: String,
    val loginDateTime: LocalDateTime = LocalDateTime.now(),
)

enum class ResultCode(@JsonValue val code: Int) {
    OK(0),
    ERROR(10000)
}

data class WebSocketResponse<T>(
    val code: ResultCode = ResultCode.OK,
    val now: LocalDateTime = LocalDateTime.now(),
    val uri: String = "",
    val body: T? = null
)