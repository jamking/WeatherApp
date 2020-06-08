package com.gmail.kenzhang0.repository

import androidx.lifecycle.LiveData
import com.gmail.kenzhang0.AppExecutors
import com.gmail.kenzhang0.api.WeatherService
import com.gmail.kenzhang0.vo.Resource
import com.gmail.kenzhang0.vo.WeatherInfo

class WeatherRepo(
    private val appExecutors: AppExecutors,
    private val weatherService: WeatherService
) {
    fun getWeatherByCityId(cityId: Int): LiveData<Resource<WeatherInfo>> {
        return object : NetworkBoundResource<WeatherInfo>(appExecutors) {
            override suspend fun createCall() = weatherService.getWeatherByCityId(cityId)
        }.asLiveData()
    }

    fun getWeatherByZipcode(zipcode: String): LiveData<Resource<WeatherInfo>> {
        return object : NetworkBoundResource<WeatherInfo>(appExecutors) {
            override suspend fun createCall() = weatherService.getWeatherByZipCode(zipcode)
        }.asLiveData()
    }

    fun getWeatherByGps(lat: Double, lon: Double): LiveData<Resource<WeatherInfo>> {
        return object : NetworkBoundResource<WeatherInfo>(appExecutors) {
            override suspend fun createCall() = weatherService.getWeatherByGps(lat, lon)
        }.asLiveData()
    }
}

