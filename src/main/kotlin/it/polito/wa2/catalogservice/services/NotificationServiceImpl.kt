package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.EmailVerificationToken
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.EmailVerificationTokenDTO
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import org.springframework.stereotype.Service
import org.springframework.scheduling.annotation.Scheduled
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl (
        val emailVerificationTokenRepository: EmailVerificationTokenRepository
): NotificationService {

    override fun createEmailVerificationToken(user: User, expiryDate: Date, token: String): EmailVerificationTokenDTO {
        return emailVerificationTokenRepository.save(EmailVerificationToken(user, expiryDate, token)).toDTO()
    }

    override fun removeEmailVerificationToken(token: String) {
        return emailVerificationTokenRepository.removeByToken(token)
    }

    override fun removeExpiredTokens() {
        return emailVerificationTokenRepository.removeEmailVerificationTokensByExpiryDateBefore(Date())
    }

    @Scheduled(fixedRate = 60000)
    fun cleanTokenExpired() {
        removeExpiredTokens()
    }
}