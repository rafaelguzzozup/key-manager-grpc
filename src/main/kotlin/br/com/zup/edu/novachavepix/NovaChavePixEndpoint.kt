package br.com.zup.edu.novachavepix

import br.com.zup.edu.*
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
        val novaChavePixDto = request!!.converteParaDto()
        val chavePix = novaChavePixService.registraChavePix(novaChavePixDto)
        responseObserver!!.onNext(NovaChavePixResponse.newBuilder().setPixId(chavePix.id).build())
        responseObserver!!.onCompleted()
    }




}

