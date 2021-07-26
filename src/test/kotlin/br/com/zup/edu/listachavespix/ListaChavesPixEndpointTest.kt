package br.com.zup.edu.listachavespix

import br.com.zup.edu.KeyManagerListaGrpcServiceGrpc
import br.com.zup.edu.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.edu.ListaChavesPixRequest
import br.com.zup.edu.TipoChavePix
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class ListaChavesPixEndpointTest(
    val repository: ChavePixRepository,
    val grpcCliente: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub,
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setup() {
        repository.save(chave(tipo = TipoChave.EMAIL, valor = "teste@teste"))
        repository.save(chave(tipo = TipoChave.ALEATORIO, valor = "aleatorio1"))
        repository.save(chave(tipo = TipoChave.ALEATORIO, valor = "aleatorio2"))
        repository.save(chave(tipo = TipoChave.ALEATORIO, valor = "aleatorio3"))
        repository.save(chave(tipo = TipoChave.CELULAR, valor = "+5545998208181"))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar todas as chaves do cliente`() {
        val response = grpcCliente.listaChavesPix(ListaChavesPixRequest.newBuilder().setClienteId(CLIENTE_ID).build())

        with(response.chavePixList) {
            assertThat(this, hasSize(5))
            assertThat(
                this.map { Pair(it.tipoChave, it.valor) }.toList(),
                containsInAnyOrder(
                    Pair(TipoChavePix.EMAIL, "teste@teste"),
                    Pair(TipoChavePix.ALEATORIO, "aleatorio1"),
                    Pair(TipoChavePix.ALEATORIO, "aleatorio2"),
                    Pair(TipoChavePix.ALEATORIO, "aleatorio3"),
                    Pair(TipoChavePix.CELULAR, "+5545998208181")
                )
            )
        }
    }

    @Test
    fun `nao deve listar chaves do cliente quando cliente nao possuir chaves`() {
        val cliente = UUID.randomUUID().toString()
        val response = grpcCliente.listaChavesPix(ListaChavesPixRequest.newBuilder().setClienteId(cliente).build())

        assertEquals(0, response.chavePixCount)
    }

    @Test
    fun `nao deve listar chaves quando clienteid for invalido`() {
        val exception = assertThrows<StatusRuntimeException> {
            grpcCliente.listaChavesPix(ListaChavesPixRequest.newBuilder().setClienteId("").build())
        }

        with(exception) {
            assertEquals(Status.UNKNOWN.code, status.code)
            assertEquals("Cliente ID não pode ser nulo ou vazio", status.description)
        }


    }

    @Factory
    class CLients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub? {
            return KeyManagerListaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    fun chave(tipo: TipoChave, valor: String): ChavePix {
        return ChavePix(
            clienteId = CLIENTE_ID,
            tipoChave = tipo,
            valor = valor,
            tipoConta = TipoConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                agencia = "0001",
                numero = "291900",
                instituicao = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190",
                cpf = "02467781054",
                nomeTitular = "Rafael M C Ponte"
            )
        )
    }

}
