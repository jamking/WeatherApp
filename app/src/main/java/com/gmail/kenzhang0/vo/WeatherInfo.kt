package com.gmail.kenzhang0.vo

import com.gmail.kenzhang0.api.WeatherService.Companion.IMAGE_URL_FORMAT
import com.squareup.moshi.Json

data class WeatherInfo(
    @field:Json(name = "coord") @Json(name = "coord") val latLon: LatLon,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val sys: Sys,
    val name: String
) {
    fun getIconUrl(): String? {
        try {
            if (weather.isNotEmpty() && weather[0].icon.isNotBlank()) {
                return String.format(IMAGE_URL_FORMAT, weather[0].icon)
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
}

data class LatLon(
    val lat: Double = 0.0,
    val lon: Double = 0.0
) {
    override fun toString(): String = "${lat},${lon}"

    companion object {
        fun fromString(str: String): LatLon? {
            try {
                val lst = str.split(",")
                val lat = lst[0].toDoubleOrNull()
                val lon = lst[1].toDoubleOrNull()
                if (lat != null && lon != null) {
                    return LatLon(lat, lon)
                }
                return null
            } catch (e: Exception) {
                return null
            }
        }
    }
}

data class Weather(
    val main: String = "",
    val description: String = "",
    val icon: String = ""
)

data class Main(
    val temp: Float = 0.0f,
    @field:Json(name = "feels_like") @Json(name = "feels_like") val feelsLike: Float = 0.0f,
    @field:Json(name = "temp_min") @Json(name = "temp_min") val tempMin: Float = 0.0f,
    @field:Json(name = "temp_max") @Json(name = "temp_max") val tempMax: Float = 0.0f,
    val pressure: Int = 0,
    var humidity: Int = 0
)

data class Wind(
    val speed: Float = 0.0f,
    @field:Json(name = "deg") @Json(name = "deg") val degree: Float = 0.0f
)

data class Clouds(
    val all: Int = 0
)

data class Sys(
    val country: String = "",
    val sunrise: Long = 0,
    val sunset: Long = 0
)

