package com.gmail.kenzhang0.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.f2prateek.rx.preferences2.Preference
import com.gmail.kenzhang0.api.WeatherService
import com.gmail.kenzhang0.repository.CityRepo
import com.gmail.kenzhang0.repository.WeatherRepo
import com.gmail.kenzhang0.vo.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class WeatherSearchViewModel : ViewModel(), KoinComponent {
    private val queriesPreference: Preference<List<Query>> by inject()
    private val weatherRepo: WeatherRepo by inject()
    private val cityRepo: CityRepo by inject()
    private var fromRecentSearch: Boolean = false
    lateinit var recentQuery: Query

    var input: String = ""
    var cityId: Int? = null
    var latLon: LatLon? = null

    var liveWeatherInfo: MediatorLiveData<Resource<WeatherInfo>> = MediatorLiveData()
    var queryList: List<Query> = queriesPreference.get()

    fun searchMostRecent() {
        // only do once
        if (!recentSearchedDone) {
            recentSearchedDone = true
            if (queryList.isNotEmpty()) {
                val query = queryList[0]
                searchWithQuery(query)
                fromRecentSearch = true
            }
        }
    }

    fun searchWithQuery(query: Query) {
        recentQuery = query
        when (QueryType.fromInt(query.type)) {
            QueryType.CITY -> {
                cityId = query.input.toIntOrNull()
                cityId?.let {
                    recentQuery = query
                    searchByCityId(it)
                }
            }
            QueryType.ZIP -> {
                input = query.input
                recentQuery = query
                searchByZipCode()
            }
            QueryType.GPS -> {
                latLon = LatLon.fromString(query.input)
                latLon?.let {
                    recentQuery = query
                    searchByLatLng(it.lat, it.lon)
                }
            }
            else -> {
            }
        }
    }

    fun isValidZipCode(): Boolean {
        return isValidZipCode(input)
    }

    private fun searchByZipCode() {
        val apiResponse = weatherRepo.getWeatherByZipcode("$input${WeatherService.POSTFIX_US}")
        updateLiveData(apiResponse)
    }

    private fun searchByCityId(id: Int) {
        val apiResponse = weatherRepo.getWeatherByCityId(id)
        updateLiveData(apiResponse)
    }

    private fun searchByLatLng(lat: Double, lon: Double) {
        val apiResponse = weatherRepo.getWeatherByGps(lat, lon)
        updateLiveData(apiResponse)
    }

    private fun updateLiveData(apiResponse: LiveData<Resource<WeatherInfo>>) {
        liveWeatherInfo.addSource(apiResponse) { response ->
            if (response.status != Status.LOADING) {
                liveWeatherInfo.removeSource(apiResponse)

                if (response.status == Status.SUCCESS && response.data != null) {
                    //save fetchType and keyword for successful query
                    recentQuery.let {
                        if (!fromRecentSearch) {
                            it.desc = "${response.data.name} ${response.data.sys.country}"
                            addQueryToPreference(it)
                        }
                    }
                }
                if (response.status == Status.SUCCESS || response.status == Status.ERROR && fromRecentSearch) {
                    fromRecentSearch = false
                }
            }
            liveWeatherInfo.value = response
        }
    }

    fun getCityList(): List<City> {
        return cityRepo.getList()
    }

    private fun addQueryToPreference(query: Query) {
        queriesPreference.set(updateQueryList(query, queryList))
        queryList = queriesPreference.get()
    }

    companion object {
        fun isValidZipCode(input: String): Boolean {
            return input.length == 5 && isInteger(input)
        }

        /**
         *  Always add the new Query to the top and remove the last one if exceeds the max allowed number
         */
        fun updateQueryList(query: Query, inputList: List<Query>): List<Query> {
            val list = mutableListOf<Query>()
            list.add(query)
            list.addAll(1, inputList)
            if (list.size > PREFERENCE_QUERY_MAX) {
                list.removeAt(list.size - 1)
            }
            return list
        }

        private fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false
        const val PREFERENCE_QUERY_MAX: Int = 20
        var recentSearchedDone: Boolean = false
    }
}
