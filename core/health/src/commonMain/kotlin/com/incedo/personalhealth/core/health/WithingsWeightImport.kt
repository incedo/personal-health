package com.incedo.personalhealth.core.health

private const val WITHINGS_IMPORT_VERSION = "withings-health-csv-v1"

fun parseHealthImportPayload(payload: String): CanonicalHealthImportDocument {
    val normalized = payload.trim()
    require(normalized.isNotBlank()) { "Import payload is leeg" }
    return if (normalized.startsWith("{")) {
        parseCanonicalHealthImportDocument(normalized)
    } else {
        parseWithingsWeightCsvImport(normalized)
    }
}

fun parseWithingsWeightCsvImport(csv: String): CanonicalHealthImportDocument {
    val rows = csv
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()
    require(rows.isNotEmpty()) { "Withings CSV is leeg" }

    val headers = parseCsvRow(rows.first())
    val dateIndex = headers.indexOfFirst { it.normalizedHeader().contains("date") }
    val timeIndex = headers.indexOfFirst { it.normalizedHeader() == "time" }
    val metricColumns = WITHINGS_COLUMN_MAPPINGS.mapNotNull { mapping ->
        headers.indexOfFirst { it.normalizedHeader() == mapping.headerKey }
            .takeIf { it >= 0 }
            ?.let { index -> mapping to index }
    }

    require(dateIndex >= 0) { "Withings CSV mist een datumkolom" }
    require(metricColumns.isNotEmpty()) { "Withings CSV mist ondersteunde healthkolommen" }

    val records = rows.drop(1).flatMapIndexed { rowIndex, row ->
        val columns = parseCsvRow(row)
        val datePart = columns.getOrNull(dateIndex).orEmpty()
        val timestamp = buildString {
            append(datePart)
            columns.getOrNull(timeIndex)
                ?.takeIf { it.isNotBlank() }
                ?.let { append(" ").append(it) }
        }
        val epochMillis = parseImportDateTimeToEpochMillis(timestamp) ?: return@flatMapIndexed emptyList()
        metricColumns.mapNotNull { (mapping, index) ->
            val value = columns.getOrNull(index)?.toDoubleOrNull() ?: return@mapNotNull null
            HealthRecord(
                id = "withings-${mapping.metric.name.lowercase()}-$epochMillis-$rowIndex",
                metric = mapping.metric,
                value = value,
                unit = mapping.unit,
                startEpochMillis = epochMillis,
                endEpochMillis = epochMillis,
                source = HealthDataSource.WITHINGS,
                metadata = mapOf(
                    "origin" to "withings-csv-import",
                    "raw_date" to datePart
                ) + columns.getOrNull(timeIndex)
                    ?.takeIf { it.isNotBlank() }
                    ?.let { mapOf("raw_time" to it) }
                    .orEmpty()
            )
        }
    }.sortedBy { it.endEpochMillis }

    require(records.isNotEmpty()) { "Geen geldige healthmetingen gevonden in Withings CSV" }

    val latestEpochMillis = records.maxOf { it.endEpochMillis }
    return CanonicalHealthImportDocument(
        version = WITHINGS_IMPORT_VERSION,
        exportedAtEpochMillis = currentEpochMillis(),
        window = importDayWindow(latestEpochMillis),
        records = records
    )
}

private data class WithingsColumnMapping(
    val headerKey: String,
    val metric: HealthMetricType,
    val unit: String
)

private val WITHINGS_COLUMN_MAPPINGS = listOf(
    WithingsColumnMapping("weightkg", HealthMetricType.BODY_WEIGHT_KG, "kg"),
    WithingsColumnMapping("fatmass%", HealthMetricType.BODY_FAT_PERCENTAGE, "%"),
    WithingsColumnMapping("musclemasskg", HealthMetricType.MUSCLE_MASS_KG, "kg"),
    WithingsColumnMapping("bonemasskg", HealthMetricType.BONE_MASS_KG, "kg"),
    WithingsColumnMapping("watermass%", HealthMetricType.WATER_PERCENTAGE, "%"),
    WithingsColumnMapping("systolicbloodpressuremmhg", HealthMetricType.SYSTOLIC_BLOOD_PRESSURE_MMHG, "mmHg"),
    WithingsColumnMapping("diastolicbloodpressuremmhg", HealthMetricType.DIASTOLIC_BLOOD_PRESSURE_MMHG, "mmHg")
)

private fun parseCsvRow(row: String): List<String> {
    val values = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false
    row.forEachIndexed { index, char ->
        when {
            char == '"' -> {
                val escapedQuote = inQuotes && row.getOrNull(index + 1) == '"'
                if (escapedQuote) {
                    current.append('"')
                } else {
                    inQuotes = !inQuotes
                }
            }

            char == ',' && !inQuotes -> {
                values += current.toString().trim()
                current.clear()
            }

            else -> current.append(char)
        }
    }
    values += current.toString().trim()
    return values
}

private fun String.normalizedHeader(): String = lowercase()
    .replace(" ", "")
    .replace("_", "")
    .replace("(", "")
    .replace(")", "")
