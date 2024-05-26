<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Remove} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

onMounted(() => {
  refresh()
})

interface User {
  id: number
  username: string
  email: string
  token: string
}

const refresh = () => {
  const token = useStorage('token', "")
  axios.get("/api/user/list", {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      tableData.value = res.data.users as User[]
      console.log(tableData.value)
      ElMessage.success("刷新成功")
    }
  })
}
const tableData = ref<User[]>([])
</script>

<template>
  <div style="display: flex;flex-direction: column;width: 100%">
    <div style="display: flex;width: 100%">
      <div class="mdui-prose">
        <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
          <mdui-icon name='people--two-tone'></mdui-icon> &nbsp;在线用户管理
        </h2>
      </div>
      <div style="flex: 1"/>
      <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal"
                   @click="refresh()">刷新
      </mdui-button>
    </div>
    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column label="用户id" prop="id" width="180"/>
      <el-table-column label="用户名" prop="username" width="180"/>
      <el-table-column label="用户邮箱" prop="email" width="180"/>
      <el-table-column label="用户token" prop="token"/>
      <el-table-column fixed="right" label="操作" width="100">
        <template v-slot="scope">
          <el-button :icon="Remove" type="danger">
            下线
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>