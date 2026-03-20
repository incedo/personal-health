package com.incedo.personalhealth.shared

@JsFun(
    """
    epochMillis => {
      const formatter = new Intl.DateTimeFormat(undefined, { day: 'numeric', month: 'short' });
      return formatter.format(new Date(epochMillis));
    }
    """
)
private external fun wasmFormatWeightDayLabel(epochMillis: Double): String

@JsFun(
    """
    epochMillis => {
      const formatter = new Intl.DateTimeFormat(undefined, { month: 'short', year: 'numeric' });
      const date = new Date(epochMillis);
      date.setDate(1);
      return formatter.format(date);
    }
    """
)
private external fun wasmFormatWeightMonthLabel(epochMillis: Double): String

@JsFun("epochMillis => new Date(epochMillis).getFullYear()")
private external fun wasmYearOfEpochMillis(epochMillis: Double): Int

@JsFun("year => new Date(year, 0, 1, 0, 0, 0, 0).getTime()")
private external fun wasmStartOfYearEpochMillis(year: Int): Double

internal actual fun formatWeightDayLabel(epochMillis: Long): String =
    wasmFormatWeightDayLabel(epochMillis.toDouble())

internal actual fun formatWeightMonthLabel(epochMillis: Long): String =
    wasmFormatWeightMonthLabel(epochMillis.toDouble())

internal actual fun yearOfEpochMillis(epochMillis: Long): Int =
    wasmYearOfEpochMillis(epochMillis.toDouble())

internal actual fun startOfYearEpochMillis(year: Int): Long =
    wasmStartOfYearEpochMillis(year).toLong()
