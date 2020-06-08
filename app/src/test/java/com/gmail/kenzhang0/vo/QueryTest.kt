package com.gmail.kenzhang0.vo

import org.junit.Assert.assertEquals
import org.junit.Test

class QueryTest {

    @Test
    fun fromIntTest() {
        assertEquals(QueryType.fromInt(0), QueryType.NONE)
        assertEquals(QueryType.fromInt(1), QueryType.ZIP)
        assertEquals(QueryType.fromInt(2), QueryType.CITY)
        assertEquals(QueryType.fromInt(3), QueryType.GPS)
        assertEquals(QueryType.fromInt(-1), QueryType.NONE)
    }
}