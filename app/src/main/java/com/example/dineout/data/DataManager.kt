package com.example.dineout.data

import android.content.Context
import com.example.dineout.models.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataManager {

    val restaurantList = ArrayList<Restaurant>()

    suspend fun loadData(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val persisted = withContext(Dispatchers.IO) {
            database.restaurantDao().getAll()
        }

        restaurantList.clear()

        if (persisted.isNotEmpty()) {
            restaurantList.addAll(persisted)
        } else {
            val samples = getSampleData()
            restaurantList.addAll(samples)
            withContext(Dispatchers.IO) {
                samples.forEach { database.restaurantDao().insert(it) }
            }
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

    suspend fun addRestaurant(context: Context, restaurant: Restaurant) {
        restaurantList.add(restaurant)
        withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(context).restaurantDao().insert(restaurant)
        }
    }

    suspend fun updateRestaurant(context: Context, oldName: String, updatedRestaurant: Restaurant) {
        val index = restaurantList.indexOfFirst { it.name == oldName }
        if (index != -1) {
            restaurantList[index] = updatedRestaurant
        } else {
            restaurantList.add(updatedRestaurant)
        }
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(context).restaurantDao()
            if (oldName != updatedRestaurant.name) {
                dao.deleteByName(oldName)
            }
            dao.insert(updatedRestaurant)
        }
    }

    fun getRestaurantByName(name: String): Restaurant? {
        return restaurantList.find { it.name == name }
    }

    suspend fun deleteRestaurant(context: Context, name: String) {
        val iterator = restaurantList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().name == name) {
                iterator.remove()
                break
            }
        }
        withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(context).restaurantDao().deleteByName(name)
        }
    }

    suspend fun markAsVisited(context: Context, name: String, newRating: Int, newNotes: String, spend: Int, worth: Int) {
        val restaurant = getRestaurantByName(name)
        if (restaurant != null) {
            restaurant.status = "visited"
            restaurant.rating = newRating
            restaurant.notes = newNotes
            restaurant.spendAmount = spend
            restaurant.worthRating = worth
            
            withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(context).restaurantDao().update(restaurant)
            }
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
