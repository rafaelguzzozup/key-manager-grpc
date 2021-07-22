package br.com.zup.edu.httpclient.bcb.dto.delete

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: String,
) {

}
