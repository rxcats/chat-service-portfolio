package rxcats.chatservice.command

import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.ChatMessageRequest
import rxcats.chatservice.domain.SystemMessageResponse
import rxcats.chatservice.domain.SystemMessageType
import rxcats.chatservice.extensions.getUserOrThrow
import rxcats.chatservice.extensions.send
import rxcats.chatservice.service.LobbyService
import rxcats.chatservice.service.RoomService
import rxcats.chatservice.ws.FridayAction
import rxcats.chatservice.ws.FridayCommand
import rxcats.chatservice.ws.FridayRequestBody
import rxcats.chatservice.ws.FridaySession

@FridayCommand(prefix = "/lobby")
class LobbyCommand(
    private val lobbyService: LobbyService,
    private val roomService: RoomService
) {

    @FridayAction("/join")
    fun join(@FridaySession session: WebSocketSession) {
        lobbyService.join(session)

        lobbyService.sendJoinLobbyBroadcastMessages(session, roomService.rooms())
    }

    @FridayAction("/users")
    fun users(@FridaySession session: WebSocketSession) {
        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.LobbyUsers,
                data = lobbyService.getOnlineUsers()
            )
        )
    }

    @FridayAction("/chat")
    fun chat(@FridaySession session: WebSocketSession, @FridayRequestBody request: ChatMessageRequest) {
        lobbyService.broadcast(session, request.message)
    }

}