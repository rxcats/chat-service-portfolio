package rxcats.chatservice.ws

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import rxcats.chatservice.util.Json

/**
 * 웹소켓 메시지를 파싱 햐여 해당되는 Command 클래스 를 검색하여 함수 를 실행,
 * 실행 결과를 클라이언트 에 전송
 */
@Component
class WebSocketMessageHandler : TextWebSocketHandler() {
    companion object {
        private const val FIELD_URI = "uri"
        private const val FIELD_HEADERS = "headers"
        private const val FIELD_BODY = "body"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var holder: WebSocketCommandBeanHolder

    override fun afterConnectionEstablished(session: WebSocketSession) {
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    }

    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        handle(session, message.payload)
    }

    private fun handle(session: WebSocketSession, message: String) {

        log.info("message: {}", message)

        val request = Json.readValue<Map<String, Any>>(message)

        val uriObject = request[FIELD_URI] ?: error("cannot find command uri")

        val uri = Json.convertValue<String>(uriObject)

        val command = holder.get(uri) ?: error("cannot find command bean")

        val result = command.method.invoke(command.bean, *parseArgs(command, request, session))

        // val response = WebSocketResponse(body = result)

        // session.sendMessage(TextMessage(Json.toJsonString(response)))

    }

    private fun parseArgs(command: FridayCommandEntity, request: Map<String, Any>, session: WebSocketSession): Array<Any?> {
        val parameterTypes = command.method.parameterTypes
        val parameterAnnotations = command.method.parameterAnnotations

        val paramValues = arrayOfNulls<Any>(parameterTypes.size)

        for (i in parameterTypes.indices) {
            for (j in parameterAnnotations[i].indices) {
                when (parameterAnnotations[i][j]) {
                    is FridayRequestBody -> {
                        paramValues[i] = Json.convertValue(request[FIELD_BODY] ?: Any(), parameterTypes[i])
                    }
                    is FridayRequestHeader -> {
                        paramValues[i] = Json.convertValue<Map<String, Any>>(request[FIELD_HEADERS] ?: Any())
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