<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Delete, Edit} from "@element-plus/icons-vue";

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
})
const handleCurrentChange = (page: number) => {
  getBigTypeList(page)
}
const refresh = () => {
  getBigTypeList(currentPage.value)

}
</script>

<template>
  <div>
    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='dashboard--two-tone'></mdui-icon> &nbsp;分类管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="add" style="margin-right: 16px;margin-bottom: 16px" variant="filled">添加分类</mdui-button>
        <mdui-button icon="refresh" style="margin-right: 8px;margin-bottom: 16px" variant="tonal" @click="refresh">
          刷新
        </mdui-button>
      </div>
    </div>
    <el-table :data="bigTypes" border stripe style="width: 100%">
      <el-table-column label="分类id" prop="id" width="120"/>
      <el-table-column label="药品分类" prop="name" width="120"/>
      <el-table-column label="药品种类" prop="types">
        <template v-slot="scope">
          <el-tag v-for="tag in scope.row.types" :key="tag.id" style="margin: 4px" type="warning">{{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="200">
        <div style="width: 100%;display: flex;justify-content: center;align-items: center">
          <el-button :icon="Edit" type="success">
            编辑
          </el-button>
          <el-button :icon="Delete" type="danger">删除</el-button>
        </div>
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
      <el-pagination v-model:current-page="currentPage" :page-count="totalPage" background layout="prev, pager, next"
                     style="margin: 16px"
                     @current-change="handleCurrentChange"/>
    </div>
  </div>
</template>

<style scoped>

</style>