package br.com.zup.edu.httpclient.bcb.dto.create

import br.com.zup.edu.httpclient.bcb.dto.AccountType
import br.com.zup.edu.httpclient.bcb.dto.BankAccount
import br.com.zup.edu.httpclient.bcb.dto.Owner
import br.com.zup.edu.httpclient.bcb.dto.PixKeyType
import br.com.zup.edu.novachavepix.model.ChavePix

data class CreatePixKeyRequest(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
) {
    companion object {
        fun toDto(chavePix: ChavePix): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = PixKeyType.converterParaPixKeyType(chavePix.tipoChave!!),
                key = chavePix.valor,
                bankAccount = BankAccount(
                    participant = chavePix.conta.ispb,
                    branch = chavePix.conta.agencia,
                    accountNumber = chavePix.conta.numero,
                    accountType = AccountType.converter(chavePix.tipoConta)
                ),
                owner = Owner(
                    type = "NATURAL_PERSON",
                    name = chavePix.conta.nomeTitular,
                    taxIdNumber = chavePix.conta.cpf
                )
            )
        }
    }


}
