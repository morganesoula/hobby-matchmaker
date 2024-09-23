package com.msoula.hobbymatchmaker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.core.database.dao.converters.Converters
import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieActorCrossRef
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel

@Database(
    entities = [MovieEntityModel::class, Actor::class, MovieActorCrossRef::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class HMMLocalDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDAO
}
