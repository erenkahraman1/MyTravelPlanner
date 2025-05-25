package com.erenkahraman.mytravelplanner

data class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    var note: String = ""
)
