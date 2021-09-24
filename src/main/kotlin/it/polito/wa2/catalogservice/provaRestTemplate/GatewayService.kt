package it.polito.wa2.catalogservice.provaRestTemplate

import it.polito.wa2.catalogservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.transaction.Transactional

@Service
@Transactional
class GatewayService(
    var userRepository: UserRepository
) {
    @Autowired
    lateinit var restTemplate: RestTemplate

    fun retriveOrderInfo (email: String): Order? {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException("User don't found")

        val order: Order? = restTemplate.getForObject("http://localhost:8081/order/userID/" + user.getId(), Order::class.java)
        return order
    }
}