<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import {Remove} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

onMounted(() => {
  const token = useStorage('token', "")
  axios.get("/api/user/list", {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      tableData.value = res.data.users as User[]
    }
  })
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
      ElMessage.success("刷新成功")
    }
  })
}

const offline = (userToken: string) => {
  const token = useStorage('token', "")
  axios.post("/api/user/offline", {
    token: userToken
  }, {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    if (res.data.code === "200") {
      ElMessage.success("下线成功")
      refresh()
    }
  })
}

const tableData = ref<User[]>([])
const currentToken = ref("")
const dialogDeleteVisible = ref(false)
</script>

<template>
  <div style="display: flex;flex-direction: column;width: 100%">
    <el-dialog
        v-model="dialogDeleteVisible"
        align-center
        title="警告"
        width="500"
    >
      <span>确认下线此用户吗，此操作将无法恢复 !!!</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogDeleteVisible = false">取消</el-button>
          <el-button type="danger" @click="dialogDeleteVisible = false;offline(currentToken)">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
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
    <el-table :data="tableData" border stripe style="width: 100%" height="500">
      <el-table-column label="用户id" prop="id" width="100"/>
      <el-table-column label="用户名" prop="username" width="180"/>
      <el-table-column label="角色"  width="180">
        <el-tag type="success">管理员</el-tag>
      </el-table-column>
      <el-table-column label="用户邮箱" prop="email" width="180"/>
      <el-table-column label="用户token" prop="token"/>
      <el-table-column fixed="right" label="操作" width="100">
        <template v-slot="scope">
          <el-button :icon="Remove" type="danger" @click="currentToken=scope.row.token;dialogDeleteVisible=true">
            下线
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>