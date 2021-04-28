package rxcats.chatservice.ws

import org.springframework.stereotype.Component

@Component
@Target(AnnotationTarget.CLASS)
annotation class FridayCommand(val prefix: String = "")

@Target(AnnotationTarget.FUNCTION)
annotation class FridayAction(val uri: String = "", val desc: String = "")

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FridayRequestBody

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FridayRequestHeader(val value: String = "")

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FridaySession