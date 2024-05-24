package lan.jing.backend.entity

import com.mybatisflex.annotation.*

@Table("medicine")
data class Medicine(
    @Id(keyType = KeyType.Auto)
    var id: Long? = null,
    var name: String? = null,
    var tid: Long? = null,
    var bid: Long? = null,
    var image: String? = null,
    var indications: String? = null,
    var mainIngredient: String? = null,
    var functionalIndications: String? = null,
    var dosage: String? = null,
    var approvalNumber: String? = null,
    var manufacturer: String? = null,
    @RelationOneToOne(selfField = "tid", targetField = "id")
    var type: MedicineTypeWithMedicines? = null,
    @RelationOneToOne(selfField = "bid", targetField = "id")
    var bigType: MedicineBigTypeWithoutTypes? = null,
    @Column(isLogicDelete = true)
    var isDelete: Boolean = false
)
