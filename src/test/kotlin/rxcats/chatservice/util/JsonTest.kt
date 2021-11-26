package rxcats.chatservice.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JsonTest {

    @Test
    fun shouldBeEquals() {
        assertThat(Json.objectMapper)
            .isEqualTo(Json.objectMapper)
            .isSameAs(Json.objectMapper)
    }

}