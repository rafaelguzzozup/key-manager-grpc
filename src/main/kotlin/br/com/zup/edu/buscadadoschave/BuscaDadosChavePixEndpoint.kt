package br.com.zup.edu.buscadadoschave

import br.com.zup.edu.DadosChavePixRequest
import br.com.zup.edu.DadosChavePixResponse
import br.com.zup.edu.KeyManagerDadosGrpcServiceGrpc
import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.buscadadoschave.entidades.BuscaPorChave
import br.com.zup.edu.buscadadoschave.entidades.BuscaPorPix
import br.com.zup.edu.buscadadoschave.entidades.OpcaoFiltroBuscaPix
import br.com.zup.edu.compartilhado.handlers.ErrorAroundHandler
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.novachavepix.ChavePixRepository
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorAroundHandler
@Singleton
class BuscaDadosChavePixEndpoint(
    val service: BuscaDadosChavePixService,
) : KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceImplBase() {

    override fun dadosChavePix(
        request: DadosChavePixRequest,
        responseObserver: StreamObserver<DadosChavePixResponse>?,
    ) {

        val opcao = request.converteParaDtoOpcaoFiltro()
        responseObserver!!.onNext(service.buscadados(opcao))
        responseObserver.onCompleted()

    }


    fun DadosChavePixRequest.converteParaDtoOpcaoFiltro(): OpcaoFiltroBuscaPix {
        val opcao = when (buscaCase) {
            DadosChavePixRequest.BuscaCase.CHAVE -> BuscaPorChave(chave)
            DadosChavePixRequest.BuscaCase.PIXID -> BuscaPorPix(pixId = pixId.pixId, clienteId = pixId.idCliente)
            DadosChavePixRequest.BuscaCase.BUSCA_NOT_SET -> object : OpcaoFiltroBuscaPix {
                override fun buscaDados(repository: ChavePixRepository, bcbClient: BcbClientExterno): DetalheChavePix {
                    throw IllegalArgumentException("Chave Pix invalida ou não informada")
                }
            }
        }
        println(buscaCase.name)
        return opcao
    }

}

