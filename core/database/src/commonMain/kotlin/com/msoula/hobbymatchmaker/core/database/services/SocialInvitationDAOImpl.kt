package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.Social_invitation
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SocialInvitationDAOImpl(
    private val database: HMMDatabase,
    private val dispatcherProvider: DispatcherProvider
) : SocialInvitationDAO {
    override fun observeIncomingInvites(toPseudo: String): Flow<List<Social_invitation>> =
        database.hmm_databaseQueries.observeIncomingInvitations(toPseudo).asFlow()
            .mapToList(dispatcherProvider.io)

    override fun observeSentInvites(fromUid: String): Flow<List<Social_invitation>> =
        database.hmm_databaseQueries.observeSentInvitations(fromUid).asFlow()
            .mapToList(dispatcherProvider.io)

    @OptIn(ExperimentalTime::class)
    override suspend fun upsertInvites(invites: List<Social_invitation>): AppResult<Unit, AppError> {
        return try {
            database.transaction {
                invites.forEach { invite ->
                    val existingInvite =
                        database.hmm_databaseQueries.getInviteById(invite.id).executeAsOneOrNull()

                    if (existingInvite == null) {
                        database.hmm_databaseQueries.insertSocialInvitation(
                            id = invite.id,
                            from_uid = invite.from_uid,
                            from_pseudo = invite.from_pseudo,
                            to_pseudo = invite.to_pseudo,
                            name = invite.name,
                            status = invite.status,
                            created_at = invite.created_at,
                            updated_at = null
                        )
                    } else {
                        database.hmm_databaseQueries.updateSocialInvitation(
                            status = invite.status,
                            from_uid = invite.from_uid,
                            from_pseudo = invite.from_pseudo,
                            to_pseudo = invite.to_pseudo,
                            name = invite.name,
                            created_at = invite.created_at,
                            updated_at = Clock.System.now().toEpochMilliseconds(),
                            id = invite.id
                        )
                    }
                }
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            Logger.e("SocialInvitationDAOImpl - Database transaction failed: $e")
            AppResult.Failure(AppError.Storage.WriteFailed)
        }
    }
}
