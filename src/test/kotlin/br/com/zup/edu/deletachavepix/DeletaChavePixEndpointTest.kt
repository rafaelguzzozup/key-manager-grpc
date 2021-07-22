package br.com.zup.edu.deletachavepix

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.httpclient.bcb.dto.delete.DeletePixKeyRequest
import br.com.zup.edu.httpclient.bcb.dto.delete.DeletePixKeyResponse
import br.com.zup.edu.novachavepix.ChavePixRepository
import br.com.zup.edu.novachavepix.model.ChavePix
import br.com.zup.edu.novachavepix.model.ContaAssociada
import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.novachavepix.model.TipoConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub,
) {

    @Inject
    lateinit var bcbClient: BcbClientExterno
    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(ChavePix(
            clienteId = UUID.randomUUID().toString(),
            tipoChave = TipoChave.EMAIL,
            valor = "teste@teste",
            tipoConta = TipoConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                agencia = "0001",
                numero = "291900",
                instituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                cpf = "02467781054",
                nomeTitular = "Rafael M C Ponte"
            )
        ))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }


    @Test
    fun `deve remover uma chave pix existente`() {
        `when`(bcbClient.deletarChavePix(
            CHAVE_EXISTENTE.valor,
            DeletePixKeyRequest(CHAVE_EXISTENTE.valor, CHAVE_EXISTENTE.conta.ispb)
        )).thenReturn(HttpResponse.ok(DeletePixKeyResponse(CHAVE_EXISTENTE.valor, CHAVE_EXISTENTE.conta.ispb, "")))

        val response = grpcClient.deletarChavePix(DeletaChavePixRequest.newBuilder()
            .setIdCliente(CHAVE_EXISTENTE.clienteId)
            .setPixId(CHAVE_EXISTENTE.id)
            .build())

        assertEquals(CHAVE_EXISTENTE.id, response.pixId)
        assertEquals(CHAVE_EXISTENTE.clienteId, response.idCliente)
    }

    @Test
    fun `nao deve remover chave quando pix id inexistente`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.deletarChavePix(DeletaChavePixRequest.newBuilder()
                .setIdCliente(CHAVE_EXISTENTE.clienteId)
                .setPixId(UUID.randomUUID().toString())
                .build())
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix nao encontrada ou nao pertence ao cliente informado", status.description)
        }

    }

    @Test
    fun `nao deve remover chave quando chave existente mas pertecente a outro cliente`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.deletarChavePix(DeletaChavePixRequest.newBuilder()
                .setIdCliente(UUID.randomUUID().toString())
                .setPixId(CHAVE_EXISTENTE.id)
                .build())
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix nao encontrada ou nao pertence ao cliente informado", status.description)
        }

    }

    @MockBean(BcbClientExterno::class)
    fun bcbClient(): BcbClientExterno? {
        return Mockito.mock(BcbClientExterno::class.java)
    }


    @Factory
    class CLients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}