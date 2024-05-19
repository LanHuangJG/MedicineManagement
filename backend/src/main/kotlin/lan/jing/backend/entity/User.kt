package lan.jing.backend.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table

@Table("user")
data class User(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var username: String,
    var password: String,
    var email: String,
)
