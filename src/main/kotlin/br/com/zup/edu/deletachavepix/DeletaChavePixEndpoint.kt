package br.com.zup.edu.deletachavepix

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.DeletaChavePixResponse
import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.NovaChavePixResponse
import br.com.zup.edu.compartilhado.handlers.ErrorAroundHandler
import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.exception.RecursoNaoPermitidoException
import br.com.zup.edu.novachavepix.ChavePixRepository
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@ErrorAroundHandler
@Validated
@Singleton
class DeletaChavePixEndpoint : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    @Inject
    lateinit var repository: ChavePixRepository

    override fun deletarChavePix(
        request: DeletaChavePixRequest?,
        responseObserver: StreamObserver<DeletaChavePixResponse>?,
    ) {
        val deletaChavePixDto = request!!.toModel()
        deletar(deletaChavePixDto)

        responseObserver!!.onNext(DeletaChavePixResponse.newBuilder().build())
        responseObserver!!.onCompleted()
    }

    fun DeletaChavePixRequest.toModel(): DeletaChavePixDto {
        return DeletaChavePixDto(
            clienteId = idCliente,
            pixId = pixId
        )
    }

    @Transactional
    fun deletar(@Valid deletaChavePixDto: DeletaChavePixDto) {
        val possivelChavePix = repository.findById(deletaChavePixDto.pixId)

        if (possivelChavePix.isEmpty) {
            throw RecursoNaoEcontradoException("Chave não encontrada com o Pix Id informado")
        }

        val chavePix = possivelChavePix.get()

        if (!chavePix.pertenceAoCliente(deletaChavePixDto.clienteId)) {
            throw RecursoNaoPermitidoException("A chave não pode ser deletada, pois não pertence ao cliente informado")
        }

        repository.delete(chavePix)
    }
}