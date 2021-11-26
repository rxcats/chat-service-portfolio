package rxcats.chatservice.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.*
import rxcats.chatservice.error.ServiceException
import rxcats.chatservice.extensions.*
import rxcats.chatservice.ws.UserProfile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class RoomService {

    private val sessions = ConcurrentHashMap<Int, ConcurrentHashMap<String, WebSocketSession>>()
    private val rooms = ConcurrentHashMap<Int, RoomInfo>()
    private val roomIdGenerator = AtomicInteger()

    fun rooms(): List<RoomInfo> {
        return rooms.values.toList()
    }

    fun getRoomUsers(roomId: Int): List<UserProfile> {
        return sessions[roomId]
            ?.values
            ?.map { it.getUserOrThrow() }
            ?: emptyList()
    }

    fun create(session: WebSocketSession, title: String, desc: String): RoomInfo {
        val roomInfo = RoomInfo(
            roomId = roomIdGenerator.incrementAndGet(),
            roomTitle = title,
            roomDescription = desc
        )

        if (sessions.containsKey(roomInfo.roomId)) {
            throw ServiceException("exists room")
        }

        sessions[roomInfo.roomId] = ConcurrentHashMap()
        sessions[roomInfo.roomId]!![session.getUserOrThrow().userId] = session

        rooms[roomInfo.roomId] = roomInfo

        session.setRoom(roomInfo)

        return roomInfo
    }

    fun join(session: WebSocketSession, roomId: Int): RoomInfo {
        if (!sessions.containsKey(roomId)) {
            throw ServiceException("cannot find room")
        }

        sessions[roomId]?.set(session.getUserOrThrow().userId, session)

        val roomInfo = rooms[roomId]!!

        session.setRoom(roomInfo)

        return roomInfo
    }

    fun leave(session: WebSocketSession, roomId: Int): RoomInfo {
        if (!sessions.containsKey(roomId) ||
            !sessions[roomId]!!.containsKey(session.getUserOrThrow().userId)
        ) {
            throw ServiceException("cannot find room")
        }

        sessions[roomId]!!.remove(session.getUserOrThrow().userId)

        val roomInfo = rooms[roomId]

        if (sessions[roomId]!!.isEmpty()) {
            sessions.remove(roomId)
            rooms.remove(roomId)
        }

        session.removeRoom()

        return roomInfo!!
    }

    fun disconnect(session: WebSocketSession) {
        val roomInfo = session.getRoom() ?: return

        if (sessions.containsKey(roomInfo.roomId)) {
            sessions[roomInfo.roomId]!!.remove(session.getUserOrThrow().userId)

            if (sessions[roomInfo.roomId]!!.isEmpty()) {
                sessions.remove(roomInfo.roomId)
                rooms.remove(roomInfo.roomId)
            } else {
                systemBroadcast(
                    roomInfo, SystemMessageResponse(
                        messageType = SystemMessageType.Disconnected,
                        data = session.getUser()
                    )
                )
            }
        }

        session.removeRoom()
    }

    fun broadcast(session: WebSocketSession, message: String) {
        val roomInfo = session.getRoom() ?: return

        val members = sessions[roomInfo.roomId]!!

        members.values.forEach {
            it.send(
                SystemMessageResponse(
                    messageType = SystemMessageType.RoomChat,
                    data = ChatMessageResponse(
                        userProfile = session.getUserOrThrow(),
                        message = message
                    )
                )
            )
        }
    }

    fun <T : Any> systemBroadcast(roomInfo: RoomInfo, response: SystemMessageResponse<T>) {
        if (sessions.containsKey(roomInfo.roomId)) {
            val members = sessions[roomInfo.roomId]!!
            members.values
                .filter { it.isOpen }
                .forEach { it.send(response) }
        }
    }

    fun sendJoinRoomBroadcastMessages(session: WebSocketSession, roomInfo: RoomInfo) {
        systemBroadcast(
            roomInfo, SystemMessageResponse(
                messageType = SystemMessageType.JoinRoom,
                data = session.getUserOrThrow()
            )
        )

        systemBroadcast(
            roomInfo, SystemMessageResponse(
                messageType = SystemMessageType.RoomUsers,
                data = getRoomUsers(roomInfo.roomId)
            )
        )
    }

    fun sendLeaveRoomBroadcastMessages(session: WebSocketSession, roomInfo: RoomInfo) {
        systemBroadcast(
            roomInfo, SystemMessageResponse(
                messageType = SystemMessageType.LeaveRoom,
                data = session.getUserOrThrow()
            )
        )

        systemBroadcast(
            roomInfo, SystemMessageResponse(
                messageType = SystemMessageType.RoomUsers,
                data = getRoomUsers(roomInfo.roomId)
            )
        )
    }

}