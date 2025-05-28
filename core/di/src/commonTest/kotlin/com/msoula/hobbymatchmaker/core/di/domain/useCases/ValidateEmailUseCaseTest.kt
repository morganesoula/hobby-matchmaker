package com.msoula.hobbymatchmaker.core.di.domain.useCases

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidateEmailUseCaseTest: StringSpec({
    val useCase = ValidateEmailUseCase()

    "should validate a standard email" {
        useCase("john.doe@example.com").successful shouldBe true
    }

    "should invalidate email without @" {
        useCase("johndoe.example.com").successful shouldBe false
    }

    "should invalidate email without domain" {
        useCase("john.doe@").successful shouldBe false
    }

    "should invalidate email without username" {
        useCase("@example.com").successful shouldBe false
    }

    "should invalidate email with multiple @" {
        useCase("john@@example.com").successful shouldBe false
    }

    "should validate minimal valid email" {
        useCase("a@b.co").successful shouldBe true
    }

    "should invalidate empty string" {
        useCase("").successful shouldBe false
    }

    "should invalidate string with spaces" {
        useCase("john doe@example.com").successful shouldBe false
    }

    "should validate email with subdomain" {
        useCase("john.doe@mail.example.com").successful shouldBe true
    }

    "should invalidate email with special characters in domain" {
        useCase("john.doe@exa!mple.com").successful shouldBe false
    }
})
