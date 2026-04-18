package com.example.dineout.data

// Explicitly import your models to resolve "Unresolved reference"
import com.example.dineout.models.Restaurant

object RestaurantFilter {

    fun filterResults(
        list: List<Restaurant>?, // Use nullable list to prevent crashes if DataManager fails
        cuisine: String,
        mustTry: String,
        price: String
    ): ArrayList<Restaurant> {

        // If the input list is null, return an empty ArrayList immediately
        val baseList = list ?: return ArrayList()

        val filtered = baseList.filter { res ->
            // 1. Cuisine Match
            val matchesCuisine = cuisine.isEmpty() ||
                    res.cuisine.contains(cuisine, ignoreCase = true)

            // 2. Must Try / Occasion Match
            // Note: Ensure your Restaurant model has these exact property names
            val matchesMustTry = mustTry.isEmpty() ||
                    res.mustTryDish.contains(mustTry, ignoreCase = true) ||
                    res.occasions.contains(mustTry, ignoreCase = true)

            // 3. Price Match mapping
            val matchesPrice = when {
                price.isEmpty() || price == "All" -> true
                price.contains("Cheap", true) -> res.priceRange == "$"
                price.contains("Moderate", true) -> res.priceRange == "$$"
                price.contains("Expensive", true) -> res.priceRange == "$$$"
                else -> res.priceRange.contains(price, ignoreCase = true)
            }

            matchesCuisine && matchesMustTry && matchesPrice
        }

        return ArrayList(filtered)
    }
}