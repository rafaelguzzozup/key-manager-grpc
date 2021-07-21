package br.com.zup.edu.deletachavepix

import br.com.zup.edu.*
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
@Singleton
class DeletaChavePixEndpoint(val service: DeletaChavePixService) :
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {


    override fun deletarChavePix(
        request: DeletaChavePixRequest,
        responseObserver: StreamObserver<DeletaChavePixResponse>,
    ) {

        service.deletar(request.pixId, request.idCliente)

        responseObserver.onNext(DeletaChavePixResponse.newBuilder().build())
        responseObserver.onCompleted()
    }


}