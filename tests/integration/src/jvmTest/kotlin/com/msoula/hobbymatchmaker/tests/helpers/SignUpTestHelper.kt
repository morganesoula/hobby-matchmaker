package com.msoula.hobbymatchmaker.tests.helpers

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.common.UIText.Plain

object PlainErrorMessageMapper : ErrorMessageMapper {
    override fun toUIText(error: AppError): UIText = Plain("mapped")
}
