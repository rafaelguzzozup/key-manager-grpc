package br.com.zup.edu.httpclient.bcb.dto

import br.com.zup.edu.novachavepix.model.TipoChave


enum class PixKeyType {
    CPF {
        override fun converterParaTipoChave(): TipoChave? {
            return TipoChave.CPF
        }
    },
    RANDOM {
        override fun converterParaTipoChave(): TipoChave? {
            return TipoChave.ALEATORIO
        }
    },
    EMAIL {
        override fun converterParaTipoChave(): TipoChave? {
            return TipoChave.EMAIL
        }
    },
    CNPJ {
        override fun converterParaTipoChave(): TipoChave? {
            return null
        }
    },
    PHONE {
        override fun converterParaTipoChave(): TipoChave? {
            return TipoChave.CELULAR
        }
    };

    abstract fun converterParaTipoChave(): TipoChave?

    companion object {
        fun converterParaPixKeyType(tipoChave: TipoChave): PixKeyType {
            return when (tipoChave) {
                TipoChave.CPF -> CPF
                TipoChave.EMAIL -> EMAIL
                TipoChave.CELULAR -> PHONE
                TipoChave.ALEATORIO -> RANDOM
            }
        }
    }
}
