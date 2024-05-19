<template>
  <div style="height: 100%">
    <div style="display: flex;flex-direction: column;width: 100%">
      <div style="display: flex;width: 100%">
        <div class="mdui-prose">
          <h2 style="font-weight: bold;font-family: 楷体,sans-serif;display: flex;justify-content: center;align-items: center">
            <mdui-icon name='catching_pokemon--two-tone'></mdui-icon> &nbsp;进货管理
          </h2>
        </div>
        <div style="flex: 1"/>
        <mdui-button icon="input--two-tone" style="margin-right: 16px;margin-bottom: 16px" variant="filled">进货
        </mdui-button>
        <mdui-button icon="refresh" style="margin-right: 8px;margin-bottom: 16px" variant="tonal">刷新</mdui-button>
      </div>
    </div>
    <div style="display: flex;justify-content: center;align-items: center;width: 100%">
      <el-empty description="当前没有已进货药品"/>
    </div>
  </div>

</template>

<script setup>


const initOption = {
  width: 400,
  height: 400
}
const option = {
  title: {
    text: '药品种类',
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
      name: 'Access From',
      type: 'pie',
      radius: '50%',
      data: [
        {value: 1048, name: 'Search Engine'},
        {value: 735, name: 'Direct'},
        {value: 580, name: 'Email'},
        {value: 484, name: 'Union Ads'},
        {value: 300, name: 'Video Ads'}
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
};
</script>

<style scoped>
.chart {
  height: 100vh;
}
</style>
