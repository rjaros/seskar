package seskar.js

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class JsTypeGuard(
    val property: String,
    val value: String,
)
