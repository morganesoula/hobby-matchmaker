package com.msoula.hobbymatchmaker.features.social.domain.useCases

import com.msoula.hobbymatchmaker.features.social.domain.models.UserReportDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository

class ReportUserUseCase(
    private val socialRepository: SocialRepository
) {
    suspend operator fun invoke(report: UserReportDomainModel) =
        socialRepository.reportUser(report)
}
