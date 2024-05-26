package lan.jing.backend.entity

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.KeyType
import com.mybatisflex.annotation.RelationOneToMany
import com.mybatisflex.annotation.Table

@Table("medicine_big_type")
data class MedicineBigType(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String? = "",
    @RelationOneToMany(selfField = "id", targetField = "bid")
    var types: List<MedicineType>? = null
)

@Table("medicine_big_type")
data class MedicineBigTypeWithMedicines(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String? = "",
    @RelationOneToMany(selfField = "id", targetField = "bid")
    var medicines: List<Medicine>? = null
)

@Table("medicine_big_type")
data class MedicineBigTypeWithoutTypes(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String? = "",
)

@Table("medicine_big_type")
data class MedicineBigTypeWithoutMedicines(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String? = "",
    @RelationOneToMany(selfField = "id", targetField = "bid")
    var types: List<MedicineTypeWithMedicines>? = null
)