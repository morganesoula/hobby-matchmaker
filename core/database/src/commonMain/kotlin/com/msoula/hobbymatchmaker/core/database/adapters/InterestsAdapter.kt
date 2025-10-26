package com.msoula.hobbymatchmaker.core.database.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object InterestsAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) emptyList()
        else Json.decodeFromString(ListSerializer(String.serializer()), databaseValue)

    override fun encode(value: List<String>): String =
        Json.encodeToString(ListSerializer(String.serializer()), value)
}
