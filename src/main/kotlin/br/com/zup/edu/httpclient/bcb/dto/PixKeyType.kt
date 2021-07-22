package br.com.zup.edu.httpclient.bcb.dto

import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.novachavepix.model.TipoConta

enum class PixKeyType {
    CPF, RANDOM, EMAIL, CNPJ, PHONE;

    companion object {
        fun converter(tipoChave: TipoChave): PixKeyType {
            return when (tipoChave) {
                TipoChave.CPF -> CPF
                TipoChave.EMAIL -> EMAIL
                TipoChave.CELULAR -> PHONE
                TipoChave.ALEATORIO -> RANDOM
            }
        }
    }
}
