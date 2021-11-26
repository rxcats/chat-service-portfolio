package rxcats.chatservice.domain

import java.time.ZonedDateTime

data class SystemMessageResponse<T : Any>(
    val messageType: SystemMessageType,
    val error: String? = null,
    val data: T? = null,
    val now: ZonedDateTime = ZonedDateTime.now()
)