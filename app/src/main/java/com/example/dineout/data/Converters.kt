package com.example.dineout.data

import androidx.room.TypeConverter
import com.example.dineout.models.Dish
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromDishList(value: MutableList<Dish>): String {
        val gson = Gson()
        val type = object : TypeToken<MutableList<Dish>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toDishList(value: String): MutableList<Dish> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<Dish>>() {}.type
        return gson.fromJson(value, type)
    }
}
