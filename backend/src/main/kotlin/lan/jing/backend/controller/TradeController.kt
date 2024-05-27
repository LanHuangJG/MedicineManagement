package lan.jing.backend.controller

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.filterOne
import com.mybatisflex.kotlin.extensions.db.insert
import com.mybatisflex.kotlin.extensions.kproperty.eq
import lan.jing.backend.entity.MedicinePurchase
import lan.jing.backend.entity.MedicineRestock
import lan.jing.backend.mapper.PurchaseMapper
import lan.jing.backend.mapper.RestockMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RestController
@RequestMapping("/trade")
class TradeController {
    data class PurchaseRequest(
        val rid: Long,
        val count: Int,
        val time: String
    )

    data class PurchaseResponse(
        val code: String,
        val message: String
    )

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    lateinit var purchaseMapper: PurchaseMapper

    @Autowired
    lateinit var restockMapper: RestockMapper

    @PostMapping("/purchase")
    fun purchase(
        @RequestBody purchaseRequest: PurchaseRequest,
        @RequestHeader("Authorization") token: String
    ): PurchaseResponse {
        val userId = redisTemplate.opsForValue().get("user:$token")
        if (userId == null) return PurchaseResponse("", "购买失败")
        else {
            val restock =
                restockMapper.selectOneWithRelationsById(purchaseRequest.rid) ?: return PurchaseResponse("", "购买失败")
            if (restock.presentCount!! < purchaseRequest.count) return PurchaseResponse("", "购买失败")
            val localDate = LocalDate.parse(purchaseRequest.time, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val localDateTime = localDate.atStartOfDay()
            insert(
                MedicinePurchase(
                    mid = restock.mid,
                    uid = userId.toLong(),
                    rid = purchaseRequest.rid,
                    count = purchaseRequest.count,
                    price = restock.outPrice,
                    time = localDateTime
                )
            )
            restockMapper.update(
                MedicineRestock(
                    id = purchaseRequest.rid,
                    presentCount = restock.presentCount!! - purchaseRequest.count
                )
            )
            return PurchaseResponse("200", "购买成功")
        }
    }

    data class RestockRequest(
        val mid: Long,
        val count: Int,
        val price: Double,
        val outPrice: Double,
        val time: String
    )

    data class RestockResponse(
        val code: String,
        val message: String
    )

    @PostMapping("/restock")
    fun restock(
        @RequestBody restockRequest: RestockRequest,
        @RequestHeader("Authorization") token: String
    ): RestockResponse {
        val userId = redisTemplate.opsForValue().get("user:$token")
        if (userId == null) return RestockResponse("", "进货失败")
        else {
            val localDate = LocalDate.parse(restockRequest.time, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val localDateTime = localDate.atStartOfDay()
            insert(
                MedicineRestock(
                    mid = restockRequest.mid,
                    uid = userId.toLong(),
                    count = restockRequest.count,
                    presentCount = restockRequest.count,
                    price = restockRequest.price,
                    outPrice = restockRequest.outPrice,
                    time = localDateTime
                )
            )
            return RestockResponse("200", "进货成功")
        }
    }

    data class RestockEditRequest(
        val id: Long,
        val count: Int,
        val price: Double,
        val time: String
    )

    data class RestockEditResponse(
        val code: String,
        val message: String
    )


    @PostMapping("/editRestock")
    fun editRestock(
        @RequestBody restockEditRequest: RestockEditRequest
    ): RestockEditResponse {
        val restock = filterOne<MedicineRestock> {
            MedicineRestock::id eq restockEditRequest.id
        } ?: return RestockEditResponse("400", "进货失败")
        if (restock.count!! < restockEditRequest.count) return RestockEditResponse("400", "进货失败")
        val localDate = LocalDate.parse(restockEditRequest.time, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val localDateTime = localDate.atStartOfDay()
        restockMapper.update(
            MedicineRestock(
                id = restockEditRequest.id,
                presentCount = restockEditRequest.count,
                outPrice = restockEditRequest.price,
                time = localDateTime
            )
        )
        return RestockEditResponse("200", "进货成功")
    }


    data class ListPurchaseResponse(
        val code: String,
        val message: String,
        val list: List<MedicinePurchase>,
        val totalSize: String
    )

    @GetMapping("/listPurchase")
    fun listPurchase(@RequestParam page: Int, @RequestParam size: Int): ListPurchaseResponse {
        val query = purchaseMapper.paginateWithRelations(page, size, QueryWrapper.create())
        val list = query.records
        return ListPurchaseResponse(
            code = "200",
            message = "获取成功",
            list = list,
            totalSize = query.totalPage.toString()
        )
    }

    data class ListRestockResponse(
        val code: String,
        val message: String,
        val list: List<MedicineRestock>,
        val totalSize: String
    )

    @GetMapping("/listRestock")
    fun listRestock(@RequestParam page: Int, @RequestParam size: Int): ListRestockResponse {
        val query = restockMapper.paginateWithRelations(page, size, QueryWrapper.create())
        val list = query.records
        return ListRestockResponse(
            code = "200",
            message = "获取成功",
            list = list,
            totalSize = query.totalPage.toString()
        )
    }

    data class DeletePurchaseRequest(
        val id: Long
    )

    @PostMapping("/deleteRestock")
    fun deleteRestock(@RequestBody deletePurchaseRequest: DeletePurchaseRequest): RestockResponse {
        if (deletePurchaseRequest.id < 16) return RestockResponse("400", "默认进货不可删除")
        try {
            restockMapper.deleteById(deletePurchaseRequest.id)
        } catch (e: Exception) {
            return RestockResponse("400", "删除失败")
        }
        return RestockResponse("200", "删除成功")
    }

    data class ResStockCountByYearResponse(
        val code: String,
        val message: String,
        val counts: List<Int>,
        val years: List<String>
    )

    @GetMapping("/restockCountByYear")
    fun stockCountByYear(): ResStockCountByYearResponse {
        //近七年
        val years = mutableListOf<String>()
        val counts = mutableListOf<Int>()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 0..6) {
            years.add((year - i).toString())
            counts.add(restockMapper.countByYear(year - i))
        }
        return ResStockCountByYearResponse("200", "获取成功", counts.reversed(), years.reversed())
    }

    data class ResStockCountByMonthResponse(
        val code: String,
        val message: String,
        val counts: List<Int>,
        val months: List<String>
    )

    @GetMapping("/restockCountByMonth")
    fun stockCountByMonth(): ResStockCountByMonthResponse {
        //近七个月
        val months = mutableListOf<String>()
        val counts = mutableListOf<Int>()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        for (i in 0..6) {
            if (month - i < 0) {
                months.add("${year - 1}-${month - i + 12}")
                counts.add(restockMapper.countByMonth(year - 1, month - i + 12))
            } else {
                months.add("$year-${month - i + 1}")
                counts.add(restockMapper.countByMonth(year, month - i + 1))
            }
        }
        return ResStockCountByMonthResponse("200", "获取成功", counts.reversed(), months.reversed())
    }

    data class ResStockCountByDayResponse(
        val code: String,
        val message: String,
        val counts: List<Int?>,
        val days: List<String?>
    )

    @GetMapping("/restockCountByDay")
    fun stockCountByDay(): ResStockCountByDayResponse {
        //近七天
        val days = mutableListOf<String>()
        val counts = mutableListOf<Int>()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        for (i in 0..6) {
            if (day - i < 0) {
                if (month - 1 < 0) {
                    days.add("${year - 1}-${month + 11}-${day - i + 30}")
                    counts.add(restockMapper.countByDay(year - 1, month + 11, day - i + 30))
                } else {
                    days.add("$year-${month + 1}-${day - i + 30}")
                    counts.add(restockMapper.countByDay(year, month + 1, day - i + 30))
                }
            } else {
                days.add("$year-${month + 1}-${day - i}")
                counts.add(restockMapper.countByDay(year, month + 1, day - i))
            }
        }
        return ResStockCountByDayResponse("200", "获取成功", counts.reversed(), days.reversed())
    }
}