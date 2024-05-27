<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {ElMessage} from "element-plus";
import VChart from "vue-echarts";


interface Type {
  id: string,
  name: string
}

interface BigType {
  id: string,
  name: string,
}

interface Medicine {
  id: number,
  name: string,
  tid: number,
  bid: number,
  image: string,
  indications: string,
  mainIngredient: string,
  functionalIndications: string,
  dosage: string,
  approvalNumber: string,
  manufacturer: string,
  type: Type,
  bigType: BigType
}


interface User {
  id: number
  username: string
  email: string
  password: string
}

interface Purchase {
  id: number
  mid: number
  uid: number
  count: number
  price: number
  time: Date
  medicine: Medicine
  user: User
}

const currentPage = ref(1)
const totalPage = ref(1)
const purchaseList = ref<Purchase[]>([])
onMounted(() => {
  refresh(1)
})
const refresh = (page: number, size: number = 10) => {
  const token = useStorage('token', "")
  axios.get(`/api/trade/listPurchase?page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      totalPage.value = Number(res.data.totalSize)
      purchaseList.value = res.data.list
    }
  })
}
const handleCurrentChange = (val: number) => {
  refresh(val)
}
const dialogAddVisible = ref(false)
const formLabelWidth = '140px'
const restockMedicineForm = reactive({
  rid: "",
  count: "",
  time: ""
})
const purchase = () => {
  const token = useStorage('token', "")
  axios.post("/api/trade/purchase", restockMedicineForm, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      refresh(currentPage.value)
      ElMessage.success("购买成功")
    } else {
      ElMessage.error("购买失败")
    }
  })
}

const dataType = ref("table")

const option = {
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'bar'
    }
  ]
};
</script>

<template>
  <div>
    <el-dialog v-model="dialogAddVisible" align-center title="药品购买" width="500">
      <el-form :model="restockMedicineForm">
        <el-form-item :label-width="formLabelWidth" label="进货订单id">
          <el-input v-model="restockMedicineForm.rid" autocomplete="off" clearable placeholder="请输入进货订单id"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="购买数目">
          <el-input v-model="restockMedicineForm.count" autocomplete="off" clearable placeholder="请输入购买数目"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品购买日期">
          <el-date-picker
              v-model="restockMedicineForm.time"
              type="date"
              placeholder="请选择药品购买日期"
              style="width: 100%"
              format="YYYY/MM/DD"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogAddVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogAddVisible = false;purchase()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>


    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='all_out--two-tone'></mdui-icon> &nbsp;销售记录
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add_shopping_cart--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled"
                     @click="dialogAddVisible=true">购买药品
        </mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal"
                     @click="refresh(currentPage)">
          刷新
        </mdui-button>
      </div>
      <div style="display: flex">
        <mdui-segmented-button-group :value="dataType" selects="single" style="margin: 0 0 16px 0">
          <mdui-segmented-button icon="table_chart--two-tone" selected-icon="table_chart--two-tone" value="table"
                                 @click="dataType='table'">表格
          </mdui-segmented-button>
          <mdui-segmented-button icon="pie_chart--two-tone" selected-icon="pie_chart--two-tone" value="chart"
                                 @click="dataType='chart'">图表
          </mdui-segmented-button>
        </mdui-segmented-button-group>
        <div style="flex: 1"/>
        <mdui-segmented-button-group :value="dataType" selects="single" style="margin: 0 8px 16px 0" v-if="dataType==='chart'">
          <mdui-segmented-button icon="today--two-tone" selected-icon="today--two-tone" value="table"
                                 @click="dataType='table'">近七日
          </mdui-segmented-button>
          <mdui-segmented-button icon="calendar_month--two-tone" selected-icon="calendar_month--two-tone" value="chart"
                                 @click="dataType='chart'">近七月
          </mdui-segmented-button>
          <mdui-segmented-button icon="access_time--two-tone" selected-icon="access_time--two-tone" value="chart"
                                 @click="dataType='chart'">近七年
          </mdui-segmented-button>
        </mdui-segmented-button-group>
      </div>
      <el-table :data="purchaseList" border stripe style="width: 100%" height="500" v-if="dataType === 'table'">
        <el-table-column label="购买订单id" prop="id" width="100"/>
        <el-table-column label="药品图片" prop="medicine" width="100">
          <template v-slot="scope">
            <el-image :src="scope.row.medicine.image" fit="cover" loading="lazy"/>
          </template>
        </el-table-column>
        <el-table-column label="药品名" prop="medicine">
          <template v-slot="scope">
            {{ scope.row.medicine.name }}
          </template>
        </el-table-column>
        <el-table-column label="购买用户" prop="user" width="150">
          <template v-slot="scope">
            {{ scope.row.user.username }}
          </template>
        </el-table-column>
        <el-table-column label="药品单价" prop="price" width="150"/>
        <el-table-column label="购买时间" prop="time" width="150">
          <template v-slot="scope">
            {{ new Date(scope.row.time).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column label="购买数量" prop="count" width="180"/>
      </el-table>
      <div class="divider" style="margin-top: 16px">
        <svg aria-hidden="true" data-v-6a29f87a="" fill="none" height="8" width="100%"
             xmlns="http://www.w3.org/2000/svg">
          <pattern id="a" data-v-6a29f87a="" height="8" patternUnits="userSpaceOnUse" width="91">
            <g clip-path="url(#clip0_2426_11367)" data-v-6a29f87a="">
              <path
                  d="M114 4c-5.067 4.667-10.133 4.667-15.2 0S88.667-.667 83.6 4 73.467 8.667 68.4 4 58.267-.667 53.2 4 43.067 8.667 38 4 27.867-.667 22.8 4 12.667 8.667 7.6 4-2.533-.667-7.6 4s-10.133 4.667-15.2 0S-32.933-.667-38 4s-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0-10.133-4.667-15.2 0-10.133 4.667-15.2 0"
                  data-v-6a29f87a="" stroke="#E1E3E1" stroke-linecap="square"></path>
            </g>
          </pattern>
          <rect data-v-6a29f87a="" fill="url(#a)" height="100%" width="100%"></rect>
        </svg>
      </div>
      <div v-if="dataType==='chart'" style="width: 100%;height:500px">
        <transition>
          <v-chart :option="option" autoresize/>
        </transition>
      </div>
      <div style="width: 100%;display: flex;justify-content: center">
        <div style="flex: 1"/>
        <el-pagination v-model:current-page="currentPage" :page-count="totalPage" background layout="prev, pager, next"
                       style="margin: 16px"
                       v-if="dataType === 'table'"
                       @current-change="handleCurrentChange"/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>