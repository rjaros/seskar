package seskar.js

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class JsAsync(
    val optional: Boolean = false,
)
