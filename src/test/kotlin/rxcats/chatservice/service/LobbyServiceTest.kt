package rxcats.chatservice.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.socket.WebSocketSession
import rxcats.chatservice.extensions.getUser
import rxcats.chatservice.ws.UserProfile

@SpringBootTest(classes = [LobbyService::class])
class LobbyServiceTest {

    @Autowired
    private lateinit var service: LobbyService

    @Test
    fun join() {
        val user = UserProfile(
            userId = "junit.1",
            username = "junit.1",
            userColor = "#000000"
        )

        val session = mockk<WebSocketSession>()

        every { session.getUser() } returns user

        every { session.isOpen } returns true

        service.join(session)

        val users = service.getOnlineUsers()

        assertThat(users.contains(user)).isTrue

        service.leave(session)
    }

    @Test
    fun leave() {
        val user = UserProfile(
            userId = "junit.2",
            username = "junit.2",
            userColor = "#000000"
        )

        val session = mockk<WebSocketSession>()

        every { session.getUser() } returns user

        every { session.isOpen } returns true

        service.join(session)

        service.leave(session)

        val users = service.getOnlineUsers()

        assertThat(users.contains(user)).isFalse
    }

    @Test
    fun disconnect() {
        val user = UserProfile(
            userId = "junit.3",
            username = "junit.3",
            userColor = "#000000"
        )

        val session = mockk<WebSocketSession>()

        every { session.getUser() } returns user

        every { session.isOpen } returns true

        service.join(session)

        service.disconnect(session)

        val users = service.getOnlineUsers()

        assertThat(users.contains(user)).isFalse
    }
}