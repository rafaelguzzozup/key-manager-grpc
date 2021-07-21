package br.com.zup.edu.novachavepix.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class ALEATORIA {

        @Test
        fun `deve ser valido quando chave aleatoria for nula ou vazia`() {
            with(TipoChave.ALEATORIO) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando chave aleatoria possuir valir`() {
            with(TipoChave.ALEATORIO) {
                assertFalse(valida("um valor"))
            }
        }

    }

    @Nested
    inner class CPF {

        @Test
        fun `deve ser valido quando chave cpf for um numero valido`() {
            with(TipoChave.CPF) {
                assertTrue(valida("98773185043"))
            }
        }

        @Test
        fun `nao deve ser valido quando chave cpf for um numero invalido`() {
            with(TipoChave.CPF) {
                assertFalse(valida("98773185049"))
                assertFalse(valida("123"))
            }
        }

        @Test
        fun `nao deve ser valido quando chave cpf for nula ou vazio`() {
            with(TipoChave.CPF) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando chave cpf possuir letras`() {
            with(TipoChave.CPF) {
                assertFalse(valida("estenao123"))
            }
        }


    }

    @Nested
    inner class CELULAR {

        @Test
        fun `deve ser valido quando chave celular for um numero valido`() {
            with(TipoChave.CELULAR) {
                assertTrue(valida("+5545998208166"))
            }
        }

        @Test
        fun `nao deve ser valido quando chave celular for um numero invalido`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("32231031"))
                assertFalse(valida("+55459982081062844")) // maior que 14 numeros
            }
        }

        @Test
        fun `nao deve ser valido quando chave celular for nulo ou vazio`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando chave celular possuir letras`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("+5545celular"))
            }
        }


    }

    @Nested
    inner class EMAIL {

        @Test
        fun `deve ser valido quando chave email for um email valido`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("teste@teste"))
            }
        }

        @Test
        fun `nao deve ser valido quando chave email for um email invalido`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("umvalorqualquer"))
                assertFalse(valida("teste.com"))
            }
        }

        @Test
        fun `nao deve ser valido quando chave email for nulo ou vazio`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando chave email possuir dois arobas`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("teste@teste@testado.com"))
            }
        }


    }
}