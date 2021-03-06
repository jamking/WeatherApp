package com.gmail.kenzhang0.repository

import android.content.Context
import com.gmail.kenzhang0.vo.City
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.util.*


class CityRepo(private val context: Context, private val fileName: String) {
    private var cityList: MutableList<City> = mutableListOf()
    fun initCitiesFromFile() {
        val jsonString: String =
            context.assets.open(fileName).bufferedReader().use { it.readText() }

        val type = Types.newParameterizedType(List::class.java, City::class.java)
        val moshi =
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter<List<City>>(type)

        try {
            cityList.addAll(moshi.fromJson(jsonString) ?: listOf())
            Collections.sort(cityList, compareBy(City::name))
        } catch (e: IOException) {
        }
    }

    fun getList(): List<City> {
        return cityList
    }

    companion object {
        fun findMatchedCities(cityList: List<City>, input: String): List<City> {
            val capitalized = input.capitalize()
            val list = mutableListOf<City>()
            for (city in cityList) {
                if (city.name.startsWith(capitalized)) {
                    list.add(city)
                }
            }
            return list
        }

        /**
         *  binarySearch to find the city list with the same prefix.
         */
        fun findCitiesByPrefix(cityList: List<City>, prefix: String): List<City> {
            val ignoreCase: Boolean = true
            val prefixComparator: Comparator<City> =
                Comparator { a, b ->
                    if (a.name.length < prefix.length)
                        a.name.compareTo(b.name, true)
                    else
                        a.name.substring(0, prefix.length).compareTo(b.name, ignoreCase)
                }

            val index = Collections.binarySearch(cityList, City(0, prefix, ""), prefixComparator)
            val list = mutableListOf<City>()
            if (index < 0) {
                return list
            }
            list.add(cityList[index])
            var i = index + 1
            while (i < cityList.size && cityList[i].name.startsWith(prefix, ignoreCase)) {
                list.add(cityList[i])
                i++
            }
            i = index - 1
            while (i >= 0 && cityList[i].name.startsWith(prefix, ignoreCase)) {
                list.add(cityList[i])
                i--
            }
            Collections.sort(list, compareBy(City::name))
            return list
        }
    }
}