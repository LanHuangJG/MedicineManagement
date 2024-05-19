package lan.jing.backend.controller

import com.mybatisflex.kotlin.extensions.db.filterOne
import com.mybatisflex.kotlin.extensions.db.insert
import com.mybatisflex.kotlin.extensions.kproperty.eq
import lan.jing.backend.entity.User
import lan.jing.backend.util.EmailUtil
import lan.jing.backend.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit


@RestController
@RequestMapping("/auth")
class AuthController {
    data class LoginRequest(
        val username: String = "",
        val password: String
    )


    data class LoginResponse(
        val code: String,
        val message: String,
        val token: String
    )

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var emailUtil: EmailUtil

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse {
        val user = filterOne<User> {
            User::username eq loginRequest.username
        }
        if (user != null && user.password == loginRequest.password) {
            val token = jwtUtil.encode(user)
            redisTemplate.opsForValue().set("user:$token", user.id.toString(), 6 * 32, TimeUnit.DAYS)
            return LoginResponse(
                "200", "登录成功",
                token
            )
        }
        return LoginResponse("400", "登录失败", "")
    }

    data class RegisterRequest(
        val username: String,
        val password: String,
        val email: String,
        val code: String
    )

    data class RegisterResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): RegisterResponse {
        val user = User(
            username = registerRequest.username,
            password = registerRequest.password,
            email = registerRequest.email
        )
        val code = redisTemplate.opsForValue().get("email:" + registerRequest.email)
        if (code == registerRequest.code) {
            try {
                insert(user)
                redisTemplate.delete("email:" + registerRequest.email)
                return RegisterResponse("200", "注册成功")
            } catch (e: Exception) {
                return RegisterResponse("400", "注册失败")
            }
        }
        return RegisterResponse("400", "注册失败")
    }

    data class MailRequest(
        val to: String,
        val type: String
    )

    data class MailResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/sendEmail")
    fun sendMail(@RequestBody mailRequest: MailRequest): MailResponse {
        val email = if (mailRequest.type == "login") {
            val user = filterOne<User> {
                User::username eq mailRequest.to
            }
            user?.email
        } else {
            mailRequest.to
        }
        val emailIsExist = redisTemplate.opsForValue().get("email:$email")
        if (emailIsExist != null) return MailResponse("400", "验证码发送失败")
        else {
            if (email == null) return MailResponse("400", "验证码发送失败")
            else {
                emailUtil.sendCode(email)
                redisTemplate.opsForValue().set("email:$email", emailUtil.code, 1, TimeUnit.MINUTES)
                return MailResponse("200", "验证码已发送")
            }
        }
    }

    data class LoginByCodeRequest(
        val username: String = "",
        val code: String
    )

    data class LoginByCodeResponse(
        val message: String,
        val code: String,
        val token: String = ""
    )

    @PostMapping("/loginByCode")
    fun loginByCode(@RequestBody loginByCodeRequest: LoginByCodeRequest): LoginByCodeResponse {
        val user = filterOne<User> {
            User::username eq loginByCodeRequest.username
        }
        if (user == null) return LoginByCodeResponse("登录失败", "400")
        val code = redisTemplate.opsForValue().get("email:" + user.email)
        if (code == loginByCodeRequest.code) {
            val token = jwtUtil.encode(user)
            redisTemplate.opsForValue().set("user:$token", user.id.toString(), 6 * 32, TimeUnit.DAYS)
            redisTemplate.delete("email:" + user.email)
            return LoginByCodeResponse("登录成功", "200", token)
        }
        return LoginByCodeResponse("登录失败", "400")
    }

    data class InfoResponse(
        val username: String,
        val email: String,
        val code: String
    )

    @GetMapping("/info")
    fun info(@RequestHeader("Authorization") token: String): InfoResponse {
        val userId = redisTemplate.opsForValue().get("user:$token")
        if (userId == null) return InfoResponse("", "", "400")
        else {
            val user = SecurityContextHolder.getContext().authentication.principal as User
            return InfoResponse(user.username, user.email, "200")
        }
    }

    @GetMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String): Map<String, String> {
        redisTemplate.delete(token)
        return mapOf(
            "code" to "200",
            "message" to "退出登录成功"
        )
    }
}