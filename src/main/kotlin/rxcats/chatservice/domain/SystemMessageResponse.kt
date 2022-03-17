package rxcats.chatservice.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import rxcats.chatservice.extensions.toIso8601
import java.time.ZonedDateTime

@JsonAutoDetect
data class SystemMessageResponse<T : Any>(
    val messageType: SystemMessageType,
    val error: String? = null,
    val data: T? = null,
    val now: String = ZonedDateTime.now().toIso8601()
)