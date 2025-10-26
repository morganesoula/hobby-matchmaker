package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow

class SocialRepositoryImpl: SocialRepository {
    
    override fun observeSocialCircleCount(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun observeSocialCircle(): Flow<List<UserSummaryDomainModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCircle(memberUid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCircle(memberUid: String) {
        TODO("Not yet implemented")
    }
}
