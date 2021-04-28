package rxcats.chatservice.ws

import java.util.concurrent.ConcurrentHashMap

class WebSocketCommandBeanHolder(
    private val commands: ConcurrentHashMap<String, FridayCommandEntity>
) {
    fun get(uri: String) = commands[uri]
    fun getAll() = commands
}