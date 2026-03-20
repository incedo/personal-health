package com.incedo.personalhealth.core.health

@JsFun(
    """rawValue => {
        const normalized = rawValue.trim().replace(/^"|"$/g, "");
        if (!normalized) return null;
        const [datePart, timePart] = normalized.split(" ");
        const [year, month, day] = datePart.split("-").map(Number);
        const [hour, minute, second] = (timePart || "00:00:00").split(":").map(Number);
        const date = new Date(year, (month || 1) - 1, day || 1, hour || 0, minute || 0, second || 0, 0);
        const time = date.getTime();
        return Number.isNaN(time) ? null : time;
    }"""
)
private external fun parseImportDateMillisJs(rawValue: String): Double?

@JsFun(
    """epochMillis => {
        const start = new Date(epochMillis);
        start.setHours(0, 0, 0, 0);
        return start.getTime();
    }"""
)
private external fun importDayStartEpochMillisJs(epochMillis: Double): Double

@JsFun(
    """epochMillis => {
        const end = new Date(epochMillis);
        end.setHours(0, 0, 0, 0);
        end.setDate(end.getDate() + 1);
        return end.getTime() - 1;
    }"""
)
private external fun importDayEndEpochMillisJs(epochMillis: Double): Double

actual fun parseImportDateTimeToEpochMillis(rawValue: String): Long? =
    parseImportDateMillisJs(rawValue)?.toLong()

actual fun importDayWindow(epochMillis: Long): CanonicalHealthImportWindow {
    return CanonicalHealthImportWindow(
        startEpochMillis = importDayStartEpochMillisJs(epochMillis.toDouble()).toLong(),
        endEpochMillis = importDayEndEpochMillisJs(epochMillis.toDouble()).toLong()
    )
}
