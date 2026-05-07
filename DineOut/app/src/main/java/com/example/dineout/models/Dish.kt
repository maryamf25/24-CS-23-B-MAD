package com.example.dineout.models

data class Dish(
    val name: String,
    val rating: Int,
    val courseType: String, // Starter, Main, Dessert, Drink
    val wouldOrderAgain: Boolean = false
)
