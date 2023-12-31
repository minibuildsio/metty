package io.minibuilds.metty

import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.horizontalLayout
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Panel
import io.ktor.client.engine.cio.*
import io.minibuilds.metty.domain.WeatherService
import io.minibuilds.metty.domain.model.LatLng
import io.minibuilds.metty.domain.model.Weather
import io.minibuilds.metty.infrastructure.GeocodingClient
import io.minibuilds.metty.infrastructure.GeocodingSearchResponse
import io.minibuilds.metty.infrastructure.WeatherClient
import io.minibuilds.metty.util.toHHmm

class App(
    private val weatherService: WeatherService,
    private val geocodingClient: GeocodingClient
) {

    suspend fun main(location: String) {
        val coords = geocodingClient.getCoordinates(location)

        if (coords.isEmpty()) {
            println("Unrecognised location $location")
            return
        }

        val latLng = coords.first().toLatLng()
        val currentWeather = weatherService.getCurrentWeather(latLng)
        val weatherForecast = weatherService.getWeatherForecast(latLng)

        val t = Terminal()

        t.println(
            horizontalLayout {
                column(0) { width = ColumnWidth.Fixed(10) }
                cell(Panel("${currentWeather.temperature}°C\n${currentWeather.weather.toEmoji()} "))
                cell(Panel("Sunrise: ${currentWeather.sunrise.toHHmm()}\nSunset: ${currentWeather.sunset.toHHmm()}", expand = true))
            }
        )

        t.println(table {
            header { row("Time", "Weather", "Temp", "Prob. of Rain", "Cloud Coverage") }
            body {
                weatherForecast.take(9).forEach {
                    row(
                        it.time,
                        "${it.weather.toEmoji()} ${it.weather}",
                        "${it.temperature}°C",
                        "${it.probabilityOfRain}%",
                        "${it.cloudCoverage}%"
                    )
                }
            }
        })
    }
}

suspend fun main(args: Array<String>) {
    val location = args.joinToString(",")
    val apiUrl = System.getenv("OPEN_WEATHER_API_URL") ?: "http://api.openweathermap.org"
    val apiKey = System.getenv("OPEN_WEATHER_API_SECRET")

    val weatherClient = WeatherClient(CIO.create(), apiUrl, apiKey)
    val geocodingClient = GeocodingClient(CIO.create(), apiUrl, apiKey)
    val app = App(WeatherService(weatherClient), geocodingClient)

    app.main(location)
}

fun GeocodingSearchResponse.toLatLng() = LatLng(this.lat, this.lon)

fun Weather.toEmoji() = when (this) {
    Weather.Clear -> "☀️ "
    Weather.Thunderstorm -> "⛈️ "
    Weather.Drizzle -> "🌧️ "
    Weather.Rain -> "🌧️ "
    Weather.Snow -> "🌨️ "
    Weather.Tornado -> "🌪️ "
    Weather.Clouds -> "☁️ "
    else -> "🌫️"
}
