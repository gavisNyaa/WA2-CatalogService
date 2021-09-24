package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<User, Long> {
    fun findByEmail (email: String): User?
}