package rxcats.chatservice.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import rxcats.chatservice.ws.UserProfile

@JsonAutoDetect
data class LoginRequest(
    val userId: String,
    val username: String,
    val userColor: String
)

@JsonAutoDetect
data class ChatMessageRequest(
    val message: String
)

@JsonAutoDetect
data class ChatMessageResponse(
    val userProfile: UserProfile,
    val message: String
)

@JsonAutoDetect
data class CreateRoomRequest(
    val roomTitle: String,
    val roomDescription: String
)

@JsonAutoDetect
data class JoinRoomRequest(
    val roomId: Int
)

@JsonAutoDetect
data class RoomInfo(
    val roomId: Int,
    val roomTitle: String,
    val roomDescription: String = ""
)