package com.msoula.movies.domain.util

interface Mapper<in T, out R> {
    fun map(entity: T): R

    fun mapList(entity: List<T>): List<R> = entity.mapTo(mutableListOf(), ::map)
}
