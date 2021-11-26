package rxcats.chatservice.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.ChatMessageResponse
import rxcats.chatservice.domain.RoomInfo
import rxcats.chatservice.domain.SystemMessageResponse
import rxcats.chatservice.domain.SystemMessageType
import rxcats.chatservice.extensions.getUser
import rxcats.chatservice.extensions.getUserOrThrow
import rxcats.chatservice.extensions.send
import rxcats.chatservice.ws.UserProfile
import java.util.concurrent.ConcurrentHashMap

@Service
class LobbyService {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun getOnlineUsers(): List<UserProfile> {
        return sessions.filter { it.value.isOpen }
            .values
            .map {
                it.getUserOrThrow()
            }
    }

    fun join(session: WebSocketSession) {
        sessions[session.getUserOrThrow().userId] = session
    }

    fun leave(session: WebSocketSession) {
        val k = session.getUserOrThrow().userId

        if (!sessions.containsKey(k)) {
            return
        }

        sessions.remove(k)
    }

    fun disconnect(session: WebSocketSession) {
        val user = session.getUser() ?: return
        if (sessions.containsKey(user.userId)) {
            sessions.remove(user.userId)
        }

        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.Disconnected,
                data = user
            )
        )
    }

    fun broadcast(session: WebSocketSession, message: String) {
        sessions.values.forEach {
            it.send(
                SystemMessageResponse(
                    messageType = SystemMessageType.LobbyChat,
                    data = ChatMessageResponse(
                        userProfile = session.getUserOrThrow(),
                        message = message
                    )
                )
            )
        }
    }

    fun <T : Any> systemBroadcast(response: SystemMessageResponse<T>) {
        sessions.filter { it.value.isOpen }
            .values
            .forEach { it.send(response) }
    }

    fun sendLeaveLobbyBroadcastMessages(session: WebSocketSession, rooms: List<RoomInfo>) {
        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.LeaveLobby,
                data = session.getUserOrThrow()
            )
        )

        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyUsers,
                data = getOnlineUsers()
            )
        )

        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyRooms,
                data = rooms
            )
        )
    }

    fun sendJoinLobbyBroadcastMessages(session: WebSocketSession, rooms: List<RoomInfo>) {
        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.JoinLobby,
                data = session.getUserOrThrow()
            )
        )

        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyUsers,
                data = getOnlineUsers()
            )
        )

        systemBroadcast(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyRooms,
                data = rooms
            )
        )
    }

}