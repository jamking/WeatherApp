package com.gmail.kenzhang0.api

import com.f2prateek.rx.preferences2.Preference
import com.gmail.kenzhang0.vo.Query
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class QueryListConverter(moshi: Moshi) : Preference.Converter<List<Query>> {
    private var serializer: JsonAdapter<List<Query>>? = moshi.adapter(Types.newParameterizedType(List::class.java, Query::class.java))

    override fun deserialize(serialized: String): List<Query> {
        try {
            return serializer?.fromJson(serialized) ?: listOf()
        } catch (e: Exception) {
            return listOf()
        }
    }

    override fun serialize(value: List<Query>): String {
        return serializer?.toJson(value) ?: ""
    }
}