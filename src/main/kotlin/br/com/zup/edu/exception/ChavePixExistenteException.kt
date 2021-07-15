package br.com.zup.edu.exception

import java.lang.RuntimeException

class ChavePixExistenteException(override val message: String) : RuntimeException() {
}