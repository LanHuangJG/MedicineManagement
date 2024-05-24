<script lang="ts" setup>
import {onMounted, reactive, ref, watch} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {ElMessage} from "element-plus";
import {Delete, Edit,Search} from "@element-plus/icons-vue";

onMounted(() => {
  const token =
      useStorage('token', "")
  getMedicineList(1, 10)
  // search()
  axios.get("/api/type/listBigTypeWithoutTypes", {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    bigTypes.value = res.data.bigTypes
  })
})

interface Type {
  id: string,
  name: string
}

interface BigType {
  id: string,
  name: string,
}

interface medicine {
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

const medicines = ref<medicine[]>([])
const totalPage = ref(1)
const currentPage = ref(1)
const bigTypes = ref<BigType[]>([])


const handleCurrentChange = (page: number) => {
  if (isSearchMode.value) {
    search(page)
  } else {
    getMedicineList(page)
  }
}

const refresh = () => {
  getMedicineList(currentPage.value)
  ElMessage({
    message: '刷新成功',
    type: 'success',
  })
}
const form = reactive({
  id: "",
  name: "",
  image: "",
  indications: "",
  mainIngredient: "",
  functionalIndications: "",
  dosage: "",
  approvalNumber: "",
  manufacturer: "",
  type: "",
  bigType: "",
  tid: "",
  bid: ""
})


const dialogFormVisible = ref(false)
const dialogDeleteVisible = ref(false)
const dialogAddVisible = ref(false)
const formLabelWidth = '140px'

const addMedicineForm = reactive({
  name: "",
  image: "",
  indications: "",
  mainIngredient: "",
  functionalIndications: "",
  dosage: "",
  approvalNumber: "",
  manufacturer: "",
  type: "",
  bigType: ""
})
const types = ref<Type[]>([])

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

const bigType = ref("")
const type = ref("")
watch(bigType, (value) => {
  loadTypes(value)
})
const updateMedicine = () => {
  const token =
      useStorage('token', "")
  axios.post("/api/medicine/updateMedicine", {
    id: form.id,
    name: form.name,
    image: form.image,
    indications: form.indications,
    mainIngredient: form.mainIngredient,
    functionalIndications: form.functionalIndications,
    dosage: form.dosage,
    approvalNumber: form.approvalNumber,
    manufacturer: form.manufacturer,
    type: type.value,
    bigType: bigType.value
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      getMedicineList(currentPage.value)
    }
  })
}

const addMedicine = () => {
  const token =
      useStorage('token', "")
  axios.post("/api/medicine/addMedicine", {
    name: addMedicineForm.name,
    image: addMedicineForm.image,
    indications: addMedicineForm.indications,
    mainIngredient: addMedicineForm.mainIngredient,
    functionalIndications: addMedicineForm.functionalIndications,
    dosage: addMedicineForm.dosage,
    approvalNumber: addMedicineForm.approvalNumber,
    manufacturer: addMedicineForm.manufacturer,
    type: type.value,
    bigType: bigType.value
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      getMedicineList(currentPage.value)
    }
  })
  ElMessage({
    message: '添加成功',
    type: 'success',
  })
}
const currentDeleteMedicineId = ref(0)

const deleteMedicine = () => {
  const token = useStorage('token', "")
  axios.post("/api/medicine/deleteMedicine", {
    id: currentDeleteMedicineId.value
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      getMedicineList(currentPage.value)
      ElMessage({
        message: '删除成功',
        type: 'success',
      });
    } else {
      ElMessage({
        message: res.data.message,
        type: 'error',
      });
    }
  })
}
const isSearchMode = ref(false)
const searchWord = ref('')
const search = (page: number = 1) => {
  const token = useStorage('token', "")
  isSearchMode.value = true
  axios.get(`/api/medicine/searchMedicine?keyword=${searchWord.value}&page=${page}&size=10`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      ElMessage({
        message: '搜索成功',
        type: 'success',
      });
      medicines.value = res.data.list.map((item: medicine) => {
        return {
          id: item.id,
          name: item.name,
          tid: item.tid,
          bid: item.bid,
          image: item.image,
          indications: item.indications,
          mainIngredient: item.mainIngredient.replace("                           ", ""),
          functionalIndications: item.functionalIndications.replace("                           ", ""),
          dosage: item.dosage,
          approvalNumber: item.approvalNumber,
          manufacturer: item.manufacturer,
          type: {
            id: item.type.id,
            name: item.type.name
          },
          bigType: {
            id: item.bigType.id,
            name: item.bigType.name
          }
        }
      })
      totalPage.value = Number(res.data.totalSize)
    }
  })
}

function getMedicineList(page: number, size: number = 10) {
  const token =
      useStorage('token', "")
  isSearchMode.value = false
  axios.get(`/api/medicine/list?page=${page}&size=${size}`, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    medicines.value = res.data.list.map((item: medicine) => {
      return {
        id: item.id,
        name: item.name,
        tid: item.tid,
        bid: item.bid,
        image: item.image,
        indications: item.indications,
        mainIngredient: item.mainIngredient.replace("                           ", ""),
        functionalIndications: item.functionalIndications.replace("                           ", ""),
        dosage: item.dosage,
        approvalNumber: item.approvalNumber,
        manufacturer: item.manufacturer,
        type: {
          id: item.type.id,
          name: item.type.name
        },
        bigType: {
          id: item.bigType.id,
          name: item.bigType.name
        }
      }
    })

    totalPage.value = Number(res.data.totalSize)
  })
}
</script>

<template>
  <div>
    <!-- 添加药品弹窗   -->

    <el-dialog v-model="dialogAddVisible" align-center title="添加药品" width="500">
      <el-form :model="addMedicineForm">
        <el-form-item :label-width="formLabelWidth" label="药品图片链接">
          <el-input v-model="addMedicineForm.image" autocomplete="off" clearable placeholder="请输入药品图片链接"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品名">
          <el-input v-model="addMedicineForm.name" autocomplete="off" clearable placeholder="请输入药品名"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品分类">
          <el-select
              v-model="bigType"
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
          <el-select
              v-model="type"
              :disabled="bigType===''"
              placeholder="请选择药品种类"
          >
            <el-option
                v-for="item in types.map(item => {
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
        <el-form-item :label-width="formLabelWidth" label="适应症">
          <el-input v-model="addMedicineForm.indications" autocomplete="off" clearable placeholder="请输入药品适应症"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="主要成分">
          <el-input
              v-model="addMedicineForm.mainIngredient"
              autocomplete="off" clearable
              placeholder="请输入药品主要成分"
          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="主要功效">
          <el-input
              v-model="addMedicineForm.functionalIndications"
              autocomplete="off" clearable
              placeholder="请输入药品主要功效"
          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="使用剂量">
          <el-input
              v-model="addMedicineForm.dosage"
              autocomplete="off" clearable
              placeholder="请输入药品使用剂量"
          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="生产公司">
          <el-input
              v-model="addMedicineForm.manufacturer"
              autocomplete="off" clearable
              placeholder="请输入药品生产公司"
          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="批准文号">
          <el-input
              v-model="addMedicineForm.approvalNumber"
              autocomplete="off" clearable
              placeholder="请输入药品批准文号"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogAddVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogAddVisible = false;addMedicine()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!--   药品信息编辑 -->

    <el-dialog v-model="dialogFormVisible" align-center title="药品信息编辑" width="500">
      <el-form :model="form">
        <el-form-item :label-width="formLabelWidth" label="药品图片链接">
          <el-input v-model="form.image" autocomplete="off" clearable placeholder="请输入药品图片链接"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品名">
          <el-input v-model="form.name" autocomplete="off" clearable placeholder="请输入药品名"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="药品分类">
          <el-select
              v-model="bigType"
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
          <el-select
              v-model="type"
              :disabled="bigType===''"
              placeholder="请选择药品种类"
          >
            <el-option
                v-for="item in types.map(item => {
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
        <el-form-item :label-width="formLabelWidth" label="适应症">
          <el-input v-model="form.indications" autocomplete="off" clearable placeholder="请输入药品适应症"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="主要成分">
          <el-input
              v-model="form.mainIngredient"
              autocomplete="off" clearable
              placeholder="请输入药品主要成分"
          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="主要功效">
          <el-input
              v-model="form.functionalIndications"
              autocomplete="off" clearable
              placeholder="请输入药品主要功效"

          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="使用剂量">
          <el-input
              v-model="form.dosage"
              autocomplete="off" clearable
              placeholder="请输入药品使用剂量"

          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="生产公司">
          <el-input
              v-model="form.manufacturer"
              autocomplete="off" clearable
              placeholder="请输入药品生产公司"

          />
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="批准文号">
          <el-input
              v-model="form.approvalNumber"
              autocomplete="off" clearable
              placeholder="请输入药品批准文号"

          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogFormVisible = false;ElMessage({
            message: '修改成功',
            type: 'success',
          });updateMedicine()">
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
          <el-button type="danger" @click="dialogDeleteVisible = false;deleteMedicine()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!--  药品管理表格操作    -->

    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='cyclone--two-tone'></mdui-icon> &nbsp;药品管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled"
                     @click="type='';bigType='';dialogAddVisible=true">添加药品
        </mdui-button>
        <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal"
                     @click="refresh()">
          刷新
        </mdui-button>
      </div>


      <!--  药品表格    -->


      <el-table :data="medicines" border height="550" stripe style="width: 100%">
        <el-table-column label="id" prop="id" width="60"/>
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
        <el-table-column label="主要功效" prop="functionalIndications" width="180">
          <template v-slot="scope">
            {{ scope.row.functionalIndications.replace("...[详情]", "").replace("[详情]", "") }}
          </template>
        </el-table-column>
        <el-table-column label="使用剂量" prop="dosage" width="180"/>
        <el-table-column label="生产公司" prop="manufacturer" width="150"/>
        <el-table-column label="批准文号" prop="approvalNumber" width="150"/>
        <el-table-column fixed="right" label="操作" width="180">
          <template v-slot="scope">
            <div style="width: 100%;display: flex;justify-content: center;align-items: center">
              <el-button :icon="Edit" type="success" @click="dialogFormVisible = true;form.name=scope.row.name;
              form.id=scope.row.id;
            form.image=scope.row.image;form.indications=scope.row.indications;
            form.mainIngredient=scope.row.mainIngredient;
            form.functionalIndications=scope.row.functionalIndications;
            form.dosage=scope.row.dosage;form.manufacturer=scope.row.manufacturer;
            form.approvalNumber=scope.row.approvalNumber;form.bigType=scope.row.bigType.name;form.type=scope.row.type.name;form.bid=scope.row.bigType.id;form.tid=scope.row.type.id;
bigType=scope.row.bigType.name;type=scope.row.type.name">
                编辑
              </el-button>
              <el-button :icon="Delete" type="danger"
                         @click="dialogDeleteVisible = true;currentDeleteMedicineId=scope.row.id">删除
              </el-button>
            </div>
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
        <div>
          <el-input v-model="searchWord" placeholder="请输入您要搜索的药品"
                    style="width: 240px;height: 36px;margin: 16px">
            <template #prefix>
              <el-icon class="el-input__icon"><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="success" plain @click="search()">搜索</el-button>
          <el-button type="primary" plain @click="currentPage=1;refresh()">重置</el-button>
        </div>
        <div style="flex: 1"/>


        <!--  分页组件    -->


        <el-pagination v-model:current-page="currentPage" :page-count="totalPage" background layout="prev, pager, next"
                       style="margin: 16px"
                       @current-change="handleCurrentChange"/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>