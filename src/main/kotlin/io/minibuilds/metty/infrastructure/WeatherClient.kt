package io.minibuilds.metty.infrastructure

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.minibuilds.metty.domain.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WeatherClient(
    engine: HttpClientEngine,
    private val openWeatherApiUrl: String,
    private val openWeatherApiKey: String,
) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getCurrentWeather(location: LatLng): OpenWeatherCurrentWeather {
        val response =
            client.get("$openWeatherApiUrl/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$openWeatherApiKey")

        return response.body()
    }

    suspend fun getWeatherForecast(location: LatLng): OpenWeatherWeatherForecast {
        val response =
            client.get("$openWeatherApiUrl/data/2.5/forecast?lat=${location.latitude}&lon=${location.longitude}&appid=$openWeatherApiKey")

        return response.body()
    }
}

@Serializable
data class OpenWeatherCurrentWeather(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class Coord(
    val lon: Double,
    val lat: Double
)

@Serializable
data class Main(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long
)

@Serializable
data class Sys(
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

@Serializable
data class Weather(
    val main: String
)

@Serializable
data class Wind(
    val speed: Double,
    val deg: Long
)

@Serializable
data class OpenWeatherWeatherForecast(
    val list: List<ListElement>,
    val city: City
)

@Serializable
data class City(
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long
)

@Serializable
data class ListElement(
    val dt: Long,
    val main: MainClass,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
)

@Serializable
data class MainClass(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
)
