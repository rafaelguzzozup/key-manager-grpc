package br.com.zup.edu.novachavepix

import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.TipoChavePix.TIPO_CHAVE_DESCONHECIDO
import br.com.zup.edu.novachavepix.dto.NovaChavePixDto
import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.TipoConta.TIPO_CONTA_DESCONHECIDO
import br.com.zup.edu.novachavepix.model.TipoConta

fun NovaChavePixRequest.converteParaDtoValido(): NovaChavePixDto {

    return NovaChavePixDto(
        clienteId = idCliente,
        valor = valor,
        tipoConta = when (tipoConta) {
            TIPO_CONTA_DESCONHECIDO -> null
            else -> TipoConta.valueOf(tipoConta.name)
        },
        tipoChave = when (tipoChave) {
            TIPO_CHAVE_DESCONHECIDO -> null
            else -> TipoChave.valueOf(tipoChave.name)
        }
    )
}