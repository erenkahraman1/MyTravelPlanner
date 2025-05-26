package com.erenkahraman.mytravelplanner

data class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val note: String? = null,
    val photoUri: String? = null
)
