package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import org.springframework.security.core.userdetails.UserDetailsService

interface UserDetailsService: UserDetailsService {
    fun create(
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name : String,
        surname: String,
        deliveryAddress: String
    ): UserDetailsDTO

    fun addRole(email: String, role: User.RoleName): UserDetailsDTO

    fun removeRole(email: String, role: User.RoleName): UserDetailsDTO

    fun enableUser(email: String): UserDetailsDTO

    fun disableUser(email: String): UserDetailsDTO

    fun validateVerificationToken(token: String): Boolean

    fun emailAlreadyExist(email: String): Boolean

    fun createTokenAndSendMailConfirmation(email: String)

    fun updateInformation(
               email: String?,
               name : String?,
               surname: String?,
               deliveryAddress: String?,
               oldEmail: String
    ): UserDetailsDTO

    fun updatePassword(oldPassword: String, newPassword: String, email: String): UserDetailsDTO
}