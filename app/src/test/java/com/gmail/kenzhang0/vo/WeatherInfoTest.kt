package com.gmail.kenzhang0.vo

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherInfoTest {

    @Test
    fun getIconUrlTest() {
        val latLon = LatLon(132.3, 139.0)
        val weathers = listOf(Weather("main", "desc", "icon"))
        val emptyWeathers = listOf<Weather>()
        val main = Main(2.2f, 2.1f, 2.3f, 2.4f, 1022, 20)
        val wind = Wind(2.5f, 12.0f)
        val clouds = Clouds(10)
        val sys = Sys("AU", 1, 1)

        val weatherInfo = WeatherInfo(latLon, weathers, main, wind, clouds, sys, "name")
        assertEquals(weatherInfo.getIconUrl(), "https://openweathermap.org/img/wn/icon@2x.png")

        val weatherInfoEmptyWeather = WeatherInfo(latLon, emptyWeathers, main, wind, clouds, sys, "name")
        assertEquals(weatherInfoEmptyWeather.getIconUrl(), null)
    }

    @Test
    fun latLonFromStringTest() {
        val latLon = LatLon.fromString("123.1,30.2")
        assert(latLon != null)
        latLon?.let {
            assertEquals("", latLon.lat, 123.1, 0.0)
            assertEquals("", latLon.lon, 30.2, 0.0)
        }
    }

    @Test
    fun latLonFromStringEmptyTest() {
        val latLon = LatLon.fromString("")
        assertEquals(latLon, null)
    }

    @Test
    fun latLonFromStringInvalidTest() {
        val latLon = LatLon.fromString("123,abc")
        assertEquals(latLon, null)
    }


}