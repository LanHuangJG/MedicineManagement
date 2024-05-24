package lan.jing.backend.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.Table

@Table("user")
data class User(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var username: String?=null,
    var password: String?=null,
    var email: String?=null,
)
