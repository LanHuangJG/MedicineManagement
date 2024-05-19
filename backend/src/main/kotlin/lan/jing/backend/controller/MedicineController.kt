package lan.jing.backend.controller

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.all
import com.mybatisflex.kotlin.extensions.db.filterOne
import com.mybatisflex.kotlin.extensions.db.insert
import com.mybatisflex.kotlin.extensions.kproperty.eq
import lan.jing.backend.entity.Medicine
import lan.jing.backend.entity.MedicineBigType
import lan.jing.backend.entity.MedicineType
import lan.jing.backend.mapper.MedicineMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/medicine")
class MedicineController {

    data class BasicInfoResponse(
        val medicineCount: String,
        val bigTypeCount: String = "0",
        val typeCount: String = "0",
        val bigTypes: List<BigTypeWithCount> = emptyList(),
        val types: List<TypeWithCount> = emptyList()
    )

    data class BigTypeWithCount(
        val name: String?,
        val count: Int
    )

    data class TypeWithCount(
        val name: String?,
        val count: Int
    )

    @GetMapping("/basicInfo")
    fun basicInfo(): BasicInfoResponse {
        val medicines = all<Medicine>()
        val bigTypes = all<MedicineBigType>()
        val types = all<MedicineType>()
        return BasicInfoResponse(
            medicineCount = medicines.size.toString(),
            bigTypeCount = bigTypes.size.toString(),
            typeCount = types.size.toString(),
            bigTypes = bigTypes.map { bigType ->
                val count = types.count { it.bid == bigType.id }
                BigTypeWithCount(bigType.name, count)
            },
            types = types.map { type ->
                val count = medicines.count { it.tid == type.id }
                TypeWithCount(type.name, count)
            }
        )
    }

    data class ListResponse(
        val code: String,
        val message: String,
        val list: List<Medicine>,
        val totalSize: String
    )

    @Autowired
    lateinit var medicineMapper: MedicineMapper

    @GetMapping("/list")
    fun list(@RequestParam page: Int, @RequestParam size: Int): ListResponse {
        val query = medicineMapper.paginateWithRelations(page, size, QueryWrapper.create())
        val list = query.records
        return ListResponse(
            code = "200",
            message = "获取成功",
            list = list,
            totalSize = query.totalPage.toString()
        )
    }

    data class TypeResponse(
        val code: String,
        val message: String,
        val list: List<MedicineType>,
        val totalSize: String
    )

    @GetMapping("/type")
    fun type(): TypeResponse {
        val types = all<MedicineType>()
        return TypeResponse(
            code = "200",
            message = "获取成功",
            list = types,
            totalSize = types.size.toString()
        )
    }

    @PostMapping("/addMedicine")
    fun addMedicine(@RequestBody medicine: Medicine) {
        insert(medicine)
    }

    data class UpdateMedicineRequest(
        var id: Long? = null,
        var name: String? = null,
        var type: String? = null,
        var bigType: String? = null,
        var image: String? = null,
        var indications: String? = null,
        var mainIngredient: String? = null,
        var functionalIndications: String? = null,
        var dosage: String? = null,
        var approvalNumber: String? = null,
        var manufacturer: String? = null,
    )

    data class UpdateMedicineResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/updateMedicine")
    fun updateMedicine(@RequestBody updateMedicineRequest: UpdateMedicineRequest): UpdateMedicineResponse {
        if (updateMedicineRequest.id == null || updateMedicineRequest.type == null || updateMedicineRequest.bigType == null) {
            return UpdateMedicineResponse("400", "参数错误")
        }
        val type = filterOne<MedicineType> {
            MedicineType::name eq updateMedicineRequest.type
        }
        val bigType = filterOne<MedicineBigType> {
            MedicineBigType::name eq updateMedicineRequest.bigType
        }
        medicineMapper.update(
            Medicine(
                id = updateMedicineRequest.id,
                name = updateMedicineRequest.name,
                tid = type?.id,
                bid = bigType?.id,
                image = updateMedicineRequest.image,
                indications = updateMedicineRequest.indications,
                mainIngredient = updateMedicineRequest.mainIngredient,
                functionalIndications = updateMedicineRequest.functionalIndications,
                dosage = updateMedicineRequest.dosage,
                approvalNumber = updateMedicineRequest.approvalNumber,
                manufacturer = updateMedicineRequest.manufacturer
            )
        )
        return UpdateMedicineResponse("200", "更新成功")
    }
}