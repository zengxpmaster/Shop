<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet } from '@/http'
import { NTag } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pagination = reactive({ page: 1, pageSize: 10 })

const statusMap = { 0: '待结算', 1: '已结算', 2: '已取消' }
const statusTag = { 0: 'warning', 1: 'success', 2: 'error' }

const columns = [
    { title: '佣金编号', key: 'no' },
    { title: '分销商', key: 'distributorName' },
    { title: '关联订单', key: 'orderNo' },
    { title: '金额', key: 'amount', render: (row) => `¥${((row.amount || 0) / 100).toFixed(2)}` },
    {
        title: '状态', key: 'status',
        render: (row) => h(NTag, { type: statusTag[row.status] ?? 'default', size: 'small' }, { default: () => statusMap[row.status] ?? '未知' })
    },
    {
        title: '创建时间', key: 'createdAt',
        render: (row) => row.createdAt ? new Date(row.createdAt * 1000).toLocaleString() : '-'
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/distribution/commissions', { page: pagination.page, pageSize: pagination.pageSize })
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
    <n-card title="佣金记录" style="margin-bottom:16px" />
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>
