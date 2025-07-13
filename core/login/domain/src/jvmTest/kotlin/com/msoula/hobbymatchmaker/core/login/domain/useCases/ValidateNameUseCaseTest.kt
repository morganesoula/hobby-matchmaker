package com.msoula.hobbymatchmaker.core.login.domain.useCases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidateNameUseCaseTest: FunSpec({
    val useCase = ValidateNameUseCase()

    context("ValidateNameUseCase should validate name properly") {
        listOf(
            "Alice" to true,
            "Jean-Michel" to true,
            "O'Connor" to true,
            "Élodie" to true,
            "D’Amato" to true,
            "María-José" to true,
            "Çınar" to true,
            "李" to true,
            "Алексей" to true,
            "محمد" to true,
            "Jean--Michel" to false,
            "-Jean" to false,
            "Jean-" to false,
            "'Jean" to false,
            "Jean'" to false,
            "Jean_" to false,
            "123Jean" to false,
            "Jean!" to false,
            "" to false
        ).forEach { (name, expected) ->
            test("should return $expected for name '$name'") {
                useCase(name).successful shouldBe expected
            }
        }
    }
})
