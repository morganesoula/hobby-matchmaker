package com.msoula.hobbymatchmaker.core.database.services

interface UserDataDAO {
    suspend fun clearAllUserData()
}
