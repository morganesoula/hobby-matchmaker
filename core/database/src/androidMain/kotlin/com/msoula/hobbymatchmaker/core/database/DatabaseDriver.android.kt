package com.msoula.hobbymatchmaker.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDriverFactory(
    private val context: Context
) : DriverFactory {

    override fun createDRiver(): SqlDriver =
        AndroidSqliteDriver(
            schema = HMMDatabase.Schema,
            context = context,
            name = DATABASE_NAME
        )
}
