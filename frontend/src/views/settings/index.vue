<script setup>
import { ref } from 'vue'
import configStore from '@/store/config'

const config = configStore()
</script>

<template>
  <div>
    <n-card title="系统设置">
      <n-form label-placement="left" label-width="100px">
        <n-form-item label="站点名称">
          <n-input v-model:value="config.siteName" placeholder="请输入站点名称" style="width:300px" />
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>
