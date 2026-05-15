package com.example.dineout.data

import androidx.room.*
import com.example.dineout.models.Restaurant

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    suspend fun getAll(): List<Restaurant>

    @Query("SELECT * FROM restaurants WHERE status = :status")
    suspend fun getByStatus(status: String): List<Restaurant>

    @Query("SELECT * FROM restaurants WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Restaurant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restaurant: Restaurant)

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)
    
    @Query("DELETE FROM restaurants WHERE name = :name")
    suspend fun deleteByName(name: String)
}
