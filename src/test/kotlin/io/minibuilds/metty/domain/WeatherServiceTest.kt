package io.minibuilds.metty.domain

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.minibuilds.metty.domain.model.LatLng
import io.minibuilds.metty.domain.model.Weather
import io.minibuilds.metty.infrastructure.OpenWeatherCurrentWeather
import io.minibuilds.metty.infrastructure.OpenWeatherWeatherForecast
import io.minibuilds.metty.infrastructure.WeatherClient
import io.minibuilds.metty.infrastructure.WeatherClientTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.time.LocalTime
import kotlin.test.Test

class WeatherServiceTest {

    private val weatherClient = mockk<WeatherClient>()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val location = LatLng(50.8, -0.8)

    @Test
    fun `Weather service requests current weather from client and converts to domain object`() {
        val openWeatherCurrentWeather =
            json.decodeFromString<OpenWeatherCurrentWeather>(
                WeatherClientTest::class.java.getResource("/current-weather.json")?.readText()!!
            )

        runBlocking {
            coEvery { weatherClient.getCurrentWeather(location) } returns openWeatherCurrentWeather

            val weatherService = WeatherService(weatherClient)

            val currentWeather = weatherService.getCurrentWeather(location)

            currentWeather.weather shouldBe Weather.Clouds
            currentWeather.temperature shouldBe 11
            currentWeather.cloudCoverage shouldBe 89
            currentWeather.sunrise shouldBe LocalTime.parse("08:08:12")
            currentWeather.sunset shouldBe LocalTime.parse("16:05:32")
        }
    }

    @Test
    fun `Weather service requests weather forecast from client and converts to domain objects`() {
        val openWeatherWeatherForecast =
            json.decodeFromString<OpenWeatherWeatherForecast>(
                WeatherClientTest::class.java.getResource("/weather-forecast.json")?.readText()!!
            )

        runBlocking {
            coEvery { weatherClient.getWeatherForecast(location) } returns openWeatherWeatherForecast

            val weatherService = WeatherService(weatherClient)

            val weatherForecast = weatherService.getWeatherForecast(location)

            weatherForecast shouldHaveSize 2
            weatherForecast[0].time shouldBe LocalTime.parse("15:00:00")
            weatherForecast[0].weather shouldBe Weather.Rain
            weatherForecast[0].temperature shouldBe 11
            weatherForecast[0].probabilityOfRain shouldBe 45
            weatherForecast[0].cloudCoverage shouldBe 90
            weatherForecast[1].time shouldBe LocalTime.parse("18:00:00")
            weatherForecast[1].weather shouldBe Weather.Snow
            weatherForecast[1].temperature shouldBe -3
            weatherForecast[1].probabilityOfRain shouldBe 54
            weatherForecast[1].cloudCoverage shouldBe 91
        }
    }
}
