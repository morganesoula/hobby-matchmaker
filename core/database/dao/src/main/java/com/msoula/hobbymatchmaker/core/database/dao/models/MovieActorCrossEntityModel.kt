package com.msoula.hobbymatchmaker.core.database.dao.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["movieId", "actorId"])
data class MovieActorCrossEntityModel(
    val movieId: Long,
    val actorId: Long
)

data class MovieWithActorsEntityModel(
    @Embedded val movie: MovieEntityModel,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "actorId",
        associateBy = Junction(MovieActorCrossEntityModel::class)
    )
    val actors: List<ActorEntityModel>
)
