package br.com.zup.edu.buscadadoschave.entidades

import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.novachavepix.ChavePixRepository
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
class BuscaPorChave(
    @field:NotBlank @field:Size(max = 77) val chave: String,
) : OpcaoFiltroBuscaPix {
    override fun buscaDados(repository: ChavePixRepository, bcbClient: BcbClientExterno): DetalheChavePix {
        return repository.findByValor(chave)
            .map(DetalheChavePix::of)
            .orElseGet {
                val response = bcbClient.detalhesChavePix(chave)
                when (response.status) {
                    HttpStatus.OK -> response.body()?.toModel()
                    else -> throw RecursoNaoEcontradoException("Chave Pix não encontrada")
                }
            }
    }
}