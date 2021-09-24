package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.EmailVerificationTokenDTO
import java.util.*

interface NotificationService {
    fun createEmailVerificationToken(user: User, expiryDate: Date, token: String): EmailVerificationTokenDTO
    fun removeEmailVerificationToken(token: String): Unit
    fun removeExpiredTokens(): Unit
}