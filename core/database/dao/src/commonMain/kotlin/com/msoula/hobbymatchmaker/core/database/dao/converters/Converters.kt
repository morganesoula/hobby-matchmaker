package com.msoula.hobbymatchmaker.core.database.dao.converters

import androidx.room.TypeConverter
import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.Genre
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    val jsonParser = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    @TypeConverter
    fun fromGenreStringList(genres: List<String>?): String? {
        return genres?.joinToString(",")
    }

    @TypeConverter
    fun toGenreStringList(data: String?): List<String>? {
        return data?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return genres?.let { jsonParser.encodeToString(it) }
    }

    @TypeConverter
    fun toGenreList(genreString: String?): List<Genre> {
        return genreString?.let { jsonParser.decodeFromString<List<Genre>>(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromActorList(actors: List<Actor>?): String? {
        return actors?.let { jsonParser.encodeToString(it) }
    }

    @TypeConverter
    fun toActorList(actorString: String?): List<Actor> {
        return actorString?.let { jsonParser.decodeFromString<List<Actor>>(it) } ?: emptyList()
    }
}
