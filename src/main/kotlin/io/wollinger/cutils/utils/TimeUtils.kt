package io.wollinger.cutils.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

object TimeUtils {
    fun getUnixTime() = Instant.now().epochSecond
}

enum class TimestampType(private val sign: String) {
    SHORT_TIME("t"),
    LONG_TIME("T"),
    SHORT_DATE("d"),
    LONG_DATE("D"),
    LONG_DATE_SHORT_TIME("f"),
    LONG_DATE_DOW_SHORT_TIME("F"),
    RELATIVE("R");

    fun formatNow() = format(TimeUtils.getUnixTime())
    fun format(time: OffsetDateTime?) = if (time == null) format(0) else format(time.toEpochSecond())
    fun format(time: LocalDateTime) = format(time.atZone(ZoneId.systemDefault()).toEpochSecond())
    fun format(unixTime: Long) = "<t:$unixTime:$sign>"
}