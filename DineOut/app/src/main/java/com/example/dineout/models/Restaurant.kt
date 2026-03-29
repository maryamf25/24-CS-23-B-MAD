package com.example.dineout.models

data class Restaurant(
    val name: String,
    val address: String,
    val cuisine: String,
    val priceRange: String,
    val mustTryDish: String,
    val rating: Int,
    val status: String,
    val visitDate: String = "",
    val mealTime: String = "",
    val occasions: String = "",
    val notes: String = ""
)

