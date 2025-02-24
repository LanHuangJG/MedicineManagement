package lan.jing.backend.mapper

import com.mybatisflex.core.BaseMapper
import lan.jing.backend.entity.MedicinePurchase
import org.apache.ibatis.annotations.Select

interface PurchaseMapper : BaseMapper<MedicinePurchase> {
    @Select("SELECT count(*) FROM purchase where year(time)=#{year}")
    fun countByYear(year: Int): Int

    @Select("SELECT count(*) FROM purchase where month(time)=#{month} and year(time)=#{year}")
    fun countByMonth(year: Int,month: Int): Int

    @Select("SELECT count(*) FROM purchase where day(time)=#{day} and month(time)=#{month} and year(time)=#{year}")
    fun countByDay(year: Int,month: Int,day: Int): Int
}