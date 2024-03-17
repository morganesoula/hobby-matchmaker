package com.msoula.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msoula.hobbymatchmaker.core.dao.MovieDAO
import com.msoula.hobbymatchmaker.core.dao.models.MovieEntityModel

@Database(entities = [MovieEntityModel::class], version = 7)
abstract class HMMDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDAO
}
