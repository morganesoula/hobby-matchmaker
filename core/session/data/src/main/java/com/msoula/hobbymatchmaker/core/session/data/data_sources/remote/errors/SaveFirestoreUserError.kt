package com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class SaveFirestoreUserError(override val message: String): AppError
