package com.example.dineout.data

import com.example.dineout.models.Restaurant

object DataManager {

    val restaurantList = ArrayList<Restaurant>()

    fun loadSampleData() {
        if (restaurantList.isEmpty()) {
            restaurantList.add(Restaurant("Monal", "Pir Sohawa", "Pakistani", "$$$", "Cheese Naan", 9, "wishlist", "", "", "Family, View", "Must visit at night"))
            restaurantList.add(Restaurant("Cheezious", "Johar Town", "Fast Food", "$$", "Crown Crust", 8, "visited", "12/03/2026", "8:00 PM", "Friends", "Best pizza in town"))
            restaurantList.add(Restaurant("Arcadian Cafe", "Gulberg", "Continental", "$$$", "Stuffed Chicken", 7, "wishlist", "", "", "Date Night", "Good ambiance"))
        }
    }

    fun getByStatus(status: String): ArrayList<Restaurant> {
        val filteredList = ArrayList<Restaurant>()
        for (res in restaurantList) {
            if (res.status == status) {
                filteredList.add(res)
            }
        }
        return filteredList
    }

    fun addRestaurant(restaurant: Restaurant) {
        restaurantList.add(restaurant)
    }
}