package it.polito.wa2.catalogservice.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.*

data class RegisterDTO (
    @field:NotEmpty(message = "Please provide a password")
    @field:NotNull(message = "The password can't be null")
    @field:Length(min = 3, message = "Password can't be less than three character")
    var password: String,
    @field:NotEmpty(message = "Please provide the confirm password")
    @field:NotNull(message = "The password can't be null")
    var confirmPassword: String,
    @field:NotEmpty(message = "Please provide an email")
    @field:NotNull(message = "The email can't be null")
    @field:Email(message = "Email should be valid")
    var email: String,
    @field:NotEmpty(message = "Please provide a name")
    @field:NotNull(message = "The name can't be null")
    var name: String,
    @field:NotEmpty(message = "Please provide a surname")
    @field:NotNull(message = "The surname can't be null")
    var surname: String,
    @field:NotEmpty(message = "Please provide a delivery address")
    @field:NotNull(message = "The delivery address can't be null")
    var deliveryAddress: String
)