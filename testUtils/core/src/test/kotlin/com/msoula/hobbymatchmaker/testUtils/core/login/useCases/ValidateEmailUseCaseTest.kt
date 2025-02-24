package com.msoula.hobbymatchmaker.testUtils.core.login.useCases

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidateEmailUseCaseTest {

    @Test
    fun `when email is empty, return false`() {
        val validateEmailUseCase = ValidateEmailUseCase()
        val result = validateEmailUseCase("")
        assertFalse(result.successful)
    }

    @Test
    fun `when email is not email shaped, return false`() {
        val validateEmailUseCase = ValidateEmailUseCase()
        val result = validateEmailUseCase("testEmail")
        assertFalse(result.successful)
    }

    @Test
    fun `when email is email shaped, return success`() {
        val validateEmailUseCase = ValidateEmailUseCase()
        val result = validateEmailUseCase("testEmail@email.test")
        assertTrue(result.successful)
    }
}
