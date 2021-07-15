package br.com.zup.edu.novachavepix.validador

import br.com.zup.edu.novachavepix.dto.NovaChavePixDto
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePixValida(
    val message: String = "Chave Pix invalida (\${validatedValue.tipoChave})",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Singleton
class ChavePixValidator : ConstraintValidator<ChavePixValida, NovaChavePixDto> {
    override fun isValid(
        value: NovaChavePixDto?,
        annotationMetadata: AnnotationValue<ChavePixValida>,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.valor)
    }

}
