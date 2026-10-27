package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.HMMDatabase

class UserDataDAOImpl(
    private val database: HMMDatabase
) : UserDataDAO {
    override suspend fun clearAllUserData() {
        database.hmm_databaseQueries.transaction {
            database.hmm_databaseQueries.deleteAllSocialMembers()
            database.hmm_databaseQueries.deleteAllInvitations()
            database.hmm_databaseQueries.deleteAllUsers()
            database.hmm_databaseQueries.deleteAllUserProfiles()
            database.hmm_databaseQueries.resetAllMovieUserData()
        }
    }
}
