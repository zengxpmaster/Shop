<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NTag, NSwitch } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pagination = reactive({ page: 1, pageSize: 10 })

const columns = [
    { title: 'ID', key: 'id', width: 80 },
    { title: '用户', key: 'username' },
    { title: '邀请码', key: 'inviteCode' },
    { title: '等级', key: 'level', render: (row) => h(NTag, { size: 'small' }, { default: () => `Lv${row.level ?? 1}` }) },
    { title: '佣金总额', key: 'totalCommission', render: (row) => `¥${((row.totalCommission || 0) / 100).toFixed(2)}` },
    { title: '可提现', key: 'availableAmount', render: (row) => `¥${((row.availableAmount || 0) / 100).toFixed(2)}` },
    {
        title: '状态', key: 'status',
        render: (row) => h(NSwitch, {
            value: row.status === 1,
            onUpdateValue: (val) => toggleStatus(row, val)
        })
    },
    {
        title: '操作', key: 'actions',
        render: (row) => h(NButton, { size: 'small', onClick: () => {} }, { default: () => '详情' })
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/distributors', { page: pagination.page, pageSize: pagination.pageSize })
        tableData.value = res.data?.list || res.data || []
        total.value = res.data?.total || tableData.value.length
    } catch(e) {
        tableData.value = []
    } finally {
        loading.value = false
    }
}

const toggleStatus = async (row, val) => {
    try {
        await httpPost(`/api/distributors/${row.id}/status`, { status: val ? 1 : 0 })
        row.status = val ? 1 : 0
        notice.success('状态更新成功')
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="分销商列表" style="margin-bottom:16px" />
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>
