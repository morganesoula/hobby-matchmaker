package com.msoula.hobbymatchmaker.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<HMMLocalDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("local_hmm_database.db")

    return Room.databaseBuilder<HMMLocalDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

