package lan.jing.backend.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.RelationOneToOne
import com.mybatisflex.annotation.Table
import java.util.*

@Table("restock")
data class MedicineRestock(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var uid: Long? = null,
    var mid: Long? = null,
    var count: Int? = null,
    var price: Double? = null,
    var time: Date? = Date(),
    @RelationOneToOne(selfField = "mid", targetField = "id")
    var medicine: Medicine? = null,
    @RelationOneToOne(selfField = "uid", targetField = "id")
    var user: User? = null
)
