package com.example.dineout.data

import android.content.Context
import com.example.dineout.models.Dish
import com.example.dineout.models.Restaurant
import org.json.JSONArray
import org.json.JSONObject

object DataManager {

    private const val DATA_FILE_NAME = "restaurants.json"
    val restaurantList = ArrayList<Restaurant>()

    fun loadData(context: Context) {
        if (restaurantList.isNotEmpty()) return

        val persisted = readFromJsonFile(context)
        if (persisted.isNotEmpty()) {
            restaurantList.addAll(persisted)
            return
        }

        // First run fallback.
        restaurantList.addAll(getSampleData())
        persistData(context)
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

    fun addRestaurant(context: Context, restaurant: Restaurant) {
        restaurantList.add(restaurant)
        persistData(context)
    }

    fun updateRestaurant(context: Context, oldName: String, updatedRestaurant: Restaurant) {
        val index = restaurantList.indexOfFirst { it.name == oldName }
        if (index != -1) {
            restaurantList[index] = updatedRestaurant
        } else {
            restaurantList.add(updatedRestaurant)
        }
        persistData(context)
    }

    fun getRestaurantByName(name: String): Restaurant? {
        return restaurantList.find { it.name == name }
    }

    fun deleteRestaurant(context: Context, name: String) {
        val iterator = restaurantList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().name == name) {
                iterator.remove()
                break
            }
        }
        persistData(context)
    }

    fun markAsVisited(context: Context, name: String, newRating: Int, newNotes: String, spend: Int, worth: Int) {
        val restaurant = getRestaurantByName(name)
        if (restaurant != null) {
            restaurant.status = "visited"
            restaurant.rating = newRating
            restaurant.notes = newNotes
            restaurant.spendAmount = spend
            restaurant.worthRating = worth
            persistData(context)
        }
    }

    private fun persistData(context: Context) {
        val root = JSONObject()
        val restaurantsArray = JSONArray()

        for (restaurant in restaurantList) {
            val restaurantObj = JSONObject().apply {
                put("name", restaurant.name)
                put("address", restaurant.address)
                put("cuisine", restaurant.cuisine)
                put("priceRange", restaurant.priceRange)
                put("mustTryDish", restaurant.mustTryDish)
                put("rating", restaurant.rating)
                put("status", restaurant.status)
                put("visitDate", restaurant.visitDate)
                put("mealTime", restaurant.mealTime)
                put("occasions", restaurant.occasions)
                put("notes", restaurant.notes)
                put("spendAmount", restaurant.spendAmount)
                put("worthRating", restaurant.worthRating)

                val dishesArray = JSONArray()
                for (dish in restaurant.dishes) {
                    val dishObj = JSONObject().apply {
                        put("name", dish.name)
                        put("rating", dish.rating)
                        put("courseType", dish.courseType)
                        put("wouldOrderAgain", dish.wouldOrderAgain)
                    }
                    dishesArray.put(dishObj)
                }
                put("dishes", dishesArray)
            }
            restaurantsArray.put(restaurantObj)
        }

        root.put("restaurants", restaurantsArray)
        context.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE).use { output ->
            output.write(root.toString().toByteArray())
        }
    }

    private fun readFromJsonFile(context: Context): List<Restaurant> {
        return try {
            val jsonString = context.openFileInput(DATA_FILE_NAME).bufferedReader().use { it.readText() }
            if (jsonString.isBlank()) return emptyList()

            val root = JSONObject(jsonString)
            val array = root.optJSONArray("restaurants") ?: return emptyList()
            val parsed = ArrayList<Restaurant>()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val dishesJson = item.optJSONArray("dishes") ?: JSONArray()
                val dishes = mutableListOf<Dish>()

                for (j in 0 until dishesJson.length()) {
                    val dishObj = dishesJson.getJSONObject(j)
                    dishes.add(
                        Dish(
                            name = dishObj.optString("name"),
                            rating = dishObj.optInt("rating", 0),
                            courseType = dishObj.optString("courseType"),
                            wouldOrderAgain = dishObj.optBoolean("wouldOrderAgain", false)
                        )
                    )
                }

                parsed.add(
                    Restaurant(
                        name = item.optString("name"),
                        address = item.optString("address"),
                        cuisine = item.optString("cuisine"),
                        priceRange = item.optString("priceRange"),
                        mustTryDish = item.optString("mustTryDish"),
                        rating = item.optInt("rating", 0),
                        status = item.optString("status", "wishlist"),
                        visitDate = item.optString("visitDate"),
                        mealTime = item.optString("mealTime"),
                        occasions = item.optString("occasions"),
                        notes = item.optString("notes"),
                        spendAmount = item.optInt("spendAmount", 0),
                        worthRating = item.optInt("worthRating", 0),
                        dishes = dishes
                    )
                )
            }

            parsed
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun getSampleData(): List<Restaurant> {
        return listOf(
            Restaurant("Monal", "Pir Sohawa", "Pakistani", "$$$", "Cheese Naan", 4, "wishlist", "", "", "Family", "Must visit at night"),
            Restaurant("Cheezious", "Johar Town", "Fast Food", "$$", "Crown Crust", 4, "visited", "12/03/2026", "8:00 PM", "Friends", "Best pizza in town", spendAmount = 1400, worthRating = 4),
            Restaurant("BBQ Masters", "Defense", "BBQ", "$$", "Mixed Grill", 5, "visited", "10/03/2026", "7:30 PM", "Friends", "Amazing grilled meat", spendAmount = 2100, worthRating = 5),
            Restaurant("Arcadian Cafe", "Gulberg", "Continental", "$$$", "Stuffed Chicken", 3, "wishlist", "", "", "Date Night", "Good ambiance")
        )
    }
}