package com.amonteiro.testautomotive.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//Suspend sera expliqué dans le chapitre des coroutines
suspend fun main() {
    val cities = WeatherApiDataSource.loadWeathers("nice")

    for (c in cities) {
        println(c.getResume())
    }


    WeatherApiDataSource.close()
}

object WeatherApiDataSource {

    private const val API_URL =
        "https://api.openweathermap.org/data/2.5/find?appid=b80967f0a6bd10d23e44848547b26550&units=metric&lang=fr&q="

    //Création et réglage du client
    private val client = HttpClient {
        install(Logging) {
            //(import io.ktor.client.plugins.logging.Logger)
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
            level = LogLevel.INFO  // TRACE, HEADERS, BODY, etc.
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
        //engine { proxy = ProxyBuilder.http("monproxy:1234") }
    }


    suspend fun loadWeathers(cityName: String): List<Weather> {
        val response = client.get(API_URL + cityName)
        if (!response.status.isSuccess()) {
            throw Exception("Erreur API: ${response.status} - ${response.bodyAsText()}")
        }

        //Parsing du JSON
        val res = response.body<WeatherAPIResultDTO>()

        //version ancien
//        val list = ArrayList<Weather>()
//        for (r in res.list) {
//            val icon = r.weather.firstOrNull()?.icon ?: "-"
//            list += Weather(
//                id = r.id,
//                name = r.name,
//                temp = r.main.temp,
//                speed = r.wind.speed,
//                description = r.weather.firstOrNull()?.description ?: "-",
//                icon = "https://openweathermap.org/img/wn/$icon@4x.png"
//            )
//        }

        //Version lambda expression
        return res.list.map { r: WeatherDTO ->
            val icon = r.weather.firstOrNull()?.icon ?: "-"
            Weather(
                id = r.id,
                name = r.name,
                temp = r.main.temp,
                speed = r.wind.speed,
                description = r.weather.firstOrNull()?.description ?: "-",
                icon = "https://openweathermap.org/img/wn/$icon@4x.png"
            )
        }
    }


    //Ferme le Client mais celui ci ne sera plus utilisable. Uniquement pour le main
    fun close() = client.close()
}

/* -------------------------------- */
// DTO
/* -------------------------------- */
@Serializable
data class WeatherAPIResultDTO(val list: List<WeatherDTO>)

@Serializable
data class WeatherDTO(
    val id: Int, val name: String, val main: TempDTO, val wind: WindDTO,
    val weather: List<DescriptionDTO>
)

@Serializable
data class TempDTO(val temp: Double)

@Serializable
data class WindDTO(var speed: Double)

@Serializable
data class DescriptionDTO(val description: String, val icon: String)



