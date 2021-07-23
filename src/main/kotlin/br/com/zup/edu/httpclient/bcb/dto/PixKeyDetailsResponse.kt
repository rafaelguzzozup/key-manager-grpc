package br.com.zup.edu.httpclient.bcb.dto

import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.httpclient.bcb.Instituicoes
import br.com.zup.edu.novachavepix.model.ContaAssociada
import br.com.zup.edu.novachavepix.model.TipoConta
import java.time.LocalDateTime

data class PixKeyDetailsResponse (
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    fun toModel(): DetalheChavePix {
        return DetalheChavePix(
            tipo = keyType.converterParaTipoChave()!!,
            chave = this.key,
            tipoDeConta = when (this.bankAccount.accountType) {
                AccountType.CACC -> TipoConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = Instituicoes.nome(bankAccount.participant),
                nomeTitular = owner.name,
                cpf = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                ispb = bankAccount.participant
            )
        )
    }
}