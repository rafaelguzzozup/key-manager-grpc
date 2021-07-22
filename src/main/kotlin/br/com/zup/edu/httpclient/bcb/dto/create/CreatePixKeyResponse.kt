package br.com.zup.edu.httpclient.bcb.dto.create

import br.com.zup.edu.httpclient.bcb.dto.BankAccount
import br.com.zup.edu.httpclient.bcb.dto.Owner
import br.com.zup.edu.httpclient.bcb.dto.PixKeyType

data class CreatePixKeyResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: String,
) {

}
