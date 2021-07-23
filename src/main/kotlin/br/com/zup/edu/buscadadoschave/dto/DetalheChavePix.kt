package br.com.zup.edu.buscadadoschave.dto

import br.com.zup.edu.novachavepix.model.ChavePix
import br.com.zup.edu.novachavepix.model.ContaAssociada
import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.novachavepix.model.TipoConta
import java.time.LocalDateTime
import kotlin.contracts.contract

class DetalheChavePix(
    val pixId: String? = null,
    val clienteId: String? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoDeConta: TipoConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now(),
) {

    companion object {
        fun of(chave: ChavePix): DetalheChavePix {
            return DetalheChavePix(
                pixId = chave.id,
                clienteId = chave.clienteId,
                tipo = chave.tipoChave,
                chave = chave.valor,
                tipoDeConta = chave.tipoConta,
                conta = chave.conta,
                registradaEm = chave.registradaEm
            )
        }
    }
}