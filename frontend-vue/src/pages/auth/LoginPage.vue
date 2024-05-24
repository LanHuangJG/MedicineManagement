<script lang="ts" setup>
import {useRouter} from "vue-router";
import {reactive} from "vue";
import axios from "axios";
import {useStorage} from "@vueuse/core";
import {ElNotification} from "element-plus";
const form = reactive(
    {
      username: '',
      password: ''
    }
)
function login(){
  axios.post("/api/auth/login",{
    username: form.username,
    password: form.password
  }).then(res => {
    if (res.data.code ==="200"){
      const token =
          useStorage('token', "")
      token.value = res.data.token
      router.replace('/')
      ElNotification({
        title: '提示',
        message: res.data.message,
        type: 'success',
      })
    }else{
      ElNotification({
        title: '提示',
        message: res.data.message,
        type: 'error',
      })
    }

  })
}
const router = useRouter()

</script>

<template>
  <mdui-card style="padding: 16px;width: 48%" variant="outlined">
    <div class="mdui-prose" style="justify-content: center;align-items: center;display: flex">
      <h1>登录</h1>
    </div>
    <div style="margin: 16px"/>
    <mdui-text-field clearable label="用户名" variant="outlined" v-model="form.username" icon="account_circle">
    </mdui-text-field>
    <div style="margin: 16px"/>
    <mdui-text-field clearable label="密码" variant="outlined" v-model="form.password" type="password" icon="password">
    </mdui-text-field>
    <div style="margin: 32px"/>

    <mdui-button full-width @click="login" icon="login">登录
    </mdui-button>
    <div style="margin: 16px"/>
    <mdui-button variant="elevated" full-width @click="router.replace('/loginByEmail')" icon="code">使用验证码登录
      </mdui-button>
    <div style="margin: 16px"/>
    <mdui-button full-width variant="text" @click="router.push('/register')">还没有账号?</mdui-button>
  </mdui-card>
</template>

<style scoped>

</style>