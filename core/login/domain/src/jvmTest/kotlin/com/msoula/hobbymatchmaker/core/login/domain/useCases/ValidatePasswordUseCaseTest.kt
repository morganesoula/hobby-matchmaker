package com.msoula.hobbymatchmaker.core.login.domain.useCases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidatePasswordUseCaseTest: FunSpec({
    val useCase = ValidatePasswordUseCase()

    context("ValidatePasswordUseCase should validate password strength") {
        listOf(
            "Password123!" to true,
            "passWORD1@" to true,
            "P@ssw0rd" to true,
            "pass123!" to false,
            "PASS123!" to false,
            "Password!" to false,
            "Password123" to false,
            "Pwd1!" to false,
            "Password 123!" to false,
            "Pass123🙂" to false,
            "Aa1!aa1!" to true,
            "Pássword1!" to true,
            "Pass123~" to false,
            "P@ssw0rd\"" to true,
            "Pa ss123!" to false
        ).forEach { (password, expected) ->
            test("should return $expected for password '$password'") {
                useCase.validatePassword(password).successful shouldBe expected
            }
        }
    }
})
