package lan.jing.backend.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.RelationOneToMany
import com.mybatisflex.annotation.Table


@Table("medicine_type")
data class MedicineType(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String?=null,
    var bid: Long? = null,
    @RelationOneToMany(targetField = "tid")
    var medicines: List<Medicine>? = null
)
@Table("medicine_type")
data class MedicineTypeWithMedicines(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String?=null,
    var bid: Long? = null,
)
