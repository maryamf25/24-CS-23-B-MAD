package com.example.dineout.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dishes",
    foreignKeys = [ForeignKey(
        entity = RestaurantEntity::class,
        parentColumns = ["id"],
        childColumns = ["restaurantId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("restaurantId")]
)
data class DishEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val restaurantId: Long,
    val name: String,
    val rating: Int,
    val courseType: String,
    val wouldOrderAgain: Boolean = false
)
