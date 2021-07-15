package br.com.zup.edu.novachavepix.model

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class ChavePix(
    @field:NotBlank val clienteId: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave?,
    @field:NotBlank @field:Size(max = 77) @field:Column(unique = true) val valor: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta?,
) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String? = null
}