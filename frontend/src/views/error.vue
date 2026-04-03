<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh">
    <n-result status="404" title="404" description="页面不存在">
      <template #footer>
        <n-button @click="router.push('/')">返回首页</n-button>
      </template>
    </n-result>
  </div>
</template>
