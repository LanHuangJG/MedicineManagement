package lan.jing.backend.controller

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.all
import com.mybatisflex.kotlin.extensions.db.insert
import com.mybatisflex.kotlin.extensions.kproperty.eq
import lan.jing.backend.entity.Medicine
import lan.jing.backend.entity.MedicineBigType
import lan.jing.backend.entity.MedicineBigTypeWithoutTypes
import lan.jing.backend.entity.MedicineType
import lan.jing.backend.mapper.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/type")
class TypeController {

    data class BigTypeResponse(
        val code: String,
        val message: String,
        val bigTypes: List<MedicineBigType>,
        val totalSize: String
    )

    @Autowired
    private lateinit var medicineMapper: MedicineMapper

    @Autowired
    lateinit var bigTypeMapper: BigTypeMapper

    @GetMapping("/listBigType")
    fun listBigType(@RequestParam page: Int, @RequestParam size: Int): BigTypeResponse {
        val pageQuery = bigTypeMapper.paginateWithRelations(page, size, QueryWrapper.create())
        val records = pageQuery.records
        return BigTypeResponse(
            "200", "获取药品分类成功",
            records,
            pageQuery.totalPage.toString()
        )
    }

    data class TypeResponse(
        val code: String,
        val message: String,
        val types: List<MedicineType>,
        val totalSize: String
    )

    @Autowired
    lateinit var typeMapper: TypeMapper

    @GetMapping("/listType")
    fun listType(@RequestParam page: Int, @RequestParam size: Int): TypeResponse {
        val pageQuery = typeMapper.paginateWithRelations(page, size, QueryWrapper.create())
        val records = pageQuery.records
        return TypeResponse(
            "200", "获取药品种类成功",
            records,
            pageQuery.totalPage.toString()
        )
    }

    @GetMapping("/listBigTypeWithoutTypes")
    fun listBigTypeWithoutTypes(): Map<String, Any> {
        val bigTypes = all<MedicineBigTypeWithoutTypes>()
        return mapOf(
            "code" to "200",
            "message" to "获取药品分类成功",
            "bigTypes" to bigTypes,
        )
    }

    @Autowired
    lateinit var medicineBigTypeWithoutMedicinesMapper: MedicineBigTypeWithoutMedicinesMapper

    @GetMapping("/getTypesByBigType")
    fun getTypesByBigType(@RequestParam name: String): Map<String, Any?> {
        val bigType = medicineBigTypeWithoutMedicinesMapper.selectOneWithRelationsByQuery(
            QueryWrapper.create().select()
                .where(
                    MedicineBigType::name eq name
                )
        )
        return mapOf(
            "code" to "200",
            "message" to "获取药品种类成功",
            "types" to bigType.types,
        )
    }

    data class AddBigTypeRequest(
        val name: String
    )

    data class AddBigTypeResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/addBigType")
    fun addBigType(@RequestBody addBigTypeRequest: AddBigTypeRequest): AddBigTypeResponse {
        insert(
            MedicineBigType(
                name = addBigTypeRequest.name
            )
        )
        return AddBigTypeResponse(
            "200",
            "添加成功"
        )
    }

    data class AddTypeRequest(
        val name: String,
        val bid: Long
    )

    data class AddTypeResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/addType")
    fun addType(@RequestBody addTypeRequest: AddTypeRequest): AddTypeResponse {
        insert(
            MedicineType(
                name = addTypeRequest.name,
                bid = addTypeRequest.bid
            )
        )
        return AddTypeResponse(
            "200",
            "添加成功"
        )
    }

    @PostMapping("/editBigType")
    fun editBigType(@RequestBody medicineBigType: MedicineBigType): Map<String, Any> {
        bigTypeMapper.update(medicineBigType)
        return mapOf(
            "code" to "200",
            "message" to "修改成功"
        )
    }

    @Autowired
    lateinit var medicineBigTypeWithMedicinesMapper: MedicineBigTypeWithMedicinesMapper

    @GetMapping("/getBigTypeWithMedicines")
    fun getBigTypeWithMedicines(
        @RequestParam id: String,
        @RequestParam page: Int,
        @RequestParam size: Int
    ): Map<String, Any?> {
        val medicines = medicineMapper.paginateWithRelations(
            page,
            size,
            QueryWrapper.create().select().where(Medicine::bid eq id.toLong())
        )
        return mapOf(
            "code" to "200",
            "message" to "获取药品成功",
            "medicines" to medicines.records,
            "totalSize" to medicines.totalPage
        )
    }

    @GetMapping("/deleteBigType")
    fun deleteBigType(@RequestParam id: String): Map<String, Any> {
        if (id.toLong() < 16) {
            return mapOf(
                "code" to "400",
                "message" to "无法删除默认分类"
            )
        }
        bigTypeMapper.deleteById(id.toLong())
        return mapOf(
            "code" to "200",
            "message" to "删除成功"
        )
    }

    @GetMapping("/getTypeWithMedicines")
    fun getTypeWithMedicines(
        @RequestParam id: String,
        @RequestParam page: Int,
        @RequestParam size: Int
    ): Map<String, Any?> {
        val medicines = medicineMapper.paginateWithRelations(
            page,
            size,
            QueryWrapper.create().select().where(Medicine::tid eq id.toLong())
        )
        return mapOf(
            "code" to "200",
            "message" to "获取药品成功",
            "medicines" to medicines.records,
            "totalSize" to medicines.totalPage
        )
    }
}