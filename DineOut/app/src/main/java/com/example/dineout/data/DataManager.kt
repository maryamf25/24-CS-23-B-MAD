package com.example.dineout.data

import com.example.dineout.models.Restaurant

object DataManager {

    val restaurantList = ArrayList<Restaurant>()

    fun loadSampleData() {
        if (restaurantList.isEmpty()) {
            restaurantList.add(Restaurant("Monal", "Pir Sohawa", "Pakistani", "$$$", "Cheese Naan", 4, "wishlist", "", "", "Family", "Must visit at night"))
            restaurantList.add(Restaurant("Cheezious", "Johar Town", "Fast Food", "$$", "Crown Crust", 4, "visited", "12/03/2026", "8:00 PM", "Friends", "Best pizza in town", spendAmount = 1400, worthRating = 4))
            restaurantList.add(Restaurant("BBQ Masters", "Defense", "BBQ", "$$", "Mixed Grill", 5, "visited", "10/03/2026", "7:30 PM", "Friends", "Amazing grilled meat", spendAmount = 2100, worthRating = 5))
            restaurantList.add(Restaurant("Arcadian Cafe", "Gulberg", "Continental", "$$$", "Stuffed Chicken", 3, "wishlist", "", "", "Date Night", "Good ambiance"))
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
    fun getRestaurantByName(name: String): Restaurant? {
        return restaurantList.find { it.name == name }
    }

    fun deleteRestaurant(name: String) {
        val iterator = restaurantList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().name == name) {
                iterator.remove()
                break
            }
        }
    }
    fun markAsVisited(name: String, newRating: Int, newNotes: String, spend: Int, worth: Int) {
        val restaurant = getRestaurantByName(name)
        if (restaurant != null) {
            restaurant.status = "visited"
            restaurant.rating = newRating
            restaurant.notes = newNotes
            restaurant.spendAmount = spend
            restaurant.worthRating = worth
        }
    }
}