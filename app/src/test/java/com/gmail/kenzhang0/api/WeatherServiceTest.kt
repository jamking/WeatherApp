package com.gmail.kenzhang0.api

import com.gmail.kenzhang0.vo.WeatherInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.EOFException
import java.net.HttpURLConnection

class WeatherServiceTest {
    private var mockWebServer = MockWebServer()
    private lateinit var apiService: WeatherService
    private lateinit var weatherResponse: Response<WeatherInfo>

    @Before
    fun setup() {
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val okHttpClient = OkHttpClient.Builder().build()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    private fun serviceCallWithResponse(mockBody: String) {
        val response =
            MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(mockBody)
        mockWebServer.enqueue(response)
        runBlocking {
            weatherResponse = apiService.getWeatherByZipCode("10000")
        }
    }

    @Test
    fun testGetWeatherHappyPath() {
        serviceCallWithResponse(MockResponseFileReader("weather.json").content)

        val weatherInfo = weatherResponse.body()
        assertNotNull(weatherInfo)
        weatherInfo?.let {
            assertEquals("", it.latLon.lat, 35.0, 0.0)
            assertEquals(it.latLon.lon, 139.0, 0.0)

            assertEquals(it.weather.size, 1)
            assertEquals(it.weather[0].main, "Clear")
            assertEquals(it.weather[0].description, "clear sky")
            assertEquals(it.weather[0].icon, "01n")

            assertEquals("", it.main.temp, 281.52f, 0.0f)
            assertEquals("", it.main.feelsLike, 278.99f, 0.0f)
            assertEquals("", it.main.tempMin, 280.15f, 0.0f)
            assertEquals("", it.main.tempMax, 283.71f, 0.0f)
            assertEquals(it.main.pressure, 1016)
            assertEquals(it.main.humidity, 93)

            assertEquals("", it.wind.speed, 0.47f, 0.0f)
            assertEquals("", it.wind.degree, 107.538f, 0.0f)

            assertEquals(it.clouds.all, 2)

            assertEquals(it.sys.country, "JP")
            assertEquals(it.sys.sunrise, 1560281377L)
            assertEquals(it.sys.sunset, 1560333478L)

            assertEquals(it.name, "Shuzenji")
        }
    }

    @Test(expected = EOFException::class)
    fun testGetWeatherWithException() {
        serviceCallWithResponse("")
    }


}