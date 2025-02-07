package com.msoula.hobbymatchmaker.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localAndroidDatabaseModule = module {
    single<HMMLocalDatabase> {
        configureRoomBuilder(androidContext()).build()
    }
}

private fun configureRoomBuilder(context: Context): RoomDatabase.Builder<HMMLocalDatabase> {
    val dbFile = context.getDatabasePath("local-hmm-database")

    return Room.databaseBuilder(
        context = context,
        klass = HMMLocalDatabase::class.java,
        name = dbFile.absolutePath
    )
}
