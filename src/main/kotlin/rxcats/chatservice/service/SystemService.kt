package rxcats.chatservice.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.ws.FridaySession
import java.util.concurrent.TimeUnit

@Service
class SystemService(
    private val lobbyService: LobbyService,
    private val roomService: RoomService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun disconnect(@FridaySession session: WebSocketSession) {
        lobbyService.disconnect(session)
        roomService.disconnect(session)
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
    fun monitoringSessions() {
        val lobbyUsers = lobbyService.getOnlineUsers()
        val rooms = roomService.rooms()
        log.info("lobbyUser: [{}], rooms: [{}]", lobbyUsers.size, rooms.size)
    }

}