package it.polito.wa2.catalogservice.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsDTO(
    var id: Long? = null,
    private var password: String? = null,
    var email: String? = null,
    private var isEnabled: Boolean = false,
    var roles: String,
    var name: String? = null,
    var surname: String? = null,
    var deliveryAddress: String? = null
    ): UserDetails {
    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        for (role in roles.split(','))
            authorities.add(SimpleGrantedAuthority("ROLE_$role"))

        return authorities
    }

    override fun getPassword(): String? = password

    override fun getUsername(): String? = email

    override fun isAccountNonExpired(): Boolean = isEnabled

    override fun isAccountNonLocked(): Boolean = isEnabled

    override fun isCredentialsNonExpired(): Boolean = isEnabled

    override fun isEnabled(): Boolean = isEnabled
}