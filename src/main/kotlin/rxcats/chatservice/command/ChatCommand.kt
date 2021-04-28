package rxcats.chatservice.command

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.domain.LoginRequest
import rxcats.chatservice.domain.MessageRequest
import rxcats.chatservice.service.SessionService
import rxcats.chatservice.ws.FridayAction
import rxcats.chatservice.ws.FridayCommand
import rxcats.chatservice.ws.FridayRequestBody
import rxcats.chatservice.ws.FridaySession
import rxcats.chatservice.ws.UserSession

@FridayCommand(prefix = "/chat")
class ChatCommand {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var sessionService: SessionService

    @FridayAction("/login")
    fun login(@FridaySession session: WebSocketSession, @FridayRequestBody request: LoginRequest) {
        session.attributes["userSession"] = UserSession(userId = request.userId, username = request.username, userColor = request.userColor)
    }

    @FridayAction("/join")
    fun join(@FridaySession session: WebSocketSession) {
        sessionService.join("/chat/join", "", session)
    }

    @FridayAction("/leave")
    fun leave(@FridaySession session: WebSocketSession) {
        sessionService.leave("/chat/leave", "", session)
    }

    @FridayAction("/message")
    fun message(@FridaySession session: WebSocketSession, @FridayRequestBody request: MessageRequest) {
        val userSession = session.attributes["userSession"] as UserSession
        sessionService.sendBroadcast("/chat/message", request.message, userSession)
    }

}
