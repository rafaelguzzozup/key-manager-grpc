package br.com.zup.edu.exception

import java.lang.RuntimeException

class RecursoNaoPermitidoException(override val message: String) : RuntimeException() {
}