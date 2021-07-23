package br.com.zup.edu.buscadadoschave

import br.com.zup.edu.DadosChavePixResponse
import br.com.zup.edu.TipoChavePix
import br.com.zup.edu.TipoConta
import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.buscadadoschave.entidades.OpcaoFiltroBuscaPix
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.novachavepix.ChavePixRepository
import com.google.protobuf.Timestamp
import io.micronaut.validation.Validated
import java.time.ZoneId
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class BuscaDadosChavePixService(
    val repository: ChavePixRepository,
    val bcbClientExterno: BcbClientExterno,
) {
    fun buscadados(@Valid opcao: OpcaoFiltroBuscaPix): DadosChavePixResponse? {

        val dadosChave = opcao.buscaDados(repository, bcbClientExterno)
        return converter(dadosChave)
    }

    fun converter(detalheChavePix: DetalheChavePix): DadosChavePixResponse? {

        val conta = DadosChavePixResponse.ChavePix.Conta.newBuilder()
            .setTipo(TipoConta.valueOf(detalheChavePix.tipoDeConta.name))
            .setInstituicao(detalheChavePix.conta.instituicao)
            .setNomeDoTitular(detalheChavePix.conta.nomeTitular)
            .setCpfDoTitular(detalheChavePix.conta.cpf)
            .setAgencia(detalheChavePix.conta.agencia)
            .setNumeroDaConta(detalheChavePix.conta.numero)
            .build()


        val chave = DadosChavePixResponse.ChavePix.newBuilder()
            .setTipo(TipoChavePix.valueOf(detalheChavePix.tipo.name))
            .setChave(detalheChavePix.chave)
            .setConta(conta)
            .setCriadaEm(detalheChavePix.registradaEm.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()


        return DadosChavePixResponse.newBuilder()
            .setClienteId(detalheChavePix.clienteId ?: "")
            .setPixId(detalheChavePix.pixId ?: "")
            .setChave(chave)
            .build()
    }
}
