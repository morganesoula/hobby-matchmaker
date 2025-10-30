package com.msoula.hobbymatchmaker.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class IosDriverFactory : DriverFactory {
    override fun createDRiver(): SqlDriver =
        NativeSqliteDriver(HMMDatabase.Schema, DATABASE_NAME)
}
