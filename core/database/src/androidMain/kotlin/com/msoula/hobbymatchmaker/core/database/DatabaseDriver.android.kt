package com.msoula.hobbymatchmaker.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriver(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = HMMDatabase.Schema,
        context = context,
        name = DATABASE_NAME
    )
}
