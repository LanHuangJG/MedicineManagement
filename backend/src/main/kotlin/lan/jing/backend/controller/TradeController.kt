package lan.jing.backend.controller

import com.mybatisflex.core.query.QueryWrapper
import com.mybatisflex.kotlin.extensions.db.insert
import lan.jing.backend.entity.MedicinePurchase
import lan.jing.backend.entity.MedicineRestock
import lan.jing.backend.mapper.PurchaseMapper
import lan.jing.backend.mapper.RestockMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/trade")
class TradeController {
    data class PurchaseRequest(
        val rid: Long,
        val count: Int
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
            if (restock.count!! < purchaseRequest.count) return PurchaseResponse("", "购买失败")
            insert(
                MedicinePurchase(
                    mid = restock.mid,
                    uid = userId.toLong(),
                    rid = purchaseRequest.rid,
                    count = purchaseRequest.count,
                    price = restock.price,
                    time = Date()
                )
            )
            restockMapper.update(
                MedicineRestock(
                    id = purchaseRequest.rid,
                    count = restock.count!! - purchaseRequest.count
                )
            )
            return PurchaseResponse("200", "购买成功")
        }
    }

    data class RestockRequest(
        val mid: Long,
        val count: Int,
        val price: Double
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
            insert(
                MedicineRestock(
                    mid = restockRequest.mid,
                    uid = userId.toLong(),
                    count = restockRequest.count,
                    price = restockRequest.price,
                    time = Date()
                )
            )
            return RestockResponse("200", "进货成功")
        }
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
}