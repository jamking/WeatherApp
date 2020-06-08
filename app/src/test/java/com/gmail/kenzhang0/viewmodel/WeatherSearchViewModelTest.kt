package com.gmail.kenzhang0.viewmodel

import com.gmail.kenzhang0.InstantTaskExecutorKoinRule
import com.gmail.kenzhang0.repository.CityRepo
import com.gmail.kenzhang0.repository.WeatherRepo
import com.gmail.kenzhang0.ui.WeatherSearchViewModel
import com.gmail.kenzhang0.ui.WeatherSearchViewModel.Companion.PREFERENCE_QUERY_MAX
import com.gmail.kenzhang0.vo.*
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

class WeatherSearchViewModelTest : KoinTest {
    //    private val queriesPreference: Preference<List<Query>> by inject()
    private val weatherRepo: WeatherRepo by inject()
    private val cityRepo: CityRepo by inject()

    private lateinit var vm: WeatherSearchViewModel
    private val weatherInfo = WeatherInfo(
        LatLon(132.3, 139.0),
        listOf(Weather("main", "desc", "icon")),
        Main(2.2f, 2.1f, 2.3f, 2.4f, 1022, 20),
        Wind(2.5f, 12.0f),
        Clouds(10),
        Sys("AU", 1, 1),
        "name"
    )

    @Rule
    @JvmField
    var rule = InstantTaskExecutorKoinRule()

    @Before
    fun setup() {
        vm = WeatherSearchViewModel()
    }

    @Test
    fun getCityListTest() {
        vm.getCityList()
        verify { cityRepo.getList() }
    }

    @Test
    fun searchWithQueryZipcodeTest() {
        vm.searchWithQuery(Query(QueryType.ZIP.type, "90001"))
        verify(exactly = 1) { weatherRepo.getWeatherByZipcode("90001,us") }

        rule.zipcodeLiveData.postValue(Resource(Status.SUCCESS, weatherInfo, null))
        vm.liveWeatherInfo.observeForever {
            assertNotNull(it.data)
            assertEquals(it.data, weatherInfo)
            assertEquals(it.message, null)
        }
    }

    @Test
    fun searchWithQueryCityTest() {
        vm.searchWithQuery(Query(QueryType.CITY.type, "12345"))
        verify(exactly = 1) { weatherRepo.getWeatherByCityId(12345) }

        rule.cityLiveData.postValue(Resource(Status.SUCCESS, weatherInfo, null))
        vm.liveWeatherInfo.observeForever {
            assertNotNull(it.data)
            assertEquals(it.data, weatherInfo)
            assertEquals(it.message, null)
        }
    }

    @Test
    fun searchWithQueryGpsTest() {
        vm.searchWithQuery(Query.gpsQuery(LatLon(1.2, 2.3)))
        verify(exactly = 1) {
            weatherRepo.getWeatherByGps(1.2, 2.3)
        }
        rule.gpsLiveData.postValue(Resource(Status.SUCCESS, weatherInfo, null))
        vm.liveWeatherInfo.observeForever {
            assertNotNull(it.data)
            assertEquals(it.data, weatherInfo)
            assertEquals(it.message, null)
        }
    }

    @Test
    fun searchMostRecentTest() {
        vm.searchMostRecent()
        verify(exactly = 1) {
            weatherRepo.getWeatherByGps(0.0, 0.0) // as defined in koin module
        }

        vm.searchMostRecent()
        verify(exactly = 1) {
            weatherRepo.getWeatherByGps(0.0, 0.0) // as defined in koin module
        }
    }

    @Test
    fun isValidZipCodeTest() {
        assertTrue(WeatherSearchViewModel.isValidZipCode("12345"))
        assertFalse(WeatherSearchViewModel.isValidZipCode("1234"))
        assertFalse(WeatherSearchViewModel.isValidZipCode("123456"))
        assertFalse(WeatherSearchViewModel.isValidZipCode("abcde"))
        assertFalse(WeatherSearchViewModel.isValidZipCode("abcd"))
        assertFalse(WeatherSearchViewModel.isValidZipCode("abcdef"))
    }

    @Test
    fun updateQueryListTest() {
        val queryList = mutableListOf<Query>(
            Query(1, "a"),
            Query(2, "b"),
            Query(3, "c")
        )
        val query: Query = Query(2, "temp")

        var lst: List<Query> = queryList
        repeat(PREFERENCE_QUERY_MAX - 3) {
            lst = WeatherSearchViewModel.updateQueryList(query, lst)
        }
        assertEquals(lst.size, PREFERENCE_QUERY_MAX)
        assertEquals(lst[0].type, 2)
        assertEquals(lst[0].input, "temp")

        // size won't change after reach max
        lst = WeatherSearchViewModel.updateQueryList(query, lst)
        assertEquals(lst.size, PREFERENCE_QUERY_MAX)
        assertEquals(lst[0].type, 2)
        assertEquals(lst[0].input, "temp")
    }
}