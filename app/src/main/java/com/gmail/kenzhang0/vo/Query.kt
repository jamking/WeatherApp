package com.gmail.kenzhang0.vo

data class Query(val type: Int, val input: String, var desc: String = "") {
    fun getTypeDisplay(): String {
        return when (QueryType.fromInt(type)) {
            QueryType.NONE -> QUERY_NONE_DISPLAY
            QueryType.ZIP -> QUERY_ZIP_DISPLAY
            QueryType.GPS -> QUERY_GPS_DISPLAY
            QueryType.CITY -> QUERY_CITY_DISPLAY
        }
    }

    companion object {
        private const val QUERY_NONE_DISPLAY = "None"
        private const val QUERY_ZIP_DISPLAY = "Zip Code:"
        private const val QUERY_GPS_DISPLAY = "Location:"
        private const val QUERY_CITY_DISPLAY = "City ID:"
        fun gpsQuery(latLon: LatLon) = Query(QueryType.GPS.type, latLon.toString())
    }
}

enum class QueryType(val type: Int) {
    NONE(0),
    ZIP(1),
    CITY(2),
    GPS(3);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull { it.type == value } ?: NONE
    }
}