package rxcats.chatservice.domain

import rxcats.chatservice.ws.UserProfile
import java.time.ZonedDateTime

data class LoginRequest(
    val userId: String,
    val username: String,
    val userColor: String
)

data class ChatMessageRequest(
    val message: String
)

data class ChatMessageResponse(
    val userProfile: UserProfile,
    val message: String
)

data class CreateRoomRequest(
    val roomTitle: String,
    val roomDescription: String
)

data class JoinRoomRequest(
    val roomId: Int
)

data class RoomInfo(
    val roomId: Int,
    val roomTitle: String,
    val roomDescription: String = ""
)