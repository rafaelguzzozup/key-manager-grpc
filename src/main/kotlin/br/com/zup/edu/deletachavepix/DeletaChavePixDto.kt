package br.com.zup.edu.deletachavepix

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class DeletaChavePixDto(
    @field:NotBlank val pixId : String,
    @field:NotBlank val clienteId : String
) {
}