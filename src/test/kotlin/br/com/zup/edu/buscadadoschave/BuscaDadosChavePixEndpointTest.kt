package br.com.zup.edu.buscadadoschave

import br.com.zup.edu.DadosChavePixRequest
import br.com.zup.edu.KeyManagerDadosGrpcServiceGrpc
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.httpclient.bcb.dto.*
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
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class BuscaDadosChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcCliente: KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceBlockingStub,
) {
    @Inject
    lateinit var bcbCliente: BcbClientExterno
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
    fun `deve carregar chave quando a opção for pixId e clienteId validos`() {
        val opcaoPixId =
            DadosChavePixRequest.PixId.newBuilder()
                .setPixId(CHAVE_EXISTENTE.id)
                .setIdCliente(CHAVE_EXISTENTE.clienteId)
                .build()

        val response = grpcCliente.dadosChavePix(
            DadosChavePixRequest.newBuilder()
                .setPixId(opcaoPixId)
                .build())

        with(response) {
            assertEquals(CHAVE_EXISTENTE.id, this.pixId)
            assertEquals(CHAVE_EXISTENTE.clienteId, this.clienteId)
            assertEquals(CHAVE_EXISTENTE.tipoChave.name, this.chave.tipo.name)
            assertEquals(CHAVE_EXISTENTE.valor, this.chave.chave)
        }
    }

    @Test
    fun `nao deve carregar chave quando a opção for pixId e clienteId forem invalidos`() {
        val opcaoPixId =
            DadosChavePixRequest.PixId.newBuilder()
                .setPixId("")
                .setIdCliente("")
                .build()

        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.dadosChavePix(
                DadosChavePixRequest.newBuilder()
                    .setPixId(opcaoPixId)
                    .build())
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve carregar chave quando a opção for pixId e clienteId nao existir`() {
        val opcaoPixId =
            DadosChavePixRequest.PixId.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setIdCliente(UUID.randomUUID().toString())
                .build()

        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.dadosChavePix(
                DadosChavePixRequest.newBuilder()
                    .setPixId(opcaoPixId)
                    .build())
        }

        with(exception) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix nao encontrada ou nao pertence ao cliente informado", status.description)
        }
    }

    @Test
    fun `deve carregar chave quando for a opção chave e o registro existir localmente`() {

        val response = grpcCliente.dadosChavePix(
            DadosChavePixRequest.newBuilder()
                .setChave(CHAVE_EXISTENTE.valor)
                .build())

        with(response) {
            assertEquals(CHAVE_EXISTENTE.id, this.pixId)
            assertEquals(CHAVE_EXISTENTE.clienteId, this.clienteId)
            assertEquals(CHAVE_EXISTENTE.tipoChave.name, this.chave.tipo.name)
            assertEquals(CHAVE_EXISTENTE.valor, this.chave.chave)
        }
    }

    @Test
    fun `deve carregar chave quando for a opção chave e o nao registro existir localmente mas existir no BCB`() {

        val pixKeyDetailsResponse = PixKeyDetailsResponse(
            keyType = PixKeyType.EMAIL,
            key = "umaemailqualquer@mail.com",
            bankAccount = BankAccount(
                participant = "90400888",
                branch = "9871",
                accountNumber = "987654",
                accountType = AccountType.SVGS
            ),
            owner = Owner(
                type = "NATURAL_PERSON",
                name = "Another User",
                taxIdNumber = "12345678901"
            ),
            createdAt = LocalDateTime.now()
        )

        `when`(bcbCliente.detalhesChavePix("umaemailqualquer@mail.com"))
            .thenReturn(HttpResponse.ok(pixKeyDetailsResponse))

        val response = grpcCliente.dadosChavePix(
            DadosChavePixRequest.newBuilder()
                .setChave("umaemailqualquer@mail.com")
                .build())

        with(response) {
            assertEquals("", this.pixId)
            assertEquals("", this.clienteId)
            assertEquals(pixKeyDetailsResponse.keyType.name, this.chave.tipo.name)
            assertEquals(pixKeyDetailsResponse.key, this.chave.chave)
        }
    }

    @Test
    fun `nao deve carregar chave quando for a opção chave e o nao registro existir localmente nem existir no BCB`() {

        `when`(bcbCliente.detalhesChavePix("umaemailqualquer@mail.com"))
            .thenReturn(HttpResponse.notFound())

        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.dadosChavePix(
                DadosChavePixRequest.newBuilder()
                    .setChave("umaemailqualquer@mail.com")
                    .build())
        }

        with(exception) {

            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
    }

    @Test
    fun `nao deve carregar chave quando for a opção chave e os dados do filtro invalidos`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.dadosChavePix(DadosChavePixRequest.newBuilder().setChave("").build())
        }

        with(exception) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve carregar chave quando filtro invalido`() {

        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.dadosChavePix(DadosChavePixRequest.newBuilder().build())
        }

        with(exception) {
            assertEquals(Status.UNKNOWN.code, status.code)
            assertEquals("Chave Pix invalida ou não informada", status.description)
        }
    }


    @MockBean(BcbClientExterno::class)
    fun bcbCliente(): BcbClientExterno? {
        return Mockito.mock(BcbClientExterno::class.java)
    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerDadosGrpcServiceGrpc.KeyManagerDadosGrpcServiceBlockingStub? {
            return KeyManagerDadosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}