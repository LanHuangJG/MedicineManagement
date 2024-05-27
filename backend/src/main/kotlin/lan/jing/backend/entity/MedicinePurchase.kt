package lan.jing.backend.entity

import com.mybatisflex.annotation.*
import java.time.LocalDateTime

@Table("purchase")
data class MedicinePurchase(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var mid: Long? = null,
    var uid: Long? = null,
    var rid: Long? = null,
    var count: Int? = null,
    var price: Double? = null,
    var time: LocalDateTime? = null,
    @RelationOneToOne(selfField = "mid", targetField = "id")
    var medicine: Medicine? = null,
    @RelationOneToOne(selfField = "uid", targetField = "id")
    var user: User? = null
)
