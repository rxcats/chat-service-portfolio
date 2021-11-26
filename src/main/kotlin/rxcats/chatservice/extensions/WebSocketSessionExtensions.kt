package rxcats.chatservice.extensions

import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.RoomInfo
import rxcats.chatservice.error.ServiceException
import rxcats.chatservice.util.Json
import rxcats.chatservice.ws.UserProfile

fun WebSocketSession.setUser(userId: String, username: String, userColor: String) {
    this.attributes["user"] = UserProfile(
        userId = userId,
        username = username,
        userColor = userColor
    )
}

fun WebSocketSession.getUserOrThrow(): UserProfile {
    return this.getUser()
        ?: throw ServiceException("please login first")
}

fun WebSocketSession.getUser(): UserProfile? {
    return this.attributes["user"] as? UserProfile
}

fun WebSocketSession.setRoom(roomInfo: RoomInfo) {
    this.attributes["room"] = roomInfo
}

fun WebSocketSession.removeRoom() {
    this.attributes.remove("room")
}

fun WebSocketSession.getRoom(): RoomInfo? {
    return this.attributes["room"] as? RoomInfo
}

fun WebSocketSession.send(message: Any) {
    this.sendMessage(TextMessage(Json.encode(message)))
}