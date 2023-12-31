package io.minibuilds.metty.domain.model

import java.time.LocalTime

data class WeatherForecast(
    val time: LocalTime,
    val weather: Weather,
    val temperature: Int,
    val probabilityOfRain: Int,
    val cloudCoverage: Int
)
