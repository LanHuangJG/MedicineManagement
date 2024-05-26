package lan.jing.backend.controller

import jakarta.annotation.Resource
import lan.jing.backend.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
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
        //获取以user:开头的所有key
        val keys = redisTemplate.keys("user:*")
        println(keys)
        val list = mutableListOf<UserEntry>()
        keys.forEach {
            println(it)
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
}