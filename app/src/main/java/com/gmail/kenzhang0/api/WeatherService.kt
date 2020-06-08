package com.gmail.kenzhang0.api

import com.gmail.kenzhang0.vo.WeatherInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET(SERVICE_PATH)
    suspend fun getWeatherByCityId(
        @Query("id") cityId: Int,
        @Query("units") units: String = UNITS_METRIC,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherInfo>

    @GET(SERVICE_PATH)
    suspend fun getWeatherByZipCode(
        @Query(value = "zip", encoded = true) zipCode: String,
        @Query("units") units: String = UNITS_METRIC,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherInfo>

    @GET(SERVICE_PATH)
    suspend fun getWeatherByGps(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = UNITS_METRIC,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherInfo>

    companion object {
        const val SERVICE_PATH = "data/2.5/weather"
        const val API_KEY = "95d190a434083879a6398aafd54d9e73"
        const val UNITS_METRIC = "metric"
        const val POSTFIX_US = ",us"
        const val IMAGE_URL_FORMAT = "https://openweathermap.org/img/wn/%s@2x.png"
    }
}