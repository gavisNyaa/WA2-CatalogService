package it.polito.wa2.catalogservice.dto

import org.hibernate.validator.constraints.Length

data class PasswordUpdateDTO (
    var oldPassword: String,
    @field:Length(min = 3, message = "Password can't be less than three character")
    var newPassword: String,
    var confirmPassword: String,
)