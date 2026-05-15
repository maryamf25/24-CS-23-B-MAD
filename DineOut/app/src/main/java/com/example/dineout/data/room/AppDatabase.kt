package com.example.dineout.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.ConcurrentHashMap

@Database(entities = [RestaurantEntity::class, DishEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        private val instances = ConcurrentHashMap<String, AppDatabase>()

        fun getInstance(context: Context, storageKey: String = "guest"): AppDatabase {
            val databaseName = "dineout-db-$storageKey"
            return instances[databaseName] ?: synchronized(this) {
                instances[databaseName] ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    databaseName
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { instances[databaseName] = it }
            }
        }
    }
}
