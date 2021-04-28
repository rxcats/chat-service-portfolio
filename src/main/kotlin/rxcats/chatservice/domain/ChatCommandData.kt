package rxcats.chatservice.domain

data class LoginRequest(val userId: String, val username: String, val userColor: String)

data class MessageRequest(val message: String)

data class MessageResponse(val username: String, val message: String, val userColor: String)