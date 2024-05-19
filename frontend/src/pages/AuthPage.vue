<script setup lang="ts">

</script>

<template>
  <mdui-layout full-height>
    <mdui-top-app-bar>
      <mdui-button-icon icon="medical_services--two-tone"></mdui-button-icon>
      <mdui-top-app-bar-title>
        药品管理系统
      </mdui-top-app-bar-title>
    </mdui-top-app-bar>
    <mdui-layout-main style="display: flex;justify-content: center;align-items: center">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </mdui-layout-main>
  </mdui-layout>
</template>

<style scoped>

</style>