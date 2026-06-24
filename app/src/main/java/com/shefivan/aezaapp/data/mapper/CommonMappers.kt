package com.shefivan.aezaapp.data.mapper

import com.shefivan.aezaapp.domain.model.DynamicMap
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

internal fun Double.toBigDecimalValue(): BigDecimal = BigDecimal.valueOf(this)

internal fun String?.toInstantOrEpoch(): Instant {
    val value = this?.takeIf { it.isNotBlank() } ?: return Instant.EPOCH

    return runCatching { Instant.parse(value) }
        .recoverCatching { OffsetDateTime.parse(value).toInstant() }
        .getOrDefault(Instant.EPOCH)
}

internal fun JsonObject.toDynamicMap(): DynamicMap = mapValues { (_, value) -> value.toAnyValue() }

private fun JsonElement.toAnyValue(): Any? = when (this) {
    JsonNull -> null
    is JsonObject -> toDynamicMap()
    is JsonArray -> map { it.toAnyValue() }
    is JsonPrimitive -> toPrimitiveValue()
}

private fun JsonPrimitive.toPrimitiveValue(): Any? {
    if (isString) return content

    return booleanOrNull
        ?: longOrNull
        ?: doubleOrNull
        ?: content
}
