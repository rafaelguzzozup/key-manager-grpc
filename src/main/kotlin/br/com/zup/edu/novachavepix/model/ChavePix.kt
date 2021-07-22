package br.com.zup.edu.novachavepix.model

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class ChavePix(
    @field:NotBlank val clienteId: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave?,
    @field:NotBlank @field:Size(max = 77) @field:Column(unique = true) var valor: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:Valid @Embedded val conta: ContaAssociada,
) {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: String? = null


    fun isAleatoria(): Boolean {
        return tipoChave == TipoChave.ALEATORIO
    }

    fun atualizaChaveAleatoria(chave: String): Boolean {
        if (isAleatoria()) {
            this.valor = chave
            return true
        }
        return false
    }

}