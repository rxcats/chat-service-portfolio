package rxcats.chatservice.ws

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean
import java.util.concurrent.ConcurrentHashMap

@EnableWebSocket
@Configuration
class WebSocketConfig : WebSocketConfigurer {

    @Autowired
    private lateinit var handler: WebSocketMessageHandler

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler, "/ws")
            .setAllowedOrigins("*")
    }

    @ConditionalOnMissingBean
    @Bean
    fun servletServerContainerFactoryBean(): ServletServerContainerFactoryBean? {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(8192)
        container.setMaxBinaryMessageBufferSize(8192)
        return container
    }

    @Bean
    fun registerCommandBeanHolder(ctx: ApplicationContext): WebSocketCommandBeanHolder {
        val controllers = ctx.getBeansWithAnnotation(FridayCommand::class.java)
        val commands = ConcurrentHashMap<String, FridayCommandEntity>()
        for (c in controllers) {
            val controller = c.value.javaClass.getDeclaredAnnotation(FridayCommand::class.java)
            for (m in c.value.javaClass.declaredMethods) {
                val method = m.getDeclaredAnnotation(FridayAction::class.java)
                val uri = if (method.uri.startsWith("/")) "${controller.prefix}${method.uri}" else "${controller.prefix}/${method.uri}"
                commands[uri] = FridayCommandEntity(c.value, m)
            }
        }
        return WebSocketCommandBeanHolder(commands)
    }

}