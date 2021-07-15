package br.com.zup.edu.novachavepix

import br.com.zup.edu.novachavepix.model.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByValor(chave: String): Boolean
}