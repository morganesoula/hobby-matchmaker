package com.msoula.hobbymatchmaker.core.login.domain.useCases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidateEmailUseCaseTest : FunSpec({
    val useCase = ValidateEmailUseCase()

    context("Valid emails") {
        listOf(
            "test@example.com" to true,
            "john.doe@sub.domain.com" to true,
            "user123@mail.org" to true,
            "invalidemail.com" to false,
            "user@" to false,
            "" to false,
            "1user@mail.com" to true,
            "user@mailcom" to false,
            "te st@example.com" to false,
            " test@example.com" to false,
            "test@ex ample.com" to false,
            "test@ example.com" to false,
            "test@example. com" to false
        ).forEach { (email, expected) ->
            test("should return $expected for $email") {
                useCase(email).successful shouldBe expected
            }
        }
    }
})
