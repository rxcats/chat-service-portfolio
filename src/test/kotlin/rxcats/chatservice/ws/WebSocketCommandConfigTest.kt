package rxcats.chatservice.ws

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@FridayCommand("/junit")
class JunitTestCommand {
    @FridayAction("/junit")
    fun junit() {

    }
}

@SpringBootTest(classes = [WebSocketCommandConfig::class, JunitTestCommand::class])
class WebSocketCommandConfigTest {

    @Autowired
    private lateinit var holder: WebSocketCommandBeanHolder

    @Autowired
    private lateinit var command: JunitTestCommand

    @Test
    fun findCommandSuccess() {
        val info = holder.get("/junit/junit")!!
        assertThat(info).isNotNull
        assertThat(info.bean).isSameAs(command)
        assertThat(command.junit()).isEqualTo(Unit)
    }

    @Test
    fun findCommandFailure() {
        val info = holder.get("/junit1/junit")
        assertThat(info).isNull()
    }

}