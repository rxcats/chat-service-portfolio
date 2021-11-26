package rxcats.chatservice.command

import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.*
import rxcats.chatservice.extensions.getRoom
import rxcats.chatservice.extensions.getUserOrThrow
import rxcats.chatservice.extensions.send
import rxcats.chatservice.service.LobbyService
import rxcats.chatservice.service.RoomService
import rxcats.chatservice.ws.FridayAction
import rxcats.chatservice.ws.FridayCommand
import rxcats.chatservice.ws.FridayRequestBody
import rxcats.chatservice.ws.FridaySession

@FridayCommand(prefix = "/room")
class RoomCommand(
    private val lobbyService: LobbyService,
    private val roomService: RoomService
) {

    @FridayAction("/create")
    fun create(@FridaySession session: WebSocketSession, @FridayRequestBody request: CreateRoomRequest) {
        val roomInfo = roomService.create(session, request.roomTitle, request.roomDescription)

        roomService.sendJoinRoomBroadcastMessages(session, roomInfo)

        lobbyService.leave(session)

        lobbyService.sendLeaveLobbyBroadcastMessages(session, roomService.rooms())

        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.JoinRoomSucceeded,
                data = session.getRoom()
            )
        )
    }

    @FridayAction("/join")
    fun join(
        @FridaySession session: WebSocketSession,
        @FridayRequestBody request: JoinRoomRequest
    ) {
        val roomInfo = roomService.join(session, request.roomId)

        roomService.sendJoinRoomBroadcastMessages(session, roomInfo)

        lobbyService.leave(session)

        lobbyService.sendLeaveLobbyBroadcastMessages(session, roomService.rooms())

        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.JoinRoomSucceeded,
                data = session.getRoom()
            )
        )
    }

    @FridayAction("/leave")
    fun leave(@FridaySession session: WebSocketSession) {
        val roomInfo = session.getRoom() ?: return

        roomService.leave(session, roomInfo.roomId)

        roomService.sendLeaveRoomBroadcastMessages(session, roomInfo)

        lobbyService.join(session)

        lobbyService.sendJoinLobbyBroadcastMessages(session, roomService.rooms())

        session.send(SystemMessageResponse<Any>(messageType = SystemMessageType.LeaveRoomSucceeded))
    }

    @FridayAction("/chat")
    fun chat(@FridaySession session: WebSocketSession, @FridayRequestBody request: ChatMessageRequest) {
        roomService.broadcast(session, request.message)
    }

    @FridayAction("/users")
    fun users(@FridaySession session: WebSocketSession) {
        val roomInfo = session.getRoom() ?: return
        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.RoomUsers,
                data = roomService.getRoomUsers(roomInfo.roomId)
            )
        )
    }

    @FridayAction("/rooms")
    fun rooms(@FridaySession session: WebSocketSession) {
        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyRooms,
                data = roomService.rooms()
            )
        )
    }

}