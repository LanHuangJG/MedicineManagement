package lan.jing.backend.filter

import com.mybatisflex.kotlin.extensions.db.filterOne
import com.mybatisflex.kotlin.extensions.kproperty.eq
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lan.jing.backend.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("Authorization")
        if (token != null) {
            val userId = redisTemplate.opsForValue().get("user:$token")
            val user = filterOne<User> {
                User::id eq userId
            }
            if (user != null) {
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(user, token, emptyList())
            }
        }
        filterChain.doFilter(request, response)
    }
}