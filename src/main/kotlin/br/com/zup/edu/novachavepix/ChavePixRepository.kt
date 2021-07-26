package br.com.zup.edu.novachavepix

import br.com.zup.edu.novachavepix.model.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, String> {

    fun existsByValor(chave: String): Boolean
    fun findByIdAndClienteId(id: String, clienteId: String): Optional<ChavePix>
    fun findByValor(chave: String): Optional<ChavePix>
    fun findAllByClienteId(clienteId: String): List<ChavePix>
}