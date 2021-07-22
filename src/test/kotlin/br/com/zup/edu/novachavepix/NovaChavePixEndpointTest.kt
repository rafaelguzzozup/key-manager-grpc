package br.com.zup.edu.novachavepix

import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.TipoChavePix
import br.com.zup.edu.TipoConta
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.httpclient.bcb.dto.AccountType
import br.com.zup.edu.httpclient.bcb.dto.BankAccount
import br.com.zup.edu.httpclient.bcb.dto.Owner
import br.com.zup.edu.httpclient.bcb.dto.PixKeyType
import br.com.zup.edu.httpclient.bcb.dto.create.CreatePixKeyRequest
import br.com.zup.edu.httpclient.bcb.dto.create.CreatePixKeyResponse
import br.com.zup.edu.httpclient.erpitau.ErpItauClientExterno
import br.com.zup.edu.httpclient.erpitau.dto.ClienteItauResponse
import br.com.zup.edu.httpclient.erpitau.dto.DadosDaContaResponse
import br.com.zup.edu.httpclient.erpitau.dto.Instituicao
import br.com.zup.edu.httpclient.erpitau.dto.Titular
import br.com.zup.edu.novachavepix.model.ChavePix
import br.com.zup.edu.novachavepix.model.ContaAssociada
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

    @Inject
    lateinit var bcbClient: BcbClientExterno

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
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.cadastrarChavePix(createPixKeyRequest())).thenReturn(HttpResponse.created(createPixKeyResponse()))

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
            conta = ContaAssociada(
                agencia = "0001",
                numero = "291900",
                instituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                cpf = "02467781054",
                nomeTitular = "Rafael M C Ponte"
            )
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
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE")).thenReturn(
            HttpResponse.notFound())

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

    @Test
    fun `nao deve cadastrar uma chave pix quando nao for possivel registrar no bcb`() {

        //cenario
        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.cadastrarChavePix(createPixKeyRequest())).thenReturn(HttpResponse.badRequest())

        //acao
        val exception = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrarChavePix(novaChavePixRequest())
        }

        with(exception) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao registrar pix no BCB", status.description)
        }

    }

    @MockBean(ErpItauClientExterno::class)
    fun itauClient(): ErpItauClientExterno? {
        return Mockito.mock(ErpItauClientExterno::class.java)
    }

    @MockBean(BcbClientExterno::class)
    fun bcbClient(): BcbClientExterno? {
        return Mockito.mock(BcbClientExterno::class.java)
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

    private fun createPixKeyRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = PixKeyType.converter(TipoChave.valueOf(novaChavePixRequest()!!.tipoChave.name)),
            key = novaChavePixRequest()!!.valor,
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "291900",
                accountType = AccountType.converter(CONTA_CORRENTE)
            ),
            owner = Owner(
                type = "NATURAL_PERSON",
                name = "Rafael",
                taxIdNumber = "02467781054"
            )
        )
    }

    private fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = PixKeyType.converter(TipoChave.valueOf(novaChavePixRequest()!!.tipoChave.name)),
            key = novaChavePixRequest()!!.valor,
            bankAccount = BankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "291900",
                accountType = AccountType.converter(CONTA_CORRENTE)
            ),
            owner = Owner(
                type = "NATURAL_PERSON",
                name = "Rafael",
                taxIdNumber = "02467781054"
            ),
            createdAt = ""
        )
    }

    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = Instituicao(nome = "UNIBANCO ITAU SA", ispb = "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = Titular(id = "c56dfef4-7901-44fb-84e2-a2cefb157890", nome = "Rafael", cpf = "02467781054")
        )
    }
}