package br.com.zup.edu.exception

import java.lang.RuntimeException

class RecursoNaoEcontradoException(override val message: String) : RuntimeException() {
}