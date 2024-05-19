package lan.jing.backend.controller

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.all
import com.mybatisflex.kotlin.extensions.kproperty.eq
import lan.jing.backend.entity.MedicineBigType
import lan.jing.backend.entity.MedicineBigTypeWithoutTypes
import lan.jing.backend.entity.MedicineType
import lan.jing.backend.mapper.BigTypeMapper
import lan.jing.backend.mapper.MedicineBigTypeWithoutMedicinesMapper
import lan.jing.backend.mapper.TypeMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
}