package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.dto.EmailVerificationTokenDTO
import java.util.Date
import javax.persistence.*

@Entity
class EmailVerificationToken(
    @OneToOne
    @JoinColumn(name="user_email", referencedColumnName = "email", nullable = false)
    var user: User,
    var expiryDate: Date,
    @Column(unique = true, nullable = false)
    var token: String
): EntityBase<Long>(){

    fun toDTO(): EmailVerificationTokenDTO {
        return EmailVerificationTokenDTO(getId()!!, user.email, expiryDate, token)
    }
}