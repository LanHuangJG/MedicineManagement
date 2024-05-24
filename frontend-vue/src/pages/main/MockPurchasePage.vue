<script lang="ts" setup>

</script>

<template>
  <div style="display: flex;flex-direction: column;width: 100%">
    <div style="display: flex;width: 100%">
      <div class="mdui-prose">
        <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
          <mdui-icon name='spa--two-tone'></mdui-icon> &nbsp;模拟购买
        </h2>
      </div>
      <div style="flex: 1"/>
      <mdui-button icon="add_shopping_cart--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled"
                   @click="">购买药品
      </mdui-button>
      <mdui-button icon="refresh--two-tone" style="margin-right: 8px;margin-bottom: 16px" variant="tonal">刷新</mdui-button>
    </div>
  </div>
</template>

<style scoped>

</style>