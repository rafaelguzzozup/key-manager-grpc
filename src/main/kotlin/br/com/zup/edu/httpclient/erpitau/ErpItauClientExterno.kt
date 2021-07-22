package br.com.zup.edu.httpclient.erpitau

import br.com.zup.edu.httpclient.erpitau.dto.ClienteItauResponse
import br.com.zup.edu.httpclient.erpitau.dto.DadosDaContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itauerp.client.url}")
interface ErpItauClientExterno {

    @Get(value = "/clientes/{clienteId}")
    fun consultaCliente(clienteId: String): HttpResponse<ClienteItauResponse>

    @Get(value = "/clientes/{clienteId}/contas{?tipo}")
    fun buscaContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosDaContaResponse>
}