package com.example.dineout.data.room

import androidx.room.*

@Dao
interface RestaurantDao {
    @Transaction
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<RestaurantWithDishes>

    @Transaction
    @Query("SELECT * FROM restaurants WHERE status = :status")
    fun getByStatus(status: String): List<RestaurantWithDishes>

    @Transaction
    @Query("SELECT * FROM restaurants WHERE name = :name LIMIT 1")
    fun getByName(name: String): RestaurantWithDishes?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(entity: RestaurantEntity): Long

    @Update
    fun updateRestaurant(entity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(entity: RestaurantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDishes(dishes: List<DishEntity>)

    @Query("DELETE FROM dishes WHERE restaurantId = :restaurantId")
    fun deleteDishesForRestaurant(restaurantId: Long)
}
