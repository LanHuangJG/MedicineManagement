<template>
  <div style="height: 100%">
    <el-dialog v-model="dialogAddVisible" align-center title="药品进货" width="500">
      <el-form :model="restockMedicineForm">
        <el-form-item :label-width="formLabelWidth" label="药品id">
          <el-input v-model="restockMedicineForm.id" autocomplete="off" clearable placeholder="请输入药品id"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="进货数目">
          <el-input-number v-model="restockMedicineForm.count" autocomplete="off" clearable placeholder="请输入进货数目"
                           style="width: 100%"/>

        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品单价">
          <el-input v-model="restockMedicineForm.price" autocomplete="off" clearable placeholder="请输入药品单价"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogAddVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogAddVisible = false;restock()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" align-center title="进货编辑" width="500">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="进货药品id">
          <el-input v-model="editForm.id" autocomplete="off" clearable disabled placeholder="请输入药品id"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="进货药品数目">
          <el-input-number v-model="editForm.count" autocomplete="off" clearable placeholder="请输入进货数目"
                           style="width: 100%"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品单价">
          <el-input v-model="editForm.price" autocomplete="off" clearable placeholder="请输入药品单价"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="editDialogVisible = false;editRestock()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>


    <!--    删除药品    -->
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
          <el-button type="danger" @click="dialogDeleteVisible = false;deleteStock()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>


    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='catching_pokemon--two-tone'></mdui-icon> &nbsp;进货管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="input--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled"
                     @click="dialogAddVisible=true">进货
        </mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal">刷新
        </mdui-button>
      </div>
      <el-table :data="restockList" border stripe style="width: 100%">
        <el-table-column label="进货订单id" prop="id" width="100"/>
        <el-table-column label="药品图片" prop="medicine" width="100">
          <template v-slot="scope">
            <el-image :src="scope.row.medicine.image" fit="cover" loading="lazy"/>
          </template>
        </el-table-column>
        <el-table-column label="药品id" prop="medicine" width="150">
          <template v-slot="scope">
            {{ scope.row.medicine.id }}
          </template>
        </el-table-column>
        <el-table-column label="药品名" prop="medicine">
          <template v-slot="scope">
            {{ scope.row.medicine.name }}
          </template>
        </el-table-column>
        <el-table-column label="进货用户" prop="user" width="150">
          <template v-slot="scope">
            {{ scope.row.user.username }}
          </template>
        </el-table-column>
        <el-table-column label="进货时间" prop="time" width="150">
          <template v-slot="scope">
            {{ new Date(scope.row.time).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column label="药品数量" prop="count" width="180"/>
        <el-table-column label="进货单价" prop="price" width="150"/>
        <el-table-column fixed="right" label="操作" width="180">
          <template v-slot="scope">
            <el-button type="warning" @click="editDialogVisible=true;editForm.id=scope.row.id;
editForm.count= scope.row.count;editForm.price = scope.row.price
">编辑
            </el-button>
            <el-button type="danger" @click="dialogDeleteVisible=true;deleteId=scope.row.id">删除</el-button>
          </template>
        </el-table-column>
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


        <!--  分页组件    -->


        <el-pagination v-model:current-page="currentPage" :page-count="totalPage" background layout="prev, pager, next"
                       style="margin: 16px"
                       @current-change="handleCurrentChange"/>
      </div>
    </div>
  </div>

</template>

<script lang="ts" setup>
import {useStorage} from "@vueuse/core";
import {onMounted, reactive, ref, watch} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";

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

interface Restock {
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
const restockList = ref<Restock[]>([])
const bigType = ref("")
const types = ref<Type[]>([])
const dialogAddVisible = ref(false)
const formLabelWidth = '140px'
const restockMedicineForm = reactive({
  id: "",
  count: "",
  price: "",
})

function loadTypes(name: string) {
  const token =
      useStorage('token', "")
  axios.get(`/api/type/getTypesByBigType?name=${name}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    types.value = res.data.types
  })
}

watch(bigType, (value) => {
  loadTypes(value)
})
const bigTypes = ref<BigType[]>([])
onMounted(() => {
  const token =
      useStorage('token', "")
  refresh(1)
  axios.get("/api/type//listBigTypeWithoutTypes", {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    bigTypes.value = res.data.bigTypes
  })
})
const refresh = (page: number, size: number = 10) => {
  const token = useStorage('token', "")
  axios.get(`/api/trade/listRestock?page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      restockList.value = res.data.list as Restock[]
      totalPage.value = Number(res.data.totalSize)
    }
  })
}
const handleCurrentChange = (val: number) => {
  refresh(val)
}
const restock = () => {
  const token = useStorage('token', "")
  axios.post("/api/trade/restock", {
    mid: restockMedicineForm.id,
    count: restockMedicineForm.count,
    price: restockMedicineForm.price
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      refresh(currentPage.value)
      ElMessage.success(res.data.message)
    }
  })
}
const editForm = reactive(
    {
      id: "",
      count: "",
      price: ""
    }
)
const editRestock = () => {
  const token = useStorage('token', "")
  axios.post("/api/trade/editRestock", {
    id: editForm.id,
    count: editForm.count,
    price: editForm.price
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      refresh(currentPage.value)
      ElMessage.success(res.data.message)
    }
  })
}

const editDialogVisible = ref(false)
const deleteStock = () => {
  const token = useStorage('token', "")
  axios.post("/api/trade/deleteRestock", {
    id: deleteId.value
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      refresh(currentPage.value)
      ElMessage.success(res.data.message)
    }
  })
}
const deleteId = ref(0)
const dialogDeleteVisible = ref(false)
</script>

<style scoped>

</style>
