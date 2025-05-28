package com.msoula.hobbymatchmaker.core.di.domain.useCases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidatePasswordUseCaseTest : StringSpec({

    val useCase = ValidatePasswordUseCase()

    // Tests pour validatePassword
    "should validate a strong password" {
        useCase.validatePassword("Abcdef1!").successful shouldBe true
    }

    "should invalidate password with less than 8 characters" {
        useCase.validatePassword("A1!a").successful shouldBe false
    }

    "should invalidate password with no uppercase letter" {
        useCase.validatePassword("abcdef1!").successful shouldBe false
    }

    "should invalidate password with no lowercase letter" {
        useCase.validatePassword("ABCDEF1!").successful shouldBe false
    }

    "should invalidate password with no number" {
        useCase.validatePassword("Abcdefg!").successful shouldBe false
    }

    "should invalidate password with no special character" {
        useCase.validatePassword("Abcdefg1").successful shouldBe false
    }

    "should invalidate password with whitespace" {
        useCase.validatePassword("Abcd ef1!").successful shouldBe false
    }

    "should invalidate empty password" {
        useCase.validatePassword("").successful shouldBe false
    }

    // Tests pour validateLoginPassword
    "should validate non-empty password for login" {
        useCase.validateLoginPassword("anyPassword").successful shouldBe true
    }

    "should invalidate empty password for login" {
        useCase.validateLoginPassword("").successful shouldBe false
    }
})

