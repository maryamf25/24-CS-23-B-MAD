package com.example.dineout.data

import android.content.Context
import com.example.dineout.auth.AuthManager
import com.example.dineout.data.room.AppDatabase
import com.example.dineout.data.room.DishEntity
import com.example.dineout.data.room.RestaurantEntity
import com.example.dineout.data.room.RestaurantWithDishes
import com.example.dineout.models.Dish
import com.example.dineout.models.Restaurant

object DataManager {
    private const val LEGACY_DATA_FILE = "restaurants.json"

    val restaurantList = ArrayList<Restaurant>()

    fun loadData(context: Context) {
        val storageKey = AuthManager.currentStorageKey(context)
        // remove legacy JSON file if present (no longer used)
        try {
            context.deleteFile(LEGACY_DATA_FILE)
        } catch (_: Exception) {
        }
        val dao = AppDatabase.getInstance(context, storageKey).restaurantDao()
        val persisted = dao.getAllRestaurants().map { fromRelation(it) }

        restaurantList.clear()

        restaurantList.addAll(persisted)
    }

    fun getByStatus(status: String): ArrayList<Restaurant> {
        val filteredList = ArrayList<Restaurant>()
        for (res in restaurantList) {
            if (res.status == status) filteredList.add(res)
        }
        return filteredList
    }

    fun addRestaurant(context: Context, restaurant: Restaurant) {
        val dao = AppDatabase.getInstance(context, AuthManager.currentStorageKey(context)).restaurantDao()
        val id = dao.insertRestaurant(toEntity(restaurant))
        val dishes = toDishEntities(id, restaurant.dishes)
        if (dishes.isNotEmpty()) dao.insertDishes(dishes)
        restaurantList.add(restaurant)
    }

    fun updateRestaurant(context: Context, oldName: String, updatedRestaurant: Restaurant) {
        val dao = AppDatabase.getInstance(context, AuthManager.currentStorageKey(context)).restaurantDao()
        val existing = dao.getByName(oldName)
        if (existing != null) {
            val entity = toEntity(updatedRestaurant).copy(id = existing.restaurant.id)
            dao.updateRestaurant(entity)
            dao.deleteDishesForRestaurant(existing.restaurant.id)
            val dishes = toDishEntities(existing.restaurant.id, updatedRestaurant.dishes)
            if (dishes.isNotEmpty()) dao.insertDishes(dishes)
            val index = restaurantList.indexOfFirst { it.name == oldName }
            if (index != -1) restaurantList[index] = updatedRestaurant
        } else {
            addRestaurant(context, updatedRestaurant)
        }
    }

    fun getRestaurantByName(name: String): Restaurant? {
        return restaurantList.find { it.name == name }
    }

    fun deleteRestaurant(context: Context, name: String) {
        val dao = AppDatabase.getInstance(context, AuthManager.currentStorageKey(context)).restaurantDao()
        val existing = dao.getByName(name)
        if (existing != null) {
            dao.deleteDishesForRestaurant(existing.restaurant.id)
            dao.deleteRestaurant(existing.restaurant)
        }
        val iterator = restaurantList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().name == name) {
                iterator.remove()
                break
            }
        }
    }

    fun markAsVisited(context: Context, name: String, newRating: Int, newNotes: String, spend: Int, worth: Int, photoPath: String? = null) {
        val dao = AppDatabase.getInstance(context, AuthManager.currentStorageKey(context)).restaurantDao()
        val existing = dao.getByName(name)
        if (existing != null) {
            val updated = existing.restaurant.copy(
                rating = newRating,
                status = "visited",
                notes = newNotes,
                spendAmount = spend,
                worthRating = worth
                ,photoPath = photoPath ?: existing.restaurant.photoPath
            )
            dao.updateRestaurant(updated)
            val index = restaurantList.indexOfFirst { it.name == name }
            if (index != -1) {
                val r = restaurantList[index]
                r.status = "visited"
                r.rating = newRating
                r.notes = newNotes
                r.spendAmount = spend
                r.worthRating = worth
                if (!photoPath.isNullOrEmpty()) r.photoPath = photoPath
            }
        }
    }

    private fun toEntity(r: Restaurant): RestaurantEntity {
        return RestaurantEntity(
            name = r.name,
            address = r.address,
            cuisine = r.cuisine,
            priceRange = normalizePriceRange(r.priceRange),
            mustTryDish = r.mustTryDish,
            rating = r.rating,
            status = r.status,
            visitDate = r.visitDate,
            mealTime = r.mealTime,
            occasions = r.occasions,
            notes = r.notes,
            spendAmount = r.spendAmount,
            worthRating = r.worthRating
            ,photoPath = r.photoPath
        )
    }

    private fun toDishEntities(restaurantId: Long, dishes: List<Dish>): List<DishEntity> {
        return dishes.map { d ->
            DishEntity(
                restaurantId = restaurantId,
                name = d.name,
                rating = d.rating,
                courseType = d.courseType,
                wouldOrderAgain = d.wouldOrderAgain
            )
        }
    }

    private fun fromRelation(rwd: RestaurantWithDishes): Restaurant {
        val dishes = rwd.dishes.map { d ->
            Dish(
                name = d.name,
                rating = d.rating,
                courseType = d.courseType,
                wouldOrderAgain = d.wouldOrderAgain
            )
        }.toMutableList()

        return Restaurant(
            name = rwd.restaurant.name,
            address = rwd.restaurant.address,
            cuisine = rwd.restaurant.cuisine,
            priceRange = normalizePriceRange(rwd.restaurant.priceRange),
            mustTryDish = rwd.restaurant.mustTryDish,
            rating = rwd.restaurant.rating,
            status = rwd.restaurant.status,
            visitDate = rwd.restaurant.visitDate,
            mealTime = rwd.restaurant.mealTime,
            occasions = rwd.restaurant.occasions,
            notes = rwd.restaurant.notes,
            spendAmount = rwd.restaurant.spendAmount,
            worthRating = rwd.restaurant.worthRating,
            photoPath = rwd.restaurant.photoPath,
            dishes = dishes
        )
    }

    private fun getSampleData(): List<Restaurant> {
        return listOf(
            Restaurant("Haveli Restaurant", "Badshahi Mosque, Walled City", "Pakistani", "Rs Rs Rs", "Mutton Karahi", 5, "visited", "12/04/2026", "8:30 PM", "Family", "Incredible view of Badshahi Mosque", spendAmount = 4500, worthRating = 5),
            Restaurant("Cafe Aylanto", "MM Alam Road, Gulberg", "Continental", "Rs Rs Rs", "Moroccan Chicken", 5, "wishlist", "", "", "Date Night", "Premium dining experience"),
            Restaurant("Daily Deli Co.", "Johar Town", "Fast Food", "Rs Rs", "Smash Burger", 4, "visited", "18/04/2026", "7:00 PM", "Friends", "Best juicy burgers in town", spendAmount = 1400, worthRating = 4),
            Restaurant("Bhaiyya Kabab Shop", "Model Town", "BBQ", "Rs", "Beef Kabab", 4, "visited", "20/03/2026", "9:00 PM", "Family", "Legendary street food vibe", spendAmount = 600, worthRating = 5),
            Restaurant("Monal Lahore", "Liberty Roundabout, Gulberg", "Mixed/Desi", "Rs Rs Rs", "Cheese Naan", 4, "wishlist", "", "", "Friends", "Great rooftop view at night")
        )
    }

    private fun normalizePriceRange(value: String): String {
        return when (value) {
            "$" -> "Rs"
            "$$" -> "Rs Rs"
            "$$$" -> "Rs Rs Rs"
            else -> value
        }
    }
}