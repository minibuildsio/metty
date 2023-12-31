package io.minibuilds.metty.domain

import io.minibuilds.metty.domain.model.CurrentWeather
import io.minibuilds.metty.domain.model.LatLng
import io.minibuilds.metty.domain.model.Weather
import io.minibuilds.metty.domain.model.WeatherForecast
import io.minibuilds.metty.infrastructure.ListElement
import io.minibuilds.metty.infrastructure.OpenWeatherCurrentWeather
import io.minibuilds.metty.infrastructure.WeatherClient
import io.minibuilds.metty.util.toLocalDateTime

class WeatherService(private val weatherClient: WeatherClient) {

    suspend fun getCurrentWeather(location: LatLng) =
        weatherClient.getCurrentWeather(location).toCurrentWeather()

    suspend fun getWeatherForecast(location: LatLng) =
        weatherClient.getWeatherForecast(location).list.map { it.toWeatherForecast() }

}

private fun OpenWeatherCurrentWeather.toCurrentWeather() = CurrentWeather(
    Weather.valueOf(this.weather.first().main),
    (this.main.temp - 273.15).toInt(),
    this.clouds.all,
    this.sys.sunrise.toLocalDateTime().toLocalTime(),
    this.sys.sunset.toLocalDateTime().toLocalTime()
)

private fun ListElement.toWeatherForecast() = WeatherForecast(
    this.dt.toLocalDateTime().toLocalTime(),
    Weather.valueOf(this.weather.first().main),
    (this.main.temp - 273.15).toInt(),
    (this.pop * 100).toInt(),
    this.clouds.all
)
