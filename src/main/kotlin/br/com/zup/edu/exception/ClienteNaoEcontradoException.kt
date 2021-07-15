package br.com.zup.edu.exception

import java.lang.RuntimeException

class ClienteNaoEcontradoException(override val message: String) : RuntimeException() {
}