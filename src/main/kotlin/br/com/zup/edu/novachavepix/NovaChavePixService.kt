package br.com.zup.edu.novachavepix

import br.com.zup.edu.exception.ChavePixExistenteException
import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.httpclient.erpitau.ErpItauClientExterno
import br.com.zup.edu.novachavepix.dto.NovaChavePixDto
import br.com.zup.edu.novachavepix.model.ChavePix
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import br.com.zup.edu.httpclient.bcb.dto.create.CreatePixKeyRequest
import io.micronaut.http.HttpStatus
import java.lang.IllegalStateException

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val erpItauClientExterno: ErpItauClientExterno,
    @Inject val bcbClientExterno: BcbClientExterno,
) {


    @Transactional
    fun registraChavePix(@Valid novaChavePixDto: NovaChavePixDto): ChavePix {
        if (chavePixRepository.existsByValor(novaChavePixDto.valor!!)) {
            throw ChavePixExistenteException("chave pix já existe")
        }

        val possivelClienteItau =
            erpItauClientExterno.buscaContaPorTipo(novaChavePixDto.clienteId!!, novaChavePixDto.tipoConta!!.name)
        val conta =
            possivelClienteItau.body()?.toModel() ?: throw RecursoNaoEcontradoException("Cliente não encontrado")

        val chave = novaChavePixDto.converterParaEntidade(conta)
        chavePixRepository.save(chave)

        val bcbRequest = CreatePixKeyRequest.toDto(chave)
        val bcbResponse = bcbClientExterno.cadastrarChavePix(bcbRequest)

        if (bcbResponse.status != HttpStatus.CREATED) {
            throw IllegalStateException("Erro ao registrar pix no BCB")
        }
        println(bcbResponse.body())
        chave.atualizaChaveAleatoria(bcbResponse.body()!!.key)

        return chave
    }


}

