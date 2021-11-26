package rxcats.chatservice.ws

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import rxcats.chatservice.domain.SystemMessageResponse
import rxcats.chatservice.domain.SystemMessageType
import rxcats.chatservice.error.ServiceException
import rxcats.chatservice.extensions.send
import rxcats.chatservice.service.SystemService
import rxcats.chatservice.util.Json

/**
 * 웹소켓 메시지를 파싱 햐여 해당되는 Command 클래스 를 검색하여 함수 를 실행,
 * 실행 결과를 클라이언트 에 전송
 */
@Component
class WebSocketMessageHandler(
    val holder: WebSocketCommandBeanHolder,
    val systemService: SystemService
) : TextWebSocketHandler() {
    companion object {
        private const val FIELD_URI = "uri"
        private const val FIELD_HEADERS = "headers"
        private const val FIELD_BODY = "body"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("connected: {}", session)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        systemService.disconnect(session)
        log.info("disconnected: {}", session)
    }

    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        handle(session, message.payload)
    }

    private fun handle(session: WebSocketSession, message: String) {

        log.info("message: {}", message)

        try {
            val request = Json.decode<Map<String, Any>>(message)

            val uriObject = request[FIELD_URI] ?: throw ServiceException("cannot find command uri")

            val uri = Json.convert<String>(uriObject)

            val command = holder.get(uri) ?: throw ServiceException("cannot find command bean")

            command.method.invoke(command.bean, *parseArgs(command, request, session))
        } catch (e: Throwable) {
            log.error(e.cause?.message, e)
            val errorResponse = SystemMessageResponse<Any>(SystemMessageType.OnError, e.cause?.message)
            log.error("{}", errorResponse)
            session.send(errorResponse)
        }

    }

    private fun parseArgs(
        command: FridayCommandEntity,
        request: Map<String, Any>,
        session: WebSocketSession
    ): Array<Any?> {
        val parameterTypes = command.method.parameterTypes
        val parameterAnnotations = command.method.parameterAnnotations

        val paramValues = arrayOfNulls<Any>(parameterTypes.size)

        for (i in parameterTypes.indices) {
            for (j in parameterAnnotations[i].indices) {
                when (parameterAnnotations[i][j]) {
                    is FridayRequestBody -> {
                        paramValues[i] = Json.convert(request[FIELD_BODY] ?: Any(), parameterTypes[i])
                    }
                    is FridayRequestHeader -> {
                        paramValues[i] = Json.convert<Map<String, Any>>(request[FIELD_HEADERS] ?: Any())
                    }
                    is FridaySession -> {
                        paramValues[i] = session
                    }
                }
            }
        }

        return paramValues
    }

}