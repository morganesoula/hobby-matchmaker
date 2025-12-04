package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialUserSummaryDomainModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import com.msoula.hobbymatchmaker.core.common.Logger

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SocialRemoteDataSource {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        safeFirebaseCall {
            val searchTerm = pseudo.trim().lowercase()
            if (searchTerm.isEmpty()) return@safeFirebaseCall emptyList()

            val endTerm = searchTerm + '\uf8ff'

            val documents = try {
                firestore
                    .collection("users")
                    .orderBy("information.pseudo", Direction.ASCENDING)
                    .startAt(searchTerm)
                    .endAt(endTerm)
                    .limit(20)
                    .get()
                    .documents
            } catch (e: Exception) {
                Logger.d("SocialRemoteDataSource: Error during Firestore query: ${e.message}")
                throw e
            }

            val results = documents
                .mapNotNull { document ->
                    val uid = document.id
                    val userPseudo = document.get<String?>("information.pseudo")
                    val name = document.get<String?>("information.name")
                    val avatar = document.get<String?>("information.avatarUrl")

                    if (uid == ownerUid) return@mapNotNull null

                    if (userPseudo != null && userPseudo.lowercase().contains(searchTerm)) {
                        SocialUserSummaryDomainModel(
                            uid = uid,
                            name = name,
                            pseudo = userPseudo,
                            avatarUrl = avatar
                        )
                    } else null
                }
                .take(10)

            results.map { member ->
                SocialMemberDomainModel(
                    uid = member.uid,
                    name = member.name,
                    pseudo = member.pseudo,
                    avatarUrl = member.avatarUrl ?: DEFAULT_AVATAR_URL
                )
            }
        }

    override suspend fun sendInvite(fromUid: String, toUid: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override fun observeIncomingInvites(ownerUid: String): Flow<List<SocialInviteDomainModel>> {
        TODO("Not yet implemented")
    }

    override fun observeSentInvited(ownerUid: String): Flow<List<SocialInviteDomainModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun addToSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }
}
