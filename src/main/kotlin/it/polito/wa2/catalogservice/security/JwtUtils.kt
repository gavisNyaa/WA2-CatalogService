package it.polito.wa2.catalogservice.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {
    @Value("\${application.jwt.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${application.jwt.jwtExpirationMs}")
    private lateinit var jwtExpirationMs: String

    @Value("\${application.jwt.jwtHeader}")
    lateinit var jwtHeader: String

    @Value("\${application.jwt.jwtHeaderStart}")
    lateinit var jwtHeaderStart: String

    fun generateJwtToken(authentication: Authentication): String {
        val userDetails = (authentication.principal as UserDetailsDTO)

        return generateJwtToken(userDetails)
    }

    fun generateJwtToken(userDetails: UserDetailsDTO): String {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val dateMillis = Date().time + jwtExpirationMs.toLong()

        return Jwts.builder()
            .setExpiration(Date(dateMillis))
            .setClaims(mapOf("roles" to userDetails.roles))
            .setSubject(userDetails.email)
            .signWith(key)
            .compact()
    }

    fun validateJwtToken(authToken: String): Boolean {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)

            return true
        }
        catch (ex: ExpiredJwtException){
            print("jwt expired\n")
            return false
        }
        catch (ex: JwtException) {
            print("jwt exception: ${ex.toString()}\n")
            return false
        }
    }

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO {
        val jws: Jws<Claims>
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

        jws = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(authToken)

        val jwsBody = jws.body

        return UserDetailsDTO(email = jwsBody.subject, roles = jwsBody["roles"].toString())
    }
}

