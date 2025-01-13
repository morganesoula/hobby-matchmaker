package com.msoula.hobbymatchmaker.core.database.dao.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.Genre

class Converters {

    //TODO See if Gson can be replaced by Serialization

    @TypeConverter
    fun fromGenreStringList(genres: List<String>?): String? {
        return genres?.joinToString { "," }
    }

    @TypeConverter
    fun toGenreStringList(data: String?): List<String>? {
        return data?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return Gson().toJson(genres)
    }

    @TypeConverter
    fun toGenreList(genreString: String?): List<Genre>? {
        if (genreString == null) return emptyList()
        val listType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(genreString, listType)
    }

    @TypeConverter
    fun fromActorList(actors: List<Actor>?): String? {
        return Gson().toJson(actors)
    }

    @TypeConverter
    fun toActorList(actorString: String?): List<Actor>? {
        if (actorString == null) return emptyList()
        val listType = object : TypeToken<List<Actor>>() {}.type
        return Gson().fromJson(actorString, listType)
    }
}
