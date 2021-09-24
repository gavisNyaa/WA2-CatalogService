package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.Email

@Entity
@Table(indexes = [Index(name = "email_index", columnList = "email", unique = true)])
class User (
    var password: String,
    @Column(unique = true, nullable = false)
    @Email(message = "*Please provide a valid Email")
    var email: String,
    var isEnabled: Boolean = false,
    var roles: String,
    var name: String,
    var surname: String,
    var deliveryAddress: String
    ): EntityBase<Long>(), Serializable {
    enum class RoleName {
        CUSTOMER, ADMIN
    }

    fun getRoleList(): List<RoleName> {

        return roles.split(",").map { print(it)
            RoleName.valueOf(it) }
    }

    fun addRole(role: RoleName) {
        val roleList = getRoleList()
        if (!roleList.contains(role)) {
            val stringRole = role.toString()
            if (roles.isEmpty()) {
                roles = stringRole
            } else {
                roles += ","+ stringRole
            }
        }
    }

    fun removeRole(role: RoleName) {
        roles = getRoleList().filterNot { it == role }.joinToString(",")
    }

    fun toDTO(): UserDetailsDTO {
        return UserDetailsDTO(getId()!!, password, email, isEnabled, roles, name, surname, deliveryAddress)
    }

    fun numberRoles(): Int {
        return roles.split(",").count()
    }
}