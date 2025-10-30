package com.msoula.hobbymatchmaker.core.database

import app.cash.sqldelight.db.SqlDriver

const val DATABASE_NAME: String = "hmm_local.db"

interface DriverFactory {
    fun createDRiver(): SqlDriver
}
