package me.matsumo.romaflow.core.datasource.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.aakira.napier.Napier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.collections.iterator

interface PreferenceHelper {
    fun create(name: String): DataStore<Preferences>
    fun delete(name: String)
}

@OptIn(ExperimentalSerializationApi::class)
fun <T> Preferences.deserialize(
    formatter: Json,
    serializer: KSerializer<T>,
    defaultValue: T,
): T = try {
    val parsedPrefMap = asMap()
        .map { (key, value) -> key.name to formatter.preferenceElement(value) }
        .toMap()

    val defaultData = formatter.encodeToJsonElement(serializer, defaultValue).jsonObject
    val preferenceData = JsonObject(parsedPrefMap)

    val data = buildJsonObject {
        // デフォルトをベースにしつつ、Prefs を上書き
        for ((k, v) in defaultData) put(k, v)
        for ((k, v) in preferenceData) put(k, v)
    }

    formatter.decodeFromJsonElement(serializer, data)
} catch (e: Exception) {
    Napier.e("Failed to deserialize.", e)
    defaultValue
}

private fun Json.preferenceElement(value: Any): JsonElement = when (value) {
    is String -> {
        val isObject = value.startsWith("{") && value.endsWith("}")
        val isArray = value.startsWith("[") && value.endsWith("]")
        if (isObject || isArray) parseToJsonElement(value) else JsonPrimitive(value)
    }

    is Int -> JsonPrimitive(value)

    is Long -> JsonPrimitive(value)

    is Float -> JsonPrimitive(value)

    is Double -> JsonPrimitive(value)

    is Boolean -> JsonPrimitive(value)

    else -> JsonPrimitive(value.toString())
}
