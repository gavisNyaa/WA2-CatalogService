package it.polito.wa2.catalogservice.provaRestTemplate

import it.polito.wa2.catalogservice.provaRestTemplate.GatewayService
import it.polito.wa2.catalogservice.provaRestTemplate.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GatewayController (
    var gatewayService: GatewayService
        ){
    @GetMapping("/orderInfo")
    fun retriveOrderInfo(): Order? {
        val principal = (SecurityContextHolder.getContext().authentication)
        return gatewayService.retriveOrderInfo(principal.name)
    }
}