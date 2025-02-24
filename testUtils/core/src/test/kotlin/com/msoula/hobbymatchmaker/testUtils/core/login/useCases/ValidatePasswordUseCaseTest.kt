package com.msoula.hobbymatchmaker.testUtils.core.login.useCases

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatePasswordUseCaseTest {

    @Test
    fun `when password is empty, return false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val result = validatePasswordUseCase.validatePassword("")
        assertFalse(result.successful)
    }

    @Test
    fun `when password is not strong enough, return false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val result = validatePasswordUseCase.validatePassword("password")
        assertFalse(result.successful)
    }

    @Test
    fun `when password contains no number, return false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val result = validatePasswordUseCase.validatePassword("Password!")
        assertFalse(result.successful)
    }

    @Test
    fun `when password contains no special character, return false`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val result = validatePasswordUseCase.validatePassword("Password123")
        assertFalse(result.successful)
    }

    @Test
    fun `when password is strong enough, return success`() {
        val validatePasswordUseCase = ValidatePasswordUseCase()
        val result = validatePasswordUseCase.validatePassword("Password123!")
        assertTrue(result.successful)
    }
}
