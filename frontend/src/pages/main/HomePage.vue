<template>
  <div>
    <mdui-card style="width: 100%;display: flex;margin-bottom: 16px;flex-direction: column">
      <div class="mdui-prose" style="margin: 16px;width: 100%">
        <div style="display: flex">
          <h2>{{ getGreeting() }}&nbsp;&nbsp;&nbsp;</h2>
          <h2 style="font-weight: bold">{{ username }}</h2>
        </div>
        <h1>
          欢迎您使用药品管理系统
        </h1>
      </div>
    </mdui-card>
    <mdui-card style="width: 100%;display: flex" variant="outlined">
      <mdui-card clickable style="height: 124px;flex: 1;margin: 16px" variant="elevated" @click="router.replace('/medicineManage')">
        <mdui-icon name='medical_services--two-tone' style="margin: 16px"></mdui-icon>
        <div style="display: flex;justify-items: center;align-items: center;justify-content: space-between">
          <div class="mdui-prose" style="margin-left: 16px">
            <h2>药品数量</h2>
          </div>
          <count-up :end-val="medicineCount" style="margin-right: 16px;font-size: large"></count-up>
        </div>
      </mdui-card>
      <mdui-card clickable style="height: 124px;flex: 1;margin: 16px" variant="elevated"  @click="router.replace('/bigType')">
        <mdui-icon name='medication_liquid--two-tone' style="margin: 16px"></mdui-icon>
        <div style="display: flex;justify-items: center;align-items: center;justify-content: space-between">
          <div class="mdui-prose" style="margin-left: 16px">
            <h2>药品分类</h2>
          </div>
          <count-up :end-val="bigTypeCount" style="margin-right: 16px;font-size: large"></count-up>
        </div>
      </mdui-card>
      <mdui-card clickable style="height: 124px;flex: 1;margin: 16px" variant="elevated" @click="router.replace('/type')">
        <mdui-icon name='animation--two-tone' style="margin: 16px"></mdui-icon>
        <div style="display: flex;justify-items: center;align-items: center;justify-content: space-between">
          <div class="mdui-prose" style="margin-left: 16px">
            <h2>药品种类</h2>
          </div>
          <count-up :end-val="typeCount" style="margin-right: 16px;font-size: large"></count-up>
        </div>
      </mdui-card>
    </mdui-card>
    <div class="divider" style="margin-top: 16px">
      <svg aria-hidden="true" data-v-6a29f87a="" fill="none" height="8" width="100%" xmlns="http://www.w3.org/2000/svg">
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
    <div style="display: flex;width: 100%;margin-top: 32px;justify-content: space-between">
      <v-chart :init-options="initOption" :option="bigType" autoresize class="chart"/>
      <v-chart :init-options="initOption" :option="type" autoresize class="chart"/>
    </div>
  </div>
</template>
<script lang="ts" setup>
import CountUp from 'vue-countup-v3'
import {onMounted, ref} from "vue";
import {useStorage} from "@vueuse/core";
import axios from "axios";
import VChart from 'vue-echarts';
import {useRouter} from "vue-router";
import "echarts";
const router = useRouter()
const username  =
    useStorage('username', "")
const medicineCount = ref(0)
const bigTypeCount = ref(0)
const typeCount = ref(0)

function getGreeting(): string {
  const now = new Date();
  const hours = now.getHours();

  if (hours < 6) {
    return "凌晨好";
  } else if (hours < 12) {
    return "早上好";
  } else if (hours < 14) {
    return "中午好";
  } else if (hours < 17) {
    return "下午好";
  } else if (hours < 19) {
    return "傍晚好";
  } else {
    return "晚上好";
  }
}

onMounted(() => {
  const token =
      useStorage('token', "")
  axios.get("/api/medicine/basicInfo", {
    headers: {
      Authorization: token.value
    }
  }).then(res => {
    medicineCount.value = res.data.medicineCount
    bigTypeCount.value = res.data.bigTypeCount
    typeCount.value = res.data.typeCount
    bigType.value.series[0].data = res.data.bigTypes.map((item: { name: string; count: number }) => {
      return {
        value: item.count,
        name: item.name
      }
    })
    type.value.series[0].data = res.data.types.filter((item: { count: number }) => item.count !== 0)
        .map((item: { name: string; count: number }) => {
          return {
            value: item.count,
            name: item.name
          }
        })
  })
})


const initOption = {
  width: 700,
  height: 300
}
const bigType = ref({
  title: {
    text: '药品分类',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '药品种类数',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
});
const type = ref({
  title: {
    text: '药品种类',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'right'
  },
  series: [
    {
      name: '药品数',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
});
</script>
<style scoped>

</style>