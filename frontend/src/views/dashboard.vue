<script setup>
import { ref, onMounted } from 'vue'
import { httpGet } from '@/http'

const loading = ref(false)
const stats = ref({
    totalUsers: 0,
    totalOrders: 0,
    totalProducts: 0,
    totalDistributors: 0
})

const fetchStats = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/dashboard/stats')
        stats.value = { ...stats.value, ...res.data }
    } catch(e) {
        // use defaults
    } finally {
        loading.value = false
    }
}

onMounted(fetchStats)
</script>

<template>
  <div>
    <div style="margin-bottom:24px;font-size:20px;font-weight:bold">数据概览</div>
    <n-spin :show="loading">
      <n-grid :cols="4" :x-gap="16" :y-gap="16">
        <n-grid-item>
          <n-card>
            <n-statistic label="总用户数">
              <n-number-animation :from="0" :to="stats.totalUsers" />
            </n-statistic>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card>
            <n-statistic label="总订单数">
              <n-number-animation :from="0" :to="stats.totalOrders" />
            </n-statistic>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card>
            <n-statistic label="总商品数">
              <n-number-animation :from="0" :to="stats.totalProducts" />
            </n-statistic>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card>
            <n-statistic label="分销商数">
              <n-number-animation :from="0" :to="stats.totalDistributors" />
            </n-statistic>
          </n-card>
        </n-grid-item>
      </n-grid>
    </n-spin>
  </div>
</template>
