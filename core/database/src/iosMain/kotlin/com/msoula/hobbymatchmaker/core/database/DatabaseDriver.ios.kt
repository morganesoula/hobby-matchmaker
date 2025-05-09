package com.msoula.hobbymatchmaker.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriver {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = HMMDatabase.Schema,
            name = DATABASE_NAME
        )
    }
}
