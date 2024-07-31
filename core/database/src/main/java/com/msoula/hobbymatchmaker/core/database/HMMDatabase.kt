package com.msoula.hobbymatchmaker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.core.database.dao.converters.GenreTypeConverter
import com.msoula.hobbymatchmaker.core.database.dao.models.ActorEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieActorCrossEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel

@Database(
    entities = [MovieEntityModel::class, ActorEntityModel::class, MovieActorCrossEntityModel::class],
    version = 10
)
@TypeConverters(GenreTypeConverter::class)
abstract class HMMDatabase : RoomDatabase() {
    abstract fun movieDAO(): MovieDAO
}
