package rxcats.chatservice.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.MessageResponse
import rxcats.chatservice.util.Json
import rxcats.chatservice.ws.UserSession
import rxcats.chatservice.ws.WebSocketResponse
import java.util.concurrent.ConcurrentHashMap

@Service
class SessionService {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun put(userId: String, session: WebSocketSession) {
        sessions[userId] = session
    }

    fun remove(session: UserSession) {
        sessions.remove(session.userId)
    }

    fun remove(userId: String) {
        sessions.remove(userId)
    }

    fun join(uri: String,  message: String, session: WebSocketSession) {
        val userSession = session.attributes["userSession"] as UserSession
        if (!sessions.containsKey(userSession.userId)) {
            put(userSession.userId, session)
            sendBroadcast(uri, message, userSession)
        }
    }

    fun leave(uri: String,  message: String, session: WebSocketSession) {
        val userSession = session.attributes["userSession"] as UserSession
        if (sessions.containsKey(userSession.userId)) {
            remove(userSession)
            sendBroadcast(uri, message, userSession)
        }
    }

    fun sendBroadcast(uri: String,  message: String, sender: UserSession) {
        for ((_, session) in sessions) {
            if (!session.isOpen) {
                continue
            }

            val response = WebSocketResponse(uri = uri, body = MessageResponse(username = sender.username, message = message, userColor = sender.userColor))
            session.sendMessage(TextMessage(Json.writeValueAsString(response)))
        }
    }

    @Scheduled(fixedDelay = 60_000)
    fun cleanClosedSession() {
        val userIds = mutableListOf<String>()

        for ((userId, session) in sessions) {
            if (!session.isOpen) {
                userIds.add(userId)
            }
        }

        for (userId in userIds) {
            remove(userId)
        }
    }

}