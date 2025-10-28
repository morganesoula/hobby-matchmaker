package com.msoula.hobbymatchmaker.features.profile.domain.models

sealed interface ProfileState {
    data class Incomplete(val uid: String) : ProfileState
    data class Present(val profile: UserProfileDomainModel) : ProfileState
}
