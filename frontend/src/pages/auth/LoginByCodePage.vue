<script lang="ts" setup>
import {useRouter} from "vue-router";
import {reactive, ref} from "vue";
import axios from "axios";
import {useStorage} from "@vueuse/core";
import {ElNotification} from "element-plus";
import VueCountdown from "@chenfengyuan/vue-countdown"

const form = reactive(
    {
      username: '',
      code: ''
    }
)

function login() {
  axios.post("/api/auth/loginByCode", {
    username: form.username,
    code: form.code
  }).then(res => {
    if (res.data.code === "200") {
      const token =
          useStorage('token', "")
      token.value = res.data.token
      router.replace('/')
      ElNotification({
        title: '提示',
        message: res.data.message,
        type: 'success',
      })
    } else {
      ElNotification({
        title: '提示',
        message: res.data.message,
        type: 'error',
      })
    }
  })
}

const counting = ref(false)
const onCountdownEnd = () => {
  counting.value = false
}

function sendEmail() {
  if (form.username === "") {
    ElNotification({
      title: '提示',
      message: '请填写用户名',
      type: 'warning',
    })
  } else {
    axios.post("/api/auth/sendEmail", {
      to: form.username,
      type: "login"
    }).then(res => {
      if (res.data.code === "200") {
        ElNotification({
          title: '提示',
          message: res.data.message,
          type: 'success',
        })
        counting.value = true
      } else {
        ElNotification({
          title: '提示',
          message: res.data.message,
          type: 'error',
        })
      }
    })
  }
}

const router = useRouter()
</script>

<template>
  <mdui-card style="padding: 16px;width: 48%" variant="outlined">
    <div class="mdui-prose" style="justify-content: center;align-items: center;display: flex">
      <h1>登录</h1>
    </div>
    <div style="margin: 16px"/>
    <mdui-text-field v-model="form.username" clearable icon="account_circle" label="用户名" variant="outlined">
    </mdui-text-field>
    <div style="margin: 16px"/>
    <div style="display: flex;justify-content: center;align-items: center">
      <mdui-text-field v-model="form.code" clearable icon="code" label="验证码" style="flex: 1" variant="outlined">
      </mdui-text-field>
      <div style="margin: 16px"/>
      <mdui-button :disabled="counting" full-width @click="sendEmail">
        <vue-countdown v-if="counting" v-slot="{ totalSeconds }" :time="60000" @end="onCountdownEnd">请在{{
            totalSeconds
          }}s 后重试
        </vue-countdown>
        <span v-else>获取验证码</span>
      </mdui-button>
    </div>
    <div style="margin: 32px"/>

    <mdui-button full-width icon="login" @click="login">登录
    </mdui-button>
    <div style="margin: 16px"/>
    <mdui-button full-width icon="account_circle" variant="elevated" @click="router.replace('/login')">使用用户名登录
    </mdui-button>
    <div style="margin: 16px"/>
    <mdui-button full-width variant="text" @click="router.push('/register')">还没有账号?</mdui-button>
  </mdui-card>
</template>

<style scoped>

</style>