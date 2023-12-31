package io.minibuilds.metty.infrastructure

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


class GeocodingClientTest {

    @Test
    fun `Geocoding client parses current weather response`() {
        val fileContent =
            GeocodingClientTest::class.java.getResource("/geo-search.json")?.readText()
                ?: throw NullPointerException()

        runBlocking {
            val mockEngine = MockEngine { request ->

                request.url.toString() shouldBe "http://api.openweathermap.org/geo/1.0/direct?q=southampton&appid=OPEN_WEATHER_API_KEY"

                respond(
                    content = ByteReadChannel(fileContent),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            val geocodingClient =
                GeocodingClient(mockEngine, "http://api.openweathermap.org", "OPEN_WEATHER_API_KEY")

            val coords = geocodingClient.getCoordinates("southampton")

            coords shouldHaveSize 1
            coords[0].name shouldBe "Southampton"
            coords[0].lat shouldBe 50.9025349
            coords[0].lon shouldBe -1.404189
        }
    }
}
