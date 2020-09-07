package com.example.spicezilla.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HomeDao{

    @Insert
    fun insertRestaurant(restaurantEntity: HomeEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: HomeEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<HomeEntity>

    @Query("SELECT * FROM restaurants WHERE id = :resId")
    fun getRestaurantById(resId: String):HomeEntity
}