package it.polito.wa2.catalogservice

import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.services.UserDetailsService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:test.properties"])
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var userDetailsService: UserDetailsService
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder


    @Test
    fun registerUserValid() {
        val testData = object {
            val email = "test@email.it"
            val password = "pwd"
        }
        val user = "{\"name\": \"Bob\", \"deliveryAddress\": \"Via verdi 1\", \"surname\": \"Test\", \"password\": \"${testData.password}\", \"confirmPassword\": \"${testData.password}\", \"email\" : \"${testData.email}\"}"
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isCreated)

        val retrievedUser = userDetailsService.loadUserByUsername(testData.email) as UserDetailsDTO
        Assertions.assertNotNull(retrievedUser)
        Assertions.assertEquals(retrievedUser.email, testData.email)
        Assertions.assertTrue(passwordEncoder.matches(testData.password, retrievedUser.password))
        Assertions.assertEquals(retrievedUser.isEnabled, false)
        Assertions.assertEquals(retrievedUser.roles, User.RoleName.CUSTOMER.toString())
    }

    @Test
    fun registerUserInvalid() {
        // invalid email
        val user1 = "{\"name\": \"Bob\", \"deliveryAddress\": \"Via verdi 1\", \"surname\": \"Test\", \"password\": \"pwr\", \"confirmPassword\": \"pwr\", \"email\" : \"testmail\"}"
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user1)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().json("{\"email\": \"Email should be valid\"}"))

        // empty field
        val user2 = "{\"name\": \"\", \"deliveryAddress\": \"Via verdi 1\", \"surname\": \"Test\", \"password\": \"pwr\", \"confirmPassword\": \"pwr\", \"email\" : \"test@mail.com\"}"
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user2)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().json("{\"name\": \"Please provide a name\"}"))

        // password mismatch
        val user3 = "{\"name\": \"Bob\", \"deliveryAddress\": \"Via verdi 1\", \"surname\": \"Test\", \"password\": \"pwr\", \"confirmPassword\": \"pwr2\", \"email\" : \"test@mail.com\"}"
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user3)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"Password mismatch\"}"))
    }

    @Test
    fun registerUserEmailExisting() {
        val user = "{\"name\": \"Bob\", \"deliveryAddress\": \"Via verdi 1\", \"surname\": \"Test\", \"password\": \"pwr\", \"confirmPassword\": \"pwr\", \"email\" : \"test@email.com\"}"
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isCreated)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andExpect(MockMvcResultMatchers.content().json("{\"message\": \"There already is an user with this email: test@email.com\"}"))
    }
}