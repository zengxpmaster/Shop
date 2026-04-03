<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NTag, NSpace } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const search = reactive({ status: null })
const pagination = reactive({ page: 1, pageSize: 10 })

const orderStatusMap = { 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
const orderStatusTag = { 0: 'warning', 1: 'info', 2: 'default', 3: 'success', 4: 'error' }

const columns = [
    { title: '订单号', key: 'orderNo' },
    { title: '用户', key: 'username' },
    { title: '金额', key: 'amount', render: (row) => `¥${(row.amount / 100).toFixed(2)}` },
    {
        title: '状态', key: 'status',
        render: (row) => h(NTag, { type: orderStatusTag[row.status] ?? 'default', size: 'small' }, { default: () => orderStatusMap[row.status] ?? '未知' })
    },
    {
        title: '支付状态', key: 'payStatus',
        render: (row) => h(NTag, { type: row.payStatus === 1 ? 'success' : 'warning', size: 'small' }, { default: () => row.payStatus === 1 ? '已支付' : '未支付' })
    },
    {
        title: '创建时间', key: 'createdAt',
        render: (row) => row.createdAt ? new Date(row.createdAt * 1000).toLocaleString() : '-'
    },
    {
        title: '操作', key: 'actions',
        render: (row) => h(NSpace, {}, {
            default: () => [
                h(NButton, { size: 'small', onClick: () => viewOrder(row) }, { default: () => '详情' }),
                row.status === 1 && h(NButton, { size: 'small', type: 'primary', onClick: () => shipOrder(row) }, { default: () => '发货' }),
                row.status === 0 && h(NButton, { size: 'small', type: 'error', onClick: () => cancelOrder(row) }, { default: () => '取消' })
            ].filter(Boolean)
        })
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/orders', {
            ...search,
            page: pagination.page,
            pageSize: pagination.pageSize
        })
        tableData.value = res.data?.list || res.data || []
        total.value = res.data?.total || tableData.value.length
    } catch(e) {
        tableData.value = []
    } finally {
        loading.value = false
    }
}

const viewOrder = (row) => {}

const shipOrder = async (row) => {
    try {
        await httpPost(`/api/orders/${row.id}/ship`, {})
        notice.success('发货成功')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

const cancelOrder = async (row) => {
    try {
        await httpPost(`/api/orders/${row.id}/cancel`, {})
        notice.success('订单已取消')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

const handleSearch = () => { pagination.page = 1; fetchData() }

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="订单管理" style="margin-bottom:16px">
      <n-space>
        <n-select
          v-model:value="search.status"
          :options="[{label:'待支付',value:0},{label:'待发货',value:1},{label:'待收货',value:2},{label:'已完成',value:3},{label:'已取消',value:4}]"
          placeholder="订单状态" style="width:150px" clearable
        />
        <n-button type="primary" @click="handleSearch">搜索</n-button>
        <n-button @click="() => { search.status=null; handleSearch() }">重置</n-button>
      </n-space>
    </n-card>
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
  </div>
</template>
