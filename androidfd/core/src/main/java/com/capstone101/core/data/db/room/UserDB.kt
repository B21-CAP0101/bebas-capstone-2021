package com.capstone101.core.data.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.capstone101.core.data.db.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class UserDB : RoomDatabase() {
    abstract fun getDao(): UserDao
}