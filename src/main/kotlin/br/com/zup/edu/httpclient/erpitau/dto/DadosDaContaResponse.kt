package br.com.zup.edu.httpclient.erpitau.dto

import br.com.zup.edu.novachavepix.model.ContaAssociada

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular,
) {

    fun toModel(): ContaAssociada {
        return ContaAssociada(
            agencia = agencia,
            numero = numero,
            instituicao = instituicao.nome,
            ispb = instituicao.ispb,
            cpf = titular.cpf,
            nomeTitular = titular.nome
        )
    }
}
