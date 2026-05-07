package com.example.dineout.models

data class Restaurant(
    val name: String,
    val address: String,
    val cuisine: String,
    val priceRange: String,
    val mustTryDish: String,
    var rating: Int,
    var status: String,
    val visitDate: String,
    val mealTime: String,
    val occasions: String,
    var notes: String,
    var spendAmount: Int = 0,
    var worthRating: Int = 0,
    val dishes: MutableList<Dish> = mutableListOf()
)