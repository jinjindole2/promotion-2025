package com.jinjindole2.jinpro.common.validate

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidEnumValidator::class])
annotation class ValidEnum(
    val message: String = "Invalid value for enum",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidEnumValidator : ConstraintValidator<ValidEnum, Enum<*>> {
    override fun isValid(value: Enum<*>?, context: ConstraintValidatorContext): Boolean {
        return value != null //&& value.enumValues().contains(value)
    }
}