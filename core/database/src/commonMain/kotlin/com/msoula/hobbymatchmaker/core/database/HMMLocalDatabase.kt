package com.msoula.hobbymatchmaker.core.database

import androidx.room.BuiltInTypeConverters
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
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
@TypeConverters(
    Converters::class, builtInTypeConverters = BuiltInTypeConverters()
)
@ConstructedBy(HMMLocalDatabaseConstructor::class)
abstract class HMMLocalDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDAO
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object HMMLocalDatabaseConstructor : RoomDatabaseConstructor<HMMLocalDatabase> {
    override fun initialize(): HMMLocalDatabase
}
