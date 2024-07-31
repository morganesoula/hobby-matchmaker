package com.msoula.hobbymatchmaker.core.database.dao.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actors")
data class ActorEntityModel(
    @PrimaryKey(autoGenerate = true) val actorId: Long,
    val name: String,
    val role: String
)
