package br.com.zup.edu.listachavespix

import br.com.zup.edu.*
import br.com.zup.edu.compartilhado.handlers.ErrorAroundHandler
import br.com.zup.edu.novachavepix.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import javax.inject.Singleton

@ErrorAroundHandler
@Singleton
class ListaChavesPixEndpoint(
    val repository: ChavePixRepository,
) : KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceImplBase() {

    override fun listaChavesPix(
        request: ListaChavesPixRequest?,
        responseObserver: StreamObserver<ListaChavesPixResponse>?,
    ) {
        if (request!!.clienteId.isNullOrBlank()) {
            throw IllegalArgumentException("Cliente ID não pode ser nulo ou vazio")
        }

        val chaves = repository.findAllByClienteId(request.clienteId).map {
            ListaChavesPixResponse.ChavePixResponse.newBuilder()
                .setPixId(it.id)
                .setTipoChave(TipoChavePix.valueOf(it.tipoChave.name))
                .setValor(it.valor)
                .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                .setCriadaEm(it.registradaEm.let {
                    val criadaEm = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(criadaEm.epochSecond)
                        .setNanos(criadaEm.nano)
                        .build()
                })
                .build()
        }

        responseObserver!!.onNext(ListaChavesPixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .addAllChavePix(chaves)
            .build())

        responseObserver.onCompleted()
    }
}