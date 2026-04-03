<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NTag, NSpace } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pagination = reactive({ page: 1, pageSize: 10 })

const statusMap = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
const statusTag = { 0: 'warning', 1: 'success', 2: 'error' }

const columns = [
    { title: '提现编号', key: 'no' },
    { title: '用户', key: 'username' },
    { title: '金额', key: 'amount', render: (row) => `¥${((row.amount || 0) / 100).toFixed(2)}` },
    { title: '类型', key: 'type', render: (row) => row.type === 1 ? '银行卡' : '微信' },
    {
        title: '状态', key: 'status',
        render: (row) => h(NTag, { type: statusTag[row.status] ?? 'default', size: 'small' }, { default: () => statusMap[row.status] ?? '未知' })
    },
    {
        title: '创建时间', key: 'createdAt',
        render: (row) => row.createdAt ? new Date(row.createdAt * 1000).toLocaleString() : '-'
    },
    {
        title: '操作', key: 'actions',
        render: (row) => row.status === 0 ? h(NSpace, {}, {
            default: () => [
                h(NButton, { size: 'small', type: 'primary', onClick: () => approve(row) }, { default: () => '通过' }),
                h(NButton, { size: 'small', type: 'error', onClick: () => reject(row) }, { default: () => '拒绝' })
            ]
        }) : null
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/finance/withdrawals', { page: pagination.page, pageSize: pagination.pageSize })
        tableData.value = res.data?.list || res.data || []
        total.value = res.data?.total || tableData.value.length
    } catch(e) {
        tableData.value = []
    } finally {
        loading.value = false
    }
}

const approve = async (row) => {
    try {
        await httpPost(`/api/finance/withdrawals/${row.id}/approve`, {})
        notice.success('已通过')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

const reject = async (row) => {
    try {
        await httpPost(`/api/finance/withdrawals/${row.id}/reject`, {})
        notice.success('已拒绝')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="提现管理" style="margin-bottom:16px" />
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>
