package io.wollinger.cutils

import java.time.Instant

object TimeUtils {
    fun getUnixTime() = Instant.now().epochSecond
}