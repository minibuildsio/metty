package io.minibuilds.metty.domain.model

import java.time.LocalTime

data class CurrentWeather(
    val weather: Weather,
    val temperature: Int,
    val cloudCoverage: Int,
    val sunrise: LocalTime,
    val sunset: LocalTime,
)
