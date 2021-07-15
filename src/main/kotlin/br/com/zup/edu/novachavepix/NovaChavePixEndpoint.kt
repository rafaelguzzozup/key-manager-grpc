package br.com.zup.edu.novachavepix

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.NovaChavePixResponse
import br.com.zup.edu.compartilhado.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
@ErrorAroundHandler
@Singleton
class NovaChavePixEndpoint(@Inject val novaChavePixService: NovaChavePixService) :
    KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastrarChavePix(
        request: NovaChavePixRequest?,
        responseObserver: StreamObserver<NovaChavePixResponse>?,
    ) {
        val novaChavePixDtoValida = request!!.converteParaDtoValido()
        val chavePix = novaChavePixService.registraChavePix(novaChavePixDtoValida)
        responseObserver!!.onNext(NovaChavePixResponse.newBuilder().setPixId(chavePix.id).build())
        responseObserver!!.onCompleted()
    }


}

