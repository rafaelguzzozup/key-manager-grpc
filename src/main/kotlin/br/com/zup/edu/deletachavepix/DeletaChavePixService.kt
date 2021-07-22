package br.com.zup.edu.deletachavepix

import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.httpclient.bcb.dto.delete.DeletePixKeyRequest
import br.com.zup.edu.novachavepix.ChavePixRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeletaChavePixService(
    val repository: ChavePixRepository,
    val bcbClient: BcbClientExterno,
) {
    @Transactional
    fun deletar(
        @NotBlank pixId: String,
        @NotBlank clienteId: String,
    ) {
        val possivelChavePix = repository.findByIdAndClienteId(pixId, clienteId)
            .orElseThrow { RecursoNaoEcontradoException("Chave pix nao encontrada ou nao pertence ao cliente informado") }

        val bcbRequest = DeletePixKeyRequest(possivelChavePix.valor, possivelChavePix.conta.ispb)
        val bcbResponse = bcbClient.deletarChavePix(possivelChavePix.valor, bcbRequest)
        println(bcbResponse.body())
        if (bcbResponse.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao deletar pix no BCB")
        }
        repository.delete(possivelChavePix)
    }
}