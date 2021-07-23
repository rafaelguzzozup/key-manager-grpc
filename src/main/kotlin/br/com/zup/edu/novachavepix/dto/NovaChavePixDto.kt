package br.com.zup.edu.novachavepix.dto

import br.com.zup.edu.novachavepix.model.ChavePix
import br.com.zup.edu.novachavepix.model.ContaAssociada
import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.novachavepix.model.TipoConta
import br.com.zup.edu.novachavepix.validador.ChavePixValida
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ChavePixValida
@Introspected
data class NovaChavePixDto(
    @field:NotBlank val clienteId: String?,
    @field:NotNull val tipoChave: TipoChave?,
    @field:Size(max = 77) val valor: String?,
    @field:NotNull val tipoConta: TipoConta?,
) {
    override fun toString(): String {
        return "NovaChavePixDtoValida(clienteId='$clienteId', tipoChave=${tipoChave.toString()}, valor='$valor', tipoConta=${tipoConta.toString()})"
    }

    fun converterParaEntidade(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = clienteId!!,
            tipoChave = tipoChave!!,
            tipoConta = tipoConta!!,
            valor = if (this.tipoChave == TipoChave.ALEATORIO) UUID.randomUUID().toString() else this.valor!!,
            conta = conta
        )
    }
}