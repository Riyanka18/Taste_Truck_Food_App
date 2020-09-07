package com.example.spicezilla.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HomeEntity::class,OrderEntity::class], version = 1, exportSchema = false)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun restaurantDao():HomeDao
    abstract fun orderDao():OrderDao
}
