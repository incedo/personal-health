package com.incedo.personalhealth.core.health

expect fun parseImportDateTimeToEpochMillis(rawValue: String): Long?

expect fun importDayWindow(epochMillis: Long): CanonicalHealthImportWindow
