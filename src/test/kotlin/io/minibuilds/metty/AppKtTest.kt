package io.minibuilds.metty

import io.minibuilds.metty.domain.WeatherService
import io.minibuilds.metty.domain.model.CurrentWeather
import io.minibuilds.metty.domain.model.LatLng
import io.minibuilds.metty.domain.model.Weather
import io.minibuilds.metty.domain.model.WeatherForecast
import io.minibuilds.metty.infrastructure.GeocodingClient
import io.minibuilds.metty.infrastructure.GeocodingSearchResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.LocalTime
import kotlin.test.Test

class AppKtTest {
    private val weatherService = mockk<WeatherService>()
    private val geocodingClient = mockk<GeocodingClient>()
    private val app = App(weatherService, geocodingClient)

    @Test
    fun `app requests get coords by location name and requests weather data`() {
        runBlocking {
            coEvery { geocodingClient.getCoordinates(any()) } returns listOf(
                GeocodingSearchResponse(
                    "Southampton",
                    50.8,
                    -0.8,
                    "GB",
                    "England"
                )
            )
            coEvery { weatherService.getCurrentWeather(any()) } returns CurrentWeather(
                Weather.Clear,
                15,
                50,
                LocalTime.parse("06:00"),
                LocalTime.parse("21:00")
            )
            coEvery { weatherService.getWeatherForecast(any()) } returns listOf(
                WeatherForecast(
                    LocalTime.parse("10:00"), Weather.Clear, 10, 0, 15
                )
            )

            app.main("southampton")

            coVerify(exactly = 1) { geocodingClient.getCoordinates("southampton") }
            coVerify(exactly = 1) { weatherService.getCurrentWeather(LatLng(50.8, -0.8)) }
            coVerify(exactly = 1) { weatherService.getWeatherForecast(LatLng(50.8, -0.8)) }
        }
    }

    @Test
    fun `app doesn't request weather data when it cannot get coords for location name`() {
        runBlocking {
            coEvery { geocodingClient.getCoordinates(any()) } returns listOf()

            app.main("southampton")

            coVerify(exactly = 1) { geocodingClient.getCoordinates("southampton") }
            coVerify(exactly = 0) { weatherService.getCurrentWeather(any()) }
            coVerify(exactly = 0) { weatherService.getWeatherForecast(any()) }
        }
    }
}
