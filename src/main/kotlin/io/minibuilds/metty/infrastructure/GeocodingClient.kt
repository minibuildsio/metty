package io.minibuilds.metty.infrastructure

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GeocodingClient(
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

    suspend fun getCoordinates(location: String): List<GeocodingSearchResponse> {
        val response =
            client.get("$openWeatherApiUrl/geo/1.0/direct?q=$location&appid=$openWeatherApiKey")

        return response.body()
    }

}

@Serializable
data class GeocodingSearchResponse (
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String
)

