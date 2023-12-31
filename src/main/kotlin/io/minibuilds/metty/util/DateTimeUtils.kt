package io.minibuilds.metty.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Long.toLocalDateTime(): LocalDateTime = LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
fun LocalTime.toHHmm() = this.format(DateTimeFormatter.ofPattern("HH:mm"))
