package br.com.zup.edu.buscadadoschave.entidades

import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.novachavepix.ChavePixRepository
import javax.validation.constraints.NotBlank

class BuscaPorPix(
    @field:NotBlank val pixId: String,
    @field:NotBlank val clienteId: String,
) : OpcaoFiltroBuscaPix {

    override fun buscaDados(repository: ChavePixRepository, bcbClient: BcbClientExterno): DetalheChavePix {
        return repository.findByIdAndClienteId(pixId, clienteId)
            .map(DetalheChavePix::of)
            .orElseThrow { RecursoNaoEcontradoException("Chave pix nao encontrada ou nao pertence ao cliente informado") }
    }
}


