package br.com.zup.edu.httpclient.bcb.dto

import br.com.zup.edu.novachavepix.model.TipoConta

enum class AccountType() {

    CACC,
    SVGS;

    companion object {
        fun converter(tipoConta: TipoConta): AccountType {
            return when (tipoConta) {
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
            }
        }
    }
}