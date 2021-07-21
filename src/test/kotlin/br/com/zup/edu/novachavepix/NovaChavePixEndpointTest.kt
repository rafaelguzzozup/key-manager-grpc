package br.com.zup.edu.novachavepix

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.TipoChavePix
import br.com.zup.edu.TipoConta
import br.com.zup.edu.httpclient.erpitau.ErpItauClientExterno
import br.com.zup.edu.httpclient.erpitau.dto.ClienteItauResponse
import br.com.zup.edu.httpclient.erpitau.dto.Instituicao
import br.com.zup.edu.novachavepix.model.ChavePix
import br.com.zup.edu.novachavepix.model.TipoChave
import br.com.zup.edu.novachavepix.model.TipoConta.*
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class NovaChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
) {
    @Inject
    lateinit var itauClient: ErpItauClientExterno

    companion object {
        val CLIENT_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma chave pix`() {

        //cenario
        `when`(itauClient.consultaCliente(clienteId = CLIENT_ID.toString())).thenReturn(HttpResponse.ok(
            ClienteItauResponse(
                id = CLIENT_ID.toString(),
                nome = "Rafal Guzzo",
                cpf = "07344506050",
                instituicao = Instituicao(nome = "UNIBANCO ITAU SA", ispb = "60701190")
            )))

        //acao
        val response = grpcClient.cadastrarChavePix(novaChavePixRequest())

        with(response) {
            assertNotNull(pixId)
        }

    }

    @Test
    fun `nao deve cadastrar uma chave pix quando chave existente`() {
        //cenario
        repository.save(ChavePix(
            clienteId = CLIENT_ID.toString(),
            tipoChave = TipoChave.EMAIL,
            valor = novaChavePixRequest()!!.valor,
            tipoConta = CONTA_CORRENTE,
        ))

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(novaChavePixRequest())
        }

        with(exception) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("chave pix já existe", status.description)
        }

    }

    @Test
    fun `nao deve cadastrar uma chave pix quando cliente inexistente`() {
        `when`(itauClient.consultaCliente(clienteId = CLIENT_ID.toString())).thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(novaChavePixRequest())
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar uma chave pix quando parametros forem invalidos`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(NovaChavePixRequest.newBuilder().build())
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @MockBean(ErpItauClientExterno::class)
    fun itauClient(): ErpItauClientExterno? {
        return Mockito.mock(ErpItauClientExterno::class.java);
    }

    @Factory
    class Clientes {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub? {
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel);
        }
    }

    private fun novaChavePixRequest(): NovaChavePixRequest? {
        return NovaChavePixRequest.newBuilder()
            .setIdCliente(CLIENT_ID.toString())
            .setTipoChave(TipoChavePix.EMAIL)
            .setValor("teste@teste")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
    }


}