package br.com.zup.edu.deletachavepix

import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.novachavepix.ChavePixRepository
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeletaChavePixService(
    val repository: ChavePixRepository,
) {


    @Transactional
    fun deletar(
        @NotBlank pixId: String,
        @NotBlank clienteId: String,
    ) {
        val possivelChavePix = repository.findByIdAndClienteId(pixId, clienteId)
            .orElseThrow { RecursoNaoEcontradoException("Chave pix nao encontrada ou nao pertence ao cliente informado") }

        repository.delete(possivelChavePix)
    }
}