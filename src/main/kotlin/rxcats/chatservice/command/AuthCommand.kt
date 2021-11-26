package rxcats.chatservice.command

import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.LoginRequest
import rxcats.chatservice.domain.SystemMessageResponse
import rxcats.chatservice.domain.SystemMessageType
import rxcats.chatservice.extensions.getUserOrThrow
import rxcats.chatservice.extensions.send
import rxcats.chatservice.extensions.setUser
import rxcats.chatservice.ws.FridayAction
import rxcats.chatservice.ws.FridayCommand
import rxcats.chatservice.ws.FridayRequestBody
import rxcats.chatservice.ws.FridaySession

@FridayCommand(prefix = "/auth")
class AuthCommand {

    @FridayAction("/login")
    fun login(
        @FridaySession session: WebSocketSession,
        @FridayRequestBody request: LoginRequest
    ) {
        session.setUser(request.userId, request.username, request.userColor)

        session.send(
            SystemMessageResponse(
                messageType = SystemMessageType.LoginSucceeded,
                data = session.getUserOrThrow()
            )
        )

    }

}