package com.msoula.hobbymatchmaker.testUtils.core.login.useCases

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateNameUseCaseTest {

    @Test
    fun `when name is empty, return false`() {
        val validateNameUseCase = ValidateNameUseCase()
        val result = validateNameUseCase("")
        assertFalse(result.successful)
    }

    @Test
    fun `when name contains number, return false`() {
        val validateNameUseCase = ValidateNameUseCase()
        val result = validateNameUseCase("John123")
        assertFalse(result.successful)
    }

    @Test
    fun `when name contains special character, return false`() {
        val validateNameUseCase = ValidateNameUseCase()
        val result = validateNameUseCase("John!")
        assertFalse(result.successful)
    }

    @Test
    fun `when name is valid, return success`() {
        val validateNameUseCase = ValidateNameUseCase()
        val result = validateNameUseCase("John")
        assertTrue(result.successful)
    }
}
