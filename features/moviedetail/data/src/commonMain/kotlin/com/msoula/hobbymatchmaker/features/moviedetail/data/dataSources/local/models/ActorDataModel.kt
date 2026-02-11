package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models

data class ActorDataModel(
    val id: Long = 0L,
    val name: String = "",
    val role: String = ""
) {
    companion object {
        val Initial = ActorDataModel()
    }
}
