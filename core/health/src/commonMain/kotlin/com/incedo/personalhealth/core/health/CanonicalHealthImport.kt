package com.incedo.personalhealth.core.health

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class CanonicalHealthImportWindow(
    val startEpochMillis: Long,
    val endEpochMillis: Long
) {
    init {
        require(startEpochMillis <= endEpochMillis) {
            "startEpochMillis must be before or equal to endEpochMillis"
        }
    }
}

data class CanonicalHealthImportDocument(
    val version: String,
    val exportedAtEpochMillis: Long? = null,
    val window: CanonicalHealthImportWindow,
    val records: List<HealthRecord>
)

fun parseCanonicalHealthImportDocument(json: String): CanonicalHealthImportDocument {
    val root = Json.parseToJsonElement(json).jsonObject
    val windowNode = root.requiredObject("window")
    val recordsNode = root["records"]?.let { element ->
        element as? kotlinx.serialization.json.JsonArray
            ?: throw IllegalArgumentException("records must be an array")
    } ?: throw IllegalArgumentException("records is required")

    return CanonicalHealthImportDocument(
        version = root.requiredString("version"),
        exportedAtEpochMillis = root.optionalLong("exportedAtEpochMillis"),
        window = CanonicalHealthImportWindow(
            startEpochMillis = windowNode.requiredLong("startEpochMillis"),
            endEpochMillis = windowNode.requiredLong("endEpochMillis")
        ),
        records = recordsNode.mapIndexed { index, element ->
            val node = element as? JsonObject
                ?: throw IllegalArgumentException("records[$index] must be an object")
            node.toHealthRecord(index)
        }
    )
}

private fun JsonObject.toHealthRecord(index: Int): HealthRecord = HealthRecord(
    id = requiredString("id", index),
    metric = HealthMetricType.fromSerializedName(requiredString("metric", index)),
    value = requiredDouble("value", index),
    unit = requiredString("unit", index),
    startEpochMillis = requiredLong("startEpochMillis", index),
    endEpochMillis = requiredLong("endEpochMillis", index),
    source = enumValueOf(requiredString("source", index)),
    metadata = optionalObject("metadata")
        ?.entries
        ?.associate { (key, value) -> key to value.jsonPrimitive.content }
        .orEmpty()
)

private fun JsonObject.requiredObject(name: String): JsonObject = this[name]?.jsonObject
    ?: throw IllegalArgumentException("$name is required")

private fun JsonObject.optionalObject(name: String): JsonObject? = this[name]?.jsonObject

private fun JsonObject.requiredString(name: String): String = this[name]?.jsonPrimitive?.content
    ?: throw IllegalArgumentException("$name is required")

private fun JsonObject.requiredString(name: String, index: Int): String = this[name]?.jsonPrimitive?.content
    ?: throw IllegalArgumentException("records[$index].$name is required")

private fun JsonObject.requiredLong(name: String): Long = this[name]?.jsonPrimitive?.longOrNull
    ?: throw IllegalArgumentException("$name must be a long")

private fun JsonObject.requiredLong(name: String, index: Int): Long = this[name]?.jsonPrimitive?.longOrNull
    ?: throw IllegalArgumentException("records[$index].$name must be a long")

private fun JsonObject.optionalLong(name: String): Long? = this[name]?.jsonPrimitive?.longOrNull

private fun JsonObject.requiredDouble(name: String, index: Int): Double = this[name]?.jsonPrimitive?.doubleOrNull
    ?: throw IllegalArgumentException("records[$index].$name must be a number")
