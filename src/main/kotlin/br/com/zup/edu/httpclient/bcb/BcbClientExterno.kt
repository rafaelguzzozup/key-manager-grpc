package br.com.zup.edu.httpclient.bcb

import br.com.zup.edu.httpclient.bcb.dto.create.CreatePixKeyRequest
import br.com.zup.edu.httpclient.bcb.dto.create.CreatePixKeyResponse
import br.com.zup.edu.httpclient.bcb.dto.delete.DeletePixKeyRequest
import br.com.zup.edu.httpclient.bcb.dto.delete.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.client.url}")
interface BcbClientExterno {

    @Post(value = "/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML])
    fun cadastrarChavePix(@Body createPixKeyRequest: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(value = "/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML])
    fun deletarChavePix(@PathVariable key : String, @Body deletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>
}