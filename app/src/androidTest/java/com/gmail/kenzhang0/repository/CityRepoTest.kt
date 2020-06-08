package com.gmail.kenzhang0.repository

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.gmail.kenzhang0.di.CITY_LIST_FILE_NAME
import com.gmail.kenzhang0.vo.City
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CityRepoTest {
    private val ctx = InstrumentationRegistry.getInstrumentation().context
    private val targetCtx = InstrumentationRegistry.getInstrumentation().targetContext
    private val repo = CityRepo(ctx, CITY_LIST_FILE_NAME)
    private val repoApp = CityRepo(targetCtx, CITY_LIST_FILE_NAME)
    private val cities = listOf(
        City(0, "A"),
        City(1, "Syd"),
        City(2, "Syda"),
        City(3, "Sydb"),
        City(4, "Sydn"),
        City(5, "Sydney"),
        City(6, "Sydney"),
        City(7, "Z")
    )

    @Before
    fun setup() {

    }

    @Test
    fun loadCitiesFromFileTest() {
        repo.initCitiesFromFile()
        val cities = repo.getList()
        cities.let {
            assertEquals(it.size, 3)

            assertEquals(it[0].id, 5)
            assertEquals(it[0].name, "A")
            assertEquals(it[0].country, "UK")

            assertEquals(it[1].id, 3)
            assertEquals(it[1].name, "C")
            assertEquals(it[1].country, "US")

            assertEquals(it[2].id, 1)
            assertEquals(it[2].name, "D")
            assertEquals(it[2].country, "CN")
        }
    }

    @LargeTest
    @Test
    fun loadCitiesFromFileAppRepoTest() {
        repoApp.initCitiesFromFile()
        val cities = repoApp.getList()
        val list = CityRepo.findCitiesByPrefix(cities, "Syd")
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun findCitiesByPrefixTest() {
        val list1 = CityRepo.findCitiesByPrefix(cities, "Syd")
        assertEquals(list1.size, 6)
        val list2 = CityRepo.findCitiesByPrefix(cities, "Sydn")
        assertEquals(list2.size, 3)
        val list3 = CityRepo.findCitiesByPrefix(cities, "Sydney")
        assertEquals(list3.size, 2)
    }
}