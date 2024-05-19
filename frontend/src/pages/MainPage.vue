<script lang="ts" setup>
import {useStorage} from "@vueuse/core";
import {computed, onMounted, ref} from "vue";
import axios from "axios";
import {useRoute, useRouter} from "vue-router";
import {ElNotification} from "element-plus";
import {dialog} from "mdui";

const token =
    useStorage('token', "")
const username =
    useStorage('username', "admin")
const email = ref("")
const route = useRoute()
const router = useRouter()
onMounted(() => {
  if (token.value === "") {
    router.replace('/login')
  } else {
    axios.get("/api/auth/info", {
      headers: {
        "Authorization": token.value
      }
    }).then(res => {
      if (res.data.code !== "200") {
        token.value = ""
        router.replace('/login')
        ElNotification({
          title: '提示',
          message: "登录已过期,请重新登录",
          type: 'error',
        })
      } else {
        username.value = res.data.username
        email.value = res.data.email
      }
    })
  }
})
const key = computed(() => {
  return route.path + Math.random();
});

function logout() {
  axios.get("/api/auth/logout", {
    headers: {
      "Authorization": token.value
    }
  }).then(res => {
    ElNotification({
      title: '提示',
      message: res.data.message,
      type: 'success',
    })
    token.value = ""
    router.replace('/login')
  })
}

const homeIcon = computed(() => {
  return route.path === "/" ? "home--two-tone" : "home--outlined"
})
const medicineManageIcon = computed(() => {
  return route.path === "/medicineManage" ? "cyclone--two-tone" : "cyclone--outlined"
})
const restockIcon = computed(() => {
  return route.path === "/restock" ? "catching_pokemon--two-tone" : "catching_pokemon--outlined"
})
const saleIcon = computed(() => {
  return route.path === "/sale" ? "all_out--two-tone" : "all_out--outlined"
})
const bigTypeIcon = computed(() => {
  return route.path === "/bigType" ? "dashboard--two-tone" : "dashboard--outlined"
})
const typeIcon = computed(() => {
  return route.path === "/type" ? "dataset--two-tone" : "dataset--outlined"
})

function showUserInfoDialog() {
  dialog({
    headline: "用户信息",
    body: "<div style='display:flex;align-items:center;justify-content:center;flex-direction: column;gap: 16px'><mdui-avatar>" + username.value[0].toUpperCase() + "</mdui-avatar><span style='font-weight: bold;font-size: 16px'>用户名: " + username.value + "</span><span style='font-weight: bold;font-size: 16px'>邮箱: " + email.value + "</span><span style='font-weight: bold;font-size: 16px'>角色: 管理员</span></div>",
    actions: [
      {
        text: "确定",
      }
    ]
  });
}

</script>

<template>
  <mdui-layout full-height>
    <mdui-top-app-bar>
      <mdui-button-icon icon="medical_services--two-tone"></mdui-button-icon>
      <mdui-top-app-bar-title>
        药品管理系统
      </mdui-top-app-bar-title>
      <div style="flex-grow: 1"></div>
      <mdui-dropdown>
        <mdui-avatar slot="trigger" style="flex-shrink: 0">{{ username[0].toUpperCase() }}</mdui-avatar>
        <mdui-menu>
          <mdui-menu-item @click="showUserInfoDialog">个人信息
            <mdui-icon slot="icon" name="people"></mdui-icon>
          </mdui-menu-item>
          <mdui-menu-item @click="logout">退出登录
            <mdui-icon slot="icon" name="logout"></mdui-icon>
          </mdui-menu-item>
        </mdui-menu>
      </mdui-dropdown>
    </mdui-top-app-bar>
    <mdui-navigation-rail :value="route.path">
      <mdui-navigation-rail-item :icon="homeIcon" value="/" @click="router.replace('/')">主页
      </mdui-navigation-rail-item>
      <mdui-navigation-rail-item :icon="medicineManageIcon" value="/medicineManage"
                                 @click="router.replace('/medicineManage')">
        药品管理
      </mdui-navigation-rail-item>
      <mdui-navigation-rail-item :icon="bigTypeIcon" value="/bigType" @click="router.replace('/bigType')">
        分类管理
      </mdui-navigation-rail-item>
      <mdui-navigation-rail-item :icon="typeIcon" value="/type" @click="router.replace('/type')">
        种类管理
      </mdui-navigation-rail-item>
      <mdui-navigation-rail-item :icon="restockIcon" value="/restock" @click="router.replace('/restock')">
        进货管理
      </mdui-navigation-rail-item>
      <mdui-navigation-rail-item :icon="saleIcon" value="/sale" @click="router.replace('/sale')">
        销售记录
      </mdui-navigation-rail-item>
    </mdui-navigation-rail>
    <mdui-layout-main>
      <router-view :key="key" v-slot="{ Component }">
        <transition mode="out-in" name="fade">
          <component :is="Component"/>
        </transition>
      </router-view>
    </mdui-layout-main>
  </mdui-layout>
</template>

<style scoped>

</style>