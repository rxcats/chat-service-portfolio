package rxcats.chatservice.ws

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class WebSocketCommandConfig {
    @Bean
    fun registerCommandBeanHolder(ctx: ApplicationContext): WebSocketCommandBeanHolder {
        val controllers = ctx.getBeansWithAnnotation(FridayCommand::class.java)
        val commands = hashMapOf<String, FridayCommandEntity>()
        for (c in controllers) {
            val controller = c.value.javaClass.getDeclaredAnnotation(FridayCommand::class.java)
            for (m in c.value.javaClass.declaredMethods) {
                val method = m.getDeclaredAnnotation(FridayAction::class.java) ?: continue
                val uri =
                    if (method.uri.startsWith("/")) "${controller.prefix}${method.uri}" else "${controller.prefix}/${method.uri}"
                commands[uri] = FridayCommandEntity(c.value, m)
            }
        }
        return WebSocketCommandBeanHolder(commands)
    }
}