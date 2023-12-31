package io.minibuilds.metty.infrastructure

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.minibuilds.metty.domain.model.LatLng
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


class WeatherClientTest {

    @Test
    fun `Weather client parses current weather response`() {
        val fileContent =
            WeatherClientTest::class.java.getResource("/current-weather.json")?.readText()
                ?: throw NullPointerException()

        runBlocking {
            val mockEngine = MockEngine { request ->

                request.url.toString() shouldBe "http://api.openweathermap.org/data/2.5/weather?lat=50.8&lon=-0.8&appid=OPEN_WEATHER_API_KEY"

                respond(
                    content = ByteReadChannel(fileContent),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val weatherClient =
                WeatherClient(mockEngine, "http://api.openweathermap.org", "OPEN_WEATHER_API_KEY")

            val currentWeather = weatherClient.getCurrentWeather(LatLng(50.8, -0.8))

            currentWeather.weather[0].main shouldBe "Clouds"
            currentWeather.main.temp shouldBe 284.95
            currentWeather.clouds.all shouldBe 89
        }
    }

    @Test
    fun `Weather client parses weather forecast response`() {
        val fileContent =
            WeatherClientTest::class.java.getResource("/weather-forecast.json")?.readText()
                ?: throw NullPointerException()

        runBlocking {
            val mockEngine = MockEngine { request ->
                request.url.toString() shouldBe "http://api.openweathermap.org/data/2.5/forecast?lat=50.8&lon=-0.8&appid=OPEN_WEATHER_API_KEY"

                respond(
                    content = ByteReadChannel(fileContent),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val weatherClient =
                WeatherClient(mockEngine, "http://api.openweathermap.org", "OPEN_WEATHER_API_KEY")

            val weatherForecast = weatherClient.getWeatherForecast(LatLng(50.8, -0.8))

            weatherForecast.list shouldHaveSize 2
            weatherForecast.list[0].weather[0].main shouldBe "Rain"
            weatherForecast.list[0].main.temp shouldBe 284.92
            weatherForecast.list[0].clouds.all shouldBe 90
            weatherForecast.list[0].dt shouldBe 1703775600
            weatherForecast.list[1].weather[0].main shouldBe "Snow"
            weatherForecast.list[1].main.temp shouldBe 270
            weatherForecast.list[1].clouds.all shouldBe 91
            weatherForecast.list[1].dt shouldBe 1703786400
        }
    }
}
