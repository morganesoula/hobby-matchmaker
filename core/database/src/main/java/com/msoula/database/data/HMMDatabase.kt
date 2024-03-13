package com.msoula.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msoula.database.data.dao.MovieDAO
import com.msoula.database.data.local.MovieEntity

@Database(entities = [MovieEntity::class], version = 7)
abstract class HMMDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDAO
}
