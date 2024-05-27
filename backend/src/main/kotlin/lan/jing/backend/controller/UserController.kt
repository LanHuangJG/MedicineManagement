package lan.jing.backend.controller

import lan.jing.backend.entity.User
import lan.jing.backend.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController {
    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    lateinit var userMapper: UserMapper

    @PostMapping("/offline")
    fun offline(@RequestBody request: OfflineRequest): OfflineResponse {
        redisTemplate.delete("user:${request.token}")
        return OfflineResponse("200", "下线成功")
    }

    data class OfflineRequest(
        val token: String,
    )

    data class OfflineResponse(
        val code: String,
        val message: String
    )

    data class UserEntry(
        var id: Long? = null,
        var username: String? = null,
        var email: String? = null,
        var token: String? = null
    )

    data class ListResponse(
        val code: String,
        val message: String,
        val users: List<UserEntry>,
    )

    @GetMapping("/list")
    fun list(): ListResponse {
        val keys = redisTemplate.keys("user:*")
        val list = mutableListOf<UserEntry>()
        keys.forEach {
            val user = userMapper.selectOneById(redisTemplate.opsForValue().get(it)!!.toLong())
            list.add(
                UserEntry(
                    user.id,
                    user.username,
                    user.email,
                    it.replace("user:", "")
                )
            )
        }
        return ListResponse("200", "获取用户列表成功", list.toList())
    }

    data class EditRequest(
        val username: String,
        val password: String,
        val originalPassword: String
    )

    data class EditResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/edit")
    fun edit(@RequestBody request: EditRequest): EditResponse {
        val user: User = SecurityContextHolder.getContext().authentication.principal as User
        if(request.originalPassword.isBlank()&&request.password.isBlank()){
            userMapper.update(User(id = user.id, username = request.username))
        }else{
            if (request.username != user.username || request.originalPassword != user.password) return EditResponse(
                "400",
                "修改失败"
            )
            userMapper.update(User(id = user.id, username = request.username, password = request.password))
        }
        return EditResponse("200", "修改成功")
    }
}