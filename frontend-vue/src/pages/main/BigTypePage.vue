<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Delete, Edit, View} from "@element-plus/icons-vue";
import VChart from "vue-echarts";
import {ElMessage} from "element-plus";

interface Type {
  id: string,
  name: string
}

interface BigType {
  count: string,
  name: string,
  types: Type[]
}

function getBigTypeList(page: number, size: number = 10) {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/listBigType?page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      bigTypes.value = res.data.bigTypes as BigType[]
      totalPage.value = Number(res.data.totalSize)
    }
  })
}

const bigTypes = ref<BigType[]>([])
const currentPage = ref(1)
const totalPage = ref(1)
onMounted(() => {
  getBigTypeList(currentPage.value)
  const token =
      useStorage('token', "")
  axios.get("/api/medicine/basicInfo", {
    headers: {
      Authorization: token.value
    }
  }).then(res => {
    bigType.value.series[0].data = res.data.bigTypes.map((item: { name: string; count: number }) => {
      return {
        value: item.count,
        name: item.name
      }
    })
  })
})
const handleCurrentChange = (page: number) => {
  getBigTypeList(page)
}
const refresh = () => {
  getBigTypeList(currentPage.value)

}

const dataType = ref("table")
const bigType = ref({
  title: {
    text: '药品分类',
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
      name: '药品种类数',
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
const bigTypeId = ref(1)
const isMedicinesDialogShow = ref(false)
const currentPageMedicines = ref(1)
const totalPageMedicines = ref(1)
const showMedicinesInBigType = (page: number, size: number = 10) => {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/getBigTypeWithMedicines?id=${bigTypeId.value}&page=${page}&size=${size}`, {
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
const formLabelWidth = '140px'
const addBigTypeDialogVisible = ref(false)
const form = reactive({
  name: "",
})
const handleCurrentMedicineChange = () => {
  showMedicinesInBigType(currentPageMedicines.value)
}

const addBigType = () => {
  const token =
      useStorage('token', "")
  axios.post("/api/type/addBigType", {
    name: form.name
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      ElMessage.success("添加分类成功")
      addBigTypeDialogVisible.value = false
      getBigTypeList(currentPage.value)
    } else {
      ElMessage.error("添加分类失败")
    }
  })
}

const deleteBigType = (id: number = currentDeleteBigTypeId.value) => {
  const token = useStorage('token', "")
  axios.get(`/api/type/deleteBigType?id=${id}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      ElMessage.success("删除分类成功")
      getBigTypeList(currentPage.value)
    } else {
      ElMessage.error(res.data.message)
    }
  })
}
const dialogDeleteVisible = ref(false)
const currentDeleteBigTypeId = ref(1)
const currentEditBigTypeId = ref(1)
const currentEditBigTypeName = ref("")
const editBigTypeDialogVisible = ref(false)
const editBigType = (id: number = currentEditBigTypeId.value) => {
  console.log(id)
  const token = useStorage('token', "")
  axios.post(`/api/type/editBigType`, {
    id: id,
    name: form.name
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      ElMessage.success("编辑分类成功")
      getBigTypeList(currentPage.value)
    } else {
      ElMessage.error(res.data.message)
    }
  })
}
</script>

<template>
  <div style="height: 100%;width: 100%">

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

    <el-dialog v-model="addBigTypeDialogVisible" align-center title="添加分类" width="500">
      <el-form :model="form">
        <el-form-item :label-width="formLabelWidth" label="分类名">
          <el-input v-model="form.name" autocomplete="off" clearable/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addBigTypeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addBigType">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="dialogDeleteVisible"
        align-center
        title="警告"
        width="500"
    >
      <span>确认删除此药品吗，此操作将无法恢复 !!!</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="dialogDeleteVisible = false;deleteBigType()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="editBigTypeDialogVisible"
        align-center
        title="编辑分类"
        width="500">
      <el-form :model="form">
        <el-form-item :label-width="formLabelWidth" label="分类名">
          <el-input v-model="form.name" autocomplete="off" clearable/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editBigTypeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="editBigType();editBigTypeDialogVisible = false">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='dashboard--two-tone'></mdui-icon> &nbsp;分类管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled"
                     @click="addBigTypeDialogVisible=true">添加分类
        </mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal"
                     @click="refresh">
          刷新
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
    <Transition>
      <el-table v-show="dataType==='table'" :data="bigTypes" border stripe style="width: 100%" height="500">
        <el-table-column label="分类id" prop="id" width="120"/>
        <el-table-column label="药品分类" prop="name" width="120"/>
        <el-table-column label="药品种类" prop="types">
          <template v-slot="scope">
            <el-tag v-for="tag in scope.row.types" :key="tag.id" style="margin: 4px" type="warning">{{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="300">
          <template v-slot="scope">
            <div style="width: 100%;display: flex;justify-content: center;align-items: center">
              <el-button :icon="View" type="success" @click="bigTypeId=scope.row.id;showMedicinesInBigType(1,10)">
                查看
              </el-button>
              <el-button :icon="Edit" type="warning"
                         @click="currentEditBigTypeId=scope.row.id;form.name=scope.row.name;showMedicinesInBigType(1,10)">
                编辑
              </el-button>
              <el-button :icon="Delete" type="danger" @click="currentDeleteBigTypeId=scope.row.id
              ;dialogDeleteVisible=true">删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </Transition>
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
    <div style="width: 100%;height:500px" v-if="dataType==='chart'">
      <transition>
        <v-chart :option="bigType" autoresize/>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.chart {
  height: 60%;
  width: 100%;
}
</style>