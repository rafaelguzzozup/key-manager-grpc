package br.com.zup.edu.httpclient.erpitau

import br.com.zup.edu.httpclient.erpitau.dto.ClienteItauResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client("\${itauerp.client.url}")
interface ErpItauClientExterno {

    @Get("/clientes/{clienteId}")
    fun consultaCliente(clienteId : String) : HttpResponse<ClienteItauResponse>
}