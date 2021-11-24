package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.InformationUpdateDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.transaction.Transactional
import kotlin.RuntimeException

@Service
@Transactional
class UserDetailsServiceImpl(
            val repository: UserRepository,
            val mailService: MailService,
            val notificationService: NotificationService,
            val emailVerificationTokenRepository: EmailVerificationTokenRepository
        ): UserDetailsService {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun loadUserByUsername(email: String): UserDetailsDTO? {
        val user = repository.findByEmail(email)
            ?: return null
        return user.toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun getAll(): List<InformationUpdateDTO> {
        return repository.findAll().map{user->InformationUpdateDTO(user.email, user.name, user.surname, user.deliveryAddress)}
    }

    override fun create(
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name: String,
        surname: String,
        deliveryAddress: String
    ): UserDetailsDTO {
        if (emailAlreadyExist(email)) {
            throw RuntimeException("There is already an user with this email: $email")
        }

        val encPassword = passwordEncoder.encode(password)
        var user = User(encPassword, email, isEnabled, roles, name, surname, deliveryAddress)
        user = repository.save(user)

        createTokenAndSendMailConfirmation(user.email)

        return user.toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun addRole(email: String, role: User.RoleName): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")
        user.addRole(role)

        return repository.save(user).toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun removeRole(email: String, role: User.RoleName): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")
        if (user.numberRoles() == 1) {
            throw RuntimeException("Role can't be remove, must remain at least one role.")
        }
        user.removeRole(role)

        return repository.save(user).toDTO()
    }

    @Secured("ROLE_ADMIN")
    override fun enableUser(email: String): UserDetailsDTO{
        return this.enable(email)
    }

    @Secured("ROLE_ADMIN")
    override fun disableUser(email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")
        user.isEnabled = false

        return repository.save(user).toDTO()
    }

    override fun validateVerificationToken(token: String): Boolean {
        val storedToken = emailVerificationTokenRepository.findByToken(token)
        if(storedToken == null || storedToken.expiryDate.before(Date())) return false

        enable(storedToken.user.email)
        emailVerificationTokenRepository.removeByToken(token)
        return true
    }

    override fun emailAlreadyExist(email: String): Boolean {
        return repository.findByEmail(email) != null
    }

    override fun createTokenAndSendMailConfirmation(email: String) {
        val expiry = LocalDateTime.now().plus(Duration.of(30, ChronoUnit.MINUTES))
        val expiryDate = Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant())
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")

        val confirmationToken = notificationService.createEmailVerificationToken(user, expiryDate, UUID.randomUUID().toString())

        mailService.sendMessage(
            user.email,
            "Please confirm your email address",
            "Click on the following link: http://localhost:8080/auth/registrationConfirm?token=${confirmationToken.token}"
        )
    }

    private fun enable(email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")

        user.isEnabled = true
        return repository.save(user).toDTO()
    }

    override fun updateInformation(
        email: String?,
        name: String?,
        surname: String?,
        deliveryAddress: String?,
        oldEmail: String
    ): UserDetailsDTO {
        val user = repository.findByEmail(oldEmail) ?: throw RuntimeException("User not found")

        if(name!=null)
            user.name = name
        if(surname!=null)
            user.surname = surname
        if(deliveryAddress!=null)
            user.deliveryAddress = deliveryAddress
        if(email!=null)
            user.email = email

        return repository.save(user).toDTO()
    }

    override fun updatePassword(oldPassword: String, newPassword: String, email: String): UserDetailsDTO {
        val user = repository.findByEmail(email) ?: throw RuntimeException("User not found")

        if (!passwordEncoder.matches(oldPassword, user.password))
            throw RuntimeException("The old password is wrong")

        val encPassword = passwordEncoder.encode(newPassword)
        user.password = encPassword

        return repository.save(user).toDTO()
    }
}