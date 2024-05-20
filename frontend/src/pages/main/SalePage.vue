<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";


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
</script>

<template>
  <div>
    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='all_out--two-tone'></mdui-icon> &nbsp;销售记录
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="refresh" style="margin-right: 8px;margin-bottom: 16px" variant="tonal" @click="refresh(currentPage)">
          刷新
        </mdui-button>
      </div>
      <el-table :data="purchaseList" border stripe style="width: 100%">
        <el-table-column label="订单id" prop="id" width="90"/>
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
      <div style="width: 100%;display: flex;justify-content: center">
        <div style="flex: 1"/>
        <el-pagination v-model:current-page="currentPage" :page-count="totalPage" background layout="prev, pager, next"
                       style="margin: 16px"
                       @current-change="handleCurrentChange"/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>