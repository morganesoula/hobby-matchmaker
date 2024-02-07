package com.msoula.auth.data

import com.msoula.di.domain.StringResourcesProvider
import com.msoula.auth.R.string as StringRes

class FakeStringResourcesProvider : StringResourcesProvider {
    override fun getString(stringResId: Int): String {
        return when (stringResId) {
            StringRes.login_error -> "Identifiant ou mot de passe incorrect"
            else -> "Unknown id"
        }
    }
}
