package com.msoula.hobbymatchmaker.core.database

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriver {
    fun createDriver(): SqlDriver
}

const val DATABASE_NAME: String = "hmm_local.db"
