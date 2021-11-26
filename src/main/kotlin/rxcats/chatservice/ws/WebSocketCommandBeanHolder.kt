package rxcats.chatservice.ws

class WebSocketCommandBeanHolder(
    private val commands: Map<String, FridayCommandEntity>
) {
    fun get(uri: String) = commands[uri]
    fun getAll() = commands
}