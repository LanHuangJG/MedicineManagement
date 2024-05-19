package lan.jing.backend.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.MacAlgorithm
import lan.jing.backend.entity.User
import org.springframework.stereotype.Component
import javax.crypto.SecretKey


@Component
class JwtUtil {
    private var alg: MacAlgorithm = Jwts.SIG.HS512
    private var key: SecretKey = alg.key().build()
    fun encode(user: User): String =
        Jwts.builder()
            .claim("username", user.username)
            .claim("id", user.id)
            .signWith(key, alg).compact()

    fun decodeUsername(token: String): String = kotlin.runCatching {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload["username"].toString()
    }.getOrDefault("")

    fun decodeId(token: String): Int =
        kotlin.runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload["id"].toString().toInt()
        }.getOrDefault(-1)
}