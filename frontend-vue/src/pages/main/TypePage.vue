<template>
  <div>
    <el-dialog
        v-model="dialogDeleteVisible"
        align-center
        title="警告"
        width="500"
    >
      <span>确认删除此药品种类吗，此操作将无法恢复 !!!</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="dialogDeleteVisible = false">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="isMedicinesDialogShow" align-center title="药品信息" width="1600">
      <el-table :data="medicines" border height="600" stripe style="width: 100%">
        <el-table-column label="药品id" prop="id" width="100"/>
        <el-table-column label="药品图" prop="image" width="100">
          <template v-slot="scope">
            <el-image :src="scope.row.image" fit="cover" loading="lazy"/>
          </template>
        </el-table-column>
        <el-table-column label="药品名" prop="name" width="90"/>
        <el-table-column label="分类" prop="bigType" width="70">
          <template v-slot="scope">
            <el-tag type="success">
              {{ scope.row.bigType.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="种类" prop="type" width="70">
          <template v-slot="scope">
            <el-tag>
              {{ scope.row.type.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="适应症" prop="indications">
          <template v-slot="scope">
            <el-tag v-for="item in scope.row.indications.split(' ').filter( item=>
              item !== ''
          )" style="margin: 4px" type="danger">
              {{ item }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="主要成分" prop="mainIngredient" width="180"/>
        <el-table-column label="主要功效" prop="functionalIndications" width="100">
          <template v-slot="scope">
            {{ scope.row.functionalIndications.replace("...[详情]", "").replace("[详情]", "") }}
          </template>
        </el-table-column>
        <el-table-column label="使用剂量" prop="dosage" width="100"/>
        <el-table-column label="生产公司" prop="manufacturer" width="100"/>
        <el-table-column label="批准文号" prop="approvalNumber" width="100"/>
      </el-table>
      <div style="display: flex;justify-content: center;margin-top: 16px">
        <div style="flex: 1"/>
        <el-pagination v-model:current-page="currentPageMedicines" :total="totalPageMedicines" background
                       layout="prev, pager, next"
                       @current-change="handleCurrentMedicineChange"
        />
      </div>
    </el-dialog>


    <el-dialog v-model="dialogAddVisible" align-center title="添加种类" width="500">
      <el-form :model="addTypeForm">
        <el-form-item :label-width="formLabelWidth" label="药品分类">
          <el-select
              v-model="bigTypes"
              placeholder="请选择药品分类"
          >
            <el-option
                v-for="item in bigTypes.map(item => {
                return {
                  label: item.name,
                  value: item.name
                }
              })"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品种类">
          <el-input v-model="addTypeForm.type" autocomplete="off" clearable placeholder="请输入药品种类"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogAddVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogAddVisible = false;addType()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='dataset--two-tone'></mdui-icon> &nbsp;种类管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add" style="margin-right: 16px;margin-bottom: 16px" variant="filled" @click="dialogAddVisible=true">添加种类</mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal">刷新
        </mdui-button>
      </div>
    </div>
    <mdui-segmented-button-group :value="dataType" selects="single" style="margin: 0 0 16px 0">
      <mdui-segmented-button icon="table_chart--two-tone" selected-icon="table_chart--two-tone" value="table"
                             @click="dataType='table'">表格
      </mdui-segmented-button>
      <mdui-segmented-button icon="pie_chart--two-tone" selected-icon="pie_chart--two-tone" value="chart"
                             @click="dataType='chart'">图表
      </mdui-segmented-button>
    </mdui-segmented-button-group>
    <transition>
      <el-table v-show="dataType==='table'" :data="types" border stripe style="width: 100%" height="500">
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
          <template v-slot="scope">
            <div style="width: 100%;display: flex;justify-content: center;align-items: center">
              <el-button :icon="View" type="success" @click="typeId=scope.row.id;showMedicinesInType(1,10)">
                查看
              </el-button>
              <el-button :icon="Edit" type="warning">
                编辑
              </el-button>
              <el-button :icon="Delete" type="danger" @click="dialogDeleteVisible=true">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </transition>
    <div class="divider" style="margin: 16px">
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
      <el-pagination v-show="dataType==='table'" v-model:current-page="currentPage" :page-count="totalPage" background
                     layout="prev, pager, next"
                     style="margin: 16px"
                     @current-change="handleCurrentChange"/>
    </div>
    <div v-if="dataType==='chart'" style="width: 100%;height:500px">
      <transition>
        <v-chart :option="type" autoresize/>
      </transition>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Delete, Edit, View} from "@element-plus/icons-vue";
import VChart from "vue-echarts";
import {ElMessage} from "element-plus";

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
  getBigTypeList()
})
const handleCurrentChange = (page: number) => {
  getTypeList(page)
}
const initOption = {
  width: 700,
  height: 300
}
const dataType = ref("table")
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
    left: 'left'
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
const dialogDeleteVisible = ref(false)

interface BigType {
  id: string,
  name: string,
}

function getBigTypeList() {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/listBigTypeWithoutTypes`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      bigTypes.value = res.data.bigTypes as BigType[]
      console.log(bigTypes.value)
    }
  })
}

const bigTypes = ref<BigType[]>([])

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

const medicines = ref<Medicine[]>([])
const typeId = ref(1)
const isMedicinesDialogShow = ref(false)
const currentPageMedicines = ref(1)
const totalPageMedicines = ref(1)
const showMedicinesInType = (page: number, size: number = 10) => {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/getTypeWithMedicines?id=${typeId.value}&page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      medicines.value = res.data.medicines as Medicine[]
      totalPageMedicines.value = Number(res.data.totalSize)
      isMedicinesDialogShow.value = true
      ElMessage.success("获取药品成功")
    }
  })
}
const handleCurrentMedicineChange = () => {
  showMedicinesInType(currentPageMedicines.value)
}
const formLabelWidth = '140px'

const dialogAddVisible = ref(false)

const addTypeForm = reactive({
  bigType: "",
  type: ""
})
const addType = () => {

}
</script>
<style scoped>
.chart {
  height: 60%;
  width: 100%;
}
</style>