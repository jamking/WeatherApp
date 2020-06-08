package com.gmail.kenzhang0

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.f2prateek.rx.preferences2.Preference
import com.gmail.kenzhang0.repository.CityRepo
import com.gmail.kenzhang0.repository.WeatherRepo
import com.gmail.kenzhang0.vo.*
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.Description
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class InstantTaskExecutorKoinRule : InstantTaskExecutorRule() {
    val gpsLiveData = MutableLiveData<Resource<WeatherInfo>>()
    val zipcodeLiveData = MutableLiveData<Resource<WeatherInfo>>()
    val cityLiveData = MutableLiveData<Resource<WeatherInfo>>()
    private val testModule: Module = module(override = true) {
        single {
            mockk<CityRepo> {
                every { getList() } returns
                        listOf(
                            City(0, "A"),
                            City(1, "Syd"),
                            City(2, "Syda"),
                            City(3, "Sydb"),
                            City(4, "Sydn"),
                            City(5, "Sydney"),
                            City(6, "Sydney"),
                            City(7, "Z")
                        )
            }
        }
        single {
            mockk<WeatherRepo> {
                every { getWeatherByGps(any(), any()) } returns gpsLiveData
                every { getWeatherByZipcode(any()) } returns zipcodeLiveData
                every { getWeatherByCityId(any()) } returns cityLiveData
            }
        }
        single {
            mockk<Preference<List<Query>>> {
                every { get() } returns listOf(Query.gpsQuery(LatLon(0.0, 0.0)))
                every { set(any()) } returns Unit
            }
        }
    }

    override fun starting(description: Description?) {
        super.starting(description)
        startKoin {
            modules(testModule)
        }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        stopKoin()
    }
}
