package com.jinjindole2.jinpro.common.validate.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DateRangeValidator::class])
annotation class ValidDateRange(
    val message: String = "완료일자는 시작일자 이후에만 가능합니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class DateRangeValidator : ConstraintValidator<ValidDateRange, Any> {
    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        // 리플렉션을 사용해 클래스의 필드 중 @DateField 어노테이션을 확인
        try {
            val clazz = value::class
            var startDate: LocalDate? = null
            var endDate: LocalDate? = null

            // 필드를 순회하며 @DateField 어노테이션이 있는지 확인
            clazz.members.filterIsInstance<KProperty1<Any, *>>().forEach { member ->
                val annotation = member.findAnnotation<DateRange>()
                if (annotation != null) {
                    val date = member.call(value) as? LocalDate
                    when (annotation.type) {
                        DateType.START -> startDate = date
                        DateType.END -> endDate = date
                    }
                }
            }

            // 시작일자와 완료일자를 비교
            if (startDate == null || endDate == null) return false
            return startDate!!.isBefore(endDate) || startDate!!.isEqual(endDate)

        } catch (e: Exception) {
            return false
        }
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class DateRange(val type: DateType)

enum class DateType {
    START, END
}