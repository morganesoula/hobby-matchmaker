package com.msoula.hobbymatchmaker.core.di.domain.useCases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidateNameUseCaseTest : StringSpec({

    val useCase = ValidateNameUseCase()

    "should validate a simple alphabetic name" {
        useCase("John").successful shouldBe true
    }

    "should invalidate a name with a space" {
        useCase("John Doe").successful shouldBe false
    }

    "should invalidate a name with numbers" {
        useCase("John123").successful shouldBe false
    }

    "should invalidate a name with special characters" {
        useCase("J@hn").successful shouldBe false
    }

    "should invalidate an empty string" {
        useCase("").successful shouldBe false
    }

    "should invalidate a name with accented letters" {
        useCase("Émilie").successful shouldBe false
    }

    "should validate a name with only uppercase letters" {
        useCase("ALICE").successful shouldBe true
    }

    "should validate a name with only lowercase letters" {
        useCase("alice").successful shouldBe true
    }
})
