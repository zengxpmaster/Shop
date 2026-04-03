<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet } from '@/http'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pagination = reactive({ page: 1, pageSize: 10 })

const columns = [
    { title: '用户', key: 'username' },
    { title: '余额', key: 'balance', render: (row) => `¥${((row.balance || 0) / 100).toFixed(2)}` },
    { title: '冻结金额', key: 'frozenAmount', render: (row) => `¥${((row.frozenAmount || 0) / 100).toFixed(2)}` },
    { title: '累计收入', key: 'totalIncome', render: (row) => `¥${((row.totalIncome || 0) / 100).toFixed(2)}` },
    { title: '累计提现', key: 'totalWithdrawal', render: (row) => `¥${((row.totalWithdrawal || 0) / 100).toFixed(2)}` }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/finance/wallets', { page: pagination.page, pageSize: pagination.pageSize })
        tableData.value = res.data?.list || res.data || []
        total.value = res.data?.total || tableData.value.length
    } catch(e) {
        tableData.value = []
    } finally {
        loading.value = false
    }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="钱包管理" style="margin-bottom:16px" />
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>
