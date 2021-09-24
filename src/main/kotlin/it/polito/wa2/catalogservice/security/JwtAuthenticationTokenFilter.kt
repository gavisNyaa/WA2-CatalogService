package it.polito.wa2.catalogservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Value("\${application.jwt.jwtHeader}")
    private lateinit var jwtHeader: String

    @Value("\${application.jwt.jwtHeaderStart}")
    private lateinit var jwtHeaderStart: String

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var jwtHeaderString = request.getHeader(jwtHeader)

        if(jwtHeaderString != null) {
            jwtHeaderString = jwtHeaderString.substringAfter("$jwtHeaderStart ")

            if(jwtUtils.validateJwtToken(jwtHeaderString)){
                val userDetails = jwtUtils.getDetailsFromJwtToken(jwtHeaderString)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}