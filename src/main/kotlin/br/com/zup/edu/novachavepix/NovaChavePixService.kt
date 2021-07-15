package br.com.zup.edu.novachavepix

import br.com.zup.edu.exception.ChavePixExistenteException
import br.com.zup.edu.exception.ClienteNaoEcontradoException
import br.com.zup.edu.httpclient.erpitau.ErpItauClientExterno
import br.com.zup.edu.novachavepix.dto.NovaChavePixDto
import br.com.zup.edu.novachavepix.model.ChavePix
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val erpItauClientExterno: ErpItauClientExterno,
) {


    @Transactional
    fun registraChavePix(@Valid novaChavePixDto: NovaChavePixDto): ChavePix {
        if (chavePixRepository.existsByValor(novaChavePixDto.valor)) {
            throw ChavePixExistenteException("chave pix já existe")
        }

        val possivelClienteItau = erpItauClientExterno.consultaCliente(novaChavePixDto.clienteId)
        val conta = possivelClienteItau.body() ?: throw ClienteNaoEcontradoException("Cliente não encontrado")

        val chave = novaChavePixDto.converterParaEntidade()

        chavePixRepository.save(chave)

        return chave
    }
}