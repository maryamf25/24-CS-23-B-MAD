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
        // We clear the list first to ensure we don't have duplicates when refreshing
        val persisted = readFromJsonFile(context)

        restaurantList.clear()

        if (persisted.isNotEmpty()) {
            restaurantList.addAll(persisted)
        } else {
            // First run fallback: if no file exists, load samples and save them
            restaurantList.addAll(getSampleData())
            persistData(context)
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
            Restaurant("Haveli Restaurant", "Badshahi Mosque, Walled City", "Pakistani", "$$$", "Mutton Karahi", 5, "visited", "12/04/2026", "8:30 PM", "Family", "Incredible view of Badshahi Mosque", spendAmount = 4500, worthRating = 5),
            Restaurant("Cafe Aylanto", "MM Alam Road, Gulberg", "Continental", "$$$", "Moroccan Chicken", 5, "wishlist", "", "", "Date Night", "Premium dining experience"),
            Restaurant("Daily Deli Co.", "Johar Town", "Fast Food", "$$", "Smash Burger", 4, "visited", "18/04/2026", "7:00 PM", "Friends", "Best juicy burgers in town", spendAmount = 1400, worthRating = 4),
            Restaurant("Bhaiyya Kabab Shop", "Model Town", "BBQ", "$", "Beef Kabab", 4, "visited", "20/03/2026", "9:00 PM", "Family", "Legendary street food vibe", spendAmount = 600, worthRating = 5),
            Restaurant("Monal Lahore", "Liberty Roundabout, Gulberg", "Mixed/Desi", "$$$", "Cheese Naan", 4, "wishlist", "", "", "Friends", "Great rooftop view at night")
        )
    }
}