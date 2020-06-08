package com.gmail.kenzhang0.api

import com.gmail.kenzhang0.vo.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryListConverterTest {
    private var moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private var converter = QueryListConverter(moshi)
    var emptyList = listOf<Query>()
    var queryList = mutableListOf<Query>(
        Query(1, "a"),
        Query(2, "b"),
        Query(3, "c")
    )

    @Test
    fun `serialize and deserialize happy path`() {
        var deEmptyList = converter.deserialize(converter.serialize(emptyList))
        assertEquals(deEmptyList.size, 0)
        var deQueryList = converter.deserialize(converter.serialize(queryList))

        deQueryList.let {
            assertEquals(it.size, 3)
            assertEquals(it[0].type, 1)
            assertEquals(it[0].input, "a")
            assertEquals(it[1].type, 2)
            assertEquals(it[1].input, "b")
            assertEquals(it[2].type, 3)
            assertEquals(it[2].input, "c")
        }

        queryList.add(Query(4, "d"))
        var list = converter.deserialize(converter.serialize(queryList))
        list.let {
            assertEquals(it.size, 4)
            assertEquals(it[0].type, 1)
            assertEquals(it[0].input, "a")
            assertEquals(it[1].type, 2)
            assertEquals(it[1].input, "b")
            assertEquals(it[2].type, 3)
            assertEquals(it[2].input, "c")
            assertEquals(it[3].type, 4)
            assertEquals(it[3].input, "d")
        }
    }

    @Test
    fun `deserialize empty string or invalid string`() {
        val list1 = converter.deserialize("")
        val list2 = converter.deserialize("{}")
        assertEquals(list1.size, 0)
        assertEquals(list2.size, 0)

    }
}