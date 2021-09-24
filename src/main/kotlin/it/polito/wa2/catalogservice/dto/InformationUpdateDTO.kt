package it.polito.wa2.catalogservice.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.*

data class InformationUpdateDTO (
    @field:Email(message = "Email should be valid")
    var email: String? = null,
    @field:Length(min = 1, message = "Name can't be less than one character")
    var name: String? = null,
    @field:Length(min =1, message = "Surname can't be less than one character")
    var surname: String? = null,
    @field:Length(min = 1, message = "Delivery address can't be less than one character")
    var deliveryAddress: String? = null
)