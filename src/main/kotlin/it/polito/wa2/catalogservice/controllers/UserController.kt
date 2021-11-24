package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.InformationUpdateDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.dto.PasswordUpdateDTO
import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(
    val userDetailsService: UserDetailsService,
    ) {
    @PatchMapping("/updateInformation")
    fun updateInformation(@Valid @RequestBody body: InformationUpdateDTO, br: BindingResult): ResponseEntity<Any> {
        if (br.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message(br.allErrors[0].defaultMessage))
        }
        val principal = (SecurityContextHolder.getContext().authentication)
        userDetailsService.updateInformation(body.email, body.name, body.surname, body.deliveryAddress, principal.name)
        if (body.email!=null) {
            return ResponseEntity.status(HttpStatus.OK).body(Message("Information updated: email updated, please login again"))
        }
        return ResponseEntity.status(HttpStatus.OK).body(Message("Information updated"))
    }

    @PutMapping("/updatePassword")
    fun updatePassword(@Valid @RequestBody body: PasswordUpdateDTO, br:BindingResult): ResponseEntity<Any> {
        if (br.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message(br.allErrors[0].defaultMessage))
        }
        if (body.newPassword != body.confirmPassword)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Message("New password mismatch"))
        val principal = (SecurityContextHolder.getContext().authentication)

        userDetailsService.updatePassword(body.oldPassword, body.newPassword, principal.name)

        return ResponseEntity.status(HttpStatus.OK).body(Message("Password updated"))
    }

    @GetMapping("/retrieveInformation")
    fun retrieveInformation(): InformationUpdateDTO {
        val principal = (SecurityContextHolder.getContext().authentication)
        val user = userDetailsService.loadUserByUsername(principal.name) as UserDetailsDTO

        return InformationUpdateDTO(user.email, user.name, user.surname, user.deliveryAddress)
    }

    @PostMapping("{username}/enable")
    fun adminEnableUser(@PathVariable("username") username: String){
        userDetailsService.enableUser(username)
    }

    @PostMapping("{username}/disable")
    fun adminDisableUser(@PathVariable("username") username: String){
        userDetailsService.disableUser(username)
    }

    @PostMapping("{username}/addRole")
    fun adminAddRole(@PathVariable("username") username: String, @RequestParam("role") role: String) {
        userDetailsService.addRole(username, User.RoleName.valueOf(role))
    }

    @PostMapping("{username}/removeRole")
    fun adminRemoveRole(@PathVariable("username") username: String, @RequestParam("role") role: String) {
        userDetailsService.removeRole(username, User.RoleName.valueOf(role))
    }

    @GetMapping("/list")
    fun listUsers(): List<InformationUpdateDTO> {
        return userDetailsService.getAll()
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCustomException(ce: Exception): Message {
        return Message(ce.message.toString())
    }
}