package br.com.zup.edu.httpclient.erpitau.dto

data class ClienteItauResponse(
    val id: String,
    val nome: String,
    val cpf: String,
    val instituicao: Instituicao
) {}

data class Instituicao(
    val nome: String,
    val ispb: String
) {}
