package br.com.zup.edu.buscadadoschave.entidades

import br.com.zup.edu.buscadadoschave.dto.DetalheChavePix
import br.com.zup.edu.httpclient.bcb.BcbClientExterno
import br.com.zup.edu.novachavepix.ChavePixRepository

interface OpcaoFiltroBuscaPix {

    fun buscaDados(repository: ChavePixRepository, bcbClient: BcbClientExterno) : DetalheChavePix
}