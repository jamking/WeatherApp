package com.gmail.kenzhang0.vo

data class City(
    val id: Int = 0,
    val name: String = "",
    val country: String = ""
) {
    override fun toString(): String = "$name, $country, $id"
}
