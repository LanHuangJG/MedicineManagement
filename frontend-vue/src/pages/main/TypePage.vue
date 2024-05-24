<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Delete, Edit, View} from "@element-plus/icons-vue";
import VChart from "vue-echarts";
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
  manufacturer: string
}

interface Type {
  id: string,
  name: string,
  medicines: Medicine[]
}

function getTypeList(page: number, size: number = 10) {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/listType?page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      types.value = res.data.types as Type[]
      totalPage.value = Number(res.data.totalSize)
    }
  })
}

const types = ref<Type[]>([])
const currentPage = ref(1)
const totalPage = ref(1)
onMounted(() => {
  getTypeList(currentPage.value)
  const token =
      useStorage('token', "")
  axios.get("/api/medicine/basicInfo", {
    headers: {
      Authorization: token.value
    }
  }).then(res => {
    type.value.series[0].data = res.data.types.filter((item: { count: number }) => item.count !== 0)
        .map((item: { name: string; count: number }) => {
          return {
            value: item.count,
            name: item.name
          }
        })
  })
})
const handleCurrentChange = (page: number) => {
  getTypeList(page)
}
const initOption = {
  width: 700,
  height: 300
}
const dataType = ref("chart")
const type = ref({
  title: {
    text: '药品种类',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'right'
  },
  series: [
    {
      name: '药品数',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
});
</script>

<template>
  <div>
    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='dataset--two-tone'></mdui-icon> &nbsp;种类管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add" style="margin-right: 16px;margin-bottom: 16px" variant="filled">添加种类</mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal">刷新
        </mdui-button>
      </div>
    </div>
    <transition>
      <el-table :data="types" border stripe style="width: 100%" v-show="dataType==='table'">
        <el-table-column label="种类id" prop="id" width="120"/>
        <el-table-column label="药品种类" prop="name" width="120"/>
        <el-table-column label="药品数量" prop="medicines">
          <template v-slot="scope">
            <el-tag type="success">
              {{ scope.row.medicines.length }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="300">
          <div style="width: 100%;display: flex;justify-content: center;align-items: center">
            <el-button :icon="View" type="success">
              查看
            </el-button>
            <el-button :icon="Edit" type="warning">
              编辑
            </el-button>
            <el-button :icon="Delete" type="danger">删除</el-button>
          </div>
        </el-table-column>
      </el-table>
    </transition>
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
      <mdui-segmented-button-group :value="dataType" selects="single" style="margin: 16px">
        <mdui-segmented-button icon="table_chart--two-tone" selected-icon="table_chart--two-tone" value="table"
                               @click="dataType='table'">表格
        </mdui-segmented-button>
        <mdui-segmented-button icon="pie_chart--two-tone" selected-icon="pie_chart--two-tone" value="chart"
                               @click="dataType='chart'">图表
        </mdui-segmented-button>
      </mdui-segmented-button-group>
      <div style="flex: 1"/>
      <el-pagination v-show="dataType==='table'" v-model:current-page="currentPage" :page-count="totalPage" background
                     layout="prev, pager, next"
                     style="margin: 16px"
                     @current-change="handleCurrentChange"/>
    </div>
    <transition>
      <v-chart v-show="dataType==='chart'" :option="type" autoresize :initOption="initOption" class="chart"/>
    </transition>
  </div>
</template>

<style scoped>
.chart {
  height: 60%;
  width: 100%;
}
</style>