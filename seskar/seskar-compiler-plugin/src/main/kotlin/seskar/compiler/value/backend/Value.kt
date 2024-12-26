package seskar.compiler.value.backend

internal sealed interface Value

@JvmInline
internal value class BooleanValue(
    val value: Boolean,
) : Value

@JvmInline
internal value class IntValue(
    val value: Int,
) : Value

@JvmInline
internal value class DoubleValue(
    val value: Double,
) : Value

@JvmInline
internal value class StringValue(
    val value: String,
) : Value

internal fun Value.toJsName(): String =
    when (this) {
        is BooleanValue -> "$value"
        is IntValue -> "$value"
        is DoubleValue -> "$value"

        is StringValue -> "'$value'"
    }
