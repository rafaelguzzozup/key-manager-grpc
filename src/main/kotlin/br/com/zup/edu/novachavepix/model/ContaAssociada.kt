package br.com.zup.edu.novachavepix.model

import br.com.zup.edu.httpclient.erpitau.dto.Instituicao
import br.com.zup.edu.httpclient.erpitau.dto.Titular
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class ContaAssociada(
    @field:NotBlank val instituicao: String,
    @field:NotBlank val ispb: String,
    @field:NotBlank val agencia: String,
    @field:NotBlank val numero: String,
    @field:NotBlank val nomeTitular: String,
    @field:NotBlank val cpf: String,
) {
}