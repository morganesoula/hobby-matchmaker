package com.msoula.hobbymatchmaker.core.database.dao.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "movie")
data class MovieEntityModel(
    @PrimaryKey(autoGenerate = false)
    val movieId: Long = 0L,
    val title: String? = "",
    val posterFileName: String? = "",
    val synopsis: String? = "",
    val releaseDate: String? = "",
    val genres: List<Genre>? = emptyList(),
    val localCoverFilePath: String? = "",
    val isFavorite: Boolean? = false,
    val isSeen: Boolean? = false,
    val popularity: Double? = 0.0,
    val status: String? = "",
    val cast: List<Actor>? = emptyList(),
    val videoKey: String? = ""
)

@Entity
data class Genre(
    @PrimaryKey(autoGenerate = false)
    val genreId: Long = 0L,
    val name: String? = null
) {
    companion object {
        const val DEFAULT_ID: Int = -1
        const val DEFAULT_NAME: String = ""
    }
}

@Entity
data class Actor(
    @PrimaryKey(autoGenerate = false)
    val actorId: Long = 0L,
    val name: String? = null,
    val role: String? = null
)

@Entity(
    tableName = "movie_actor_cross_ref",
    primaryKeys = ["movieId", "actorId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntityModel::class,
            parentColumns = ["movieId"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Actor::class,
            parentColumns = ["actorId"],
            childColumns = ["actorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("movieId"), Index("actorId")]
)
data class MovieActorCrossRef(
    val movieId: Long,
    val actorId: Long
)

data class MovieWithActors(
    @Embedded val movie: MovieEntityModel,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "actorId",
        associateBy = Junction(MovieActorCrossRef::class)
    )
    val actors: List<Actor>
)
