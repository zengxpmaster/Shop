<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NTag, NSwitch } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const search = reactive({ username: '', phone: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

const userTypeMap = { 0: '管理员', 1: '普通用户', 2: '分销商' }
const userTypeTagMap = { 0: 'error', 1: 'default', 2: 'success' }

const columns = [
    { title: 'ID', key: 'id', width: 80 },
    { title: '用户名', key: 'username' },
    { title: '手机', key: 'phone' },
    {
        title: '用户类型', key: 'type',
        render: (row) => h(NTag, { type: userTypeTagMap[row.type] ?? 'default', size: 'small' }, { default: () => userTypeMap[row.type] ?? '未知' })
    },
    {
        title: '状态', key: 'status',
        render: (row) => h(NSwitch, {
            value: row.status === 1,
            onUpdateValue: (val) => toggleStatus(row, val)
        })
    },
    {
        title: '创建时间', key: 'createdAt',
        render: (row) => row.createdAt ? new Date(row.createdAt * 1000).toLocaleString() : '-'
    },
    {
        title: '操作', key: 'actions',
        render: (row) => h(NButton, { size: 'small', type: 'primary', onClick: () => viewUser(row) }, { default: () => '详情' })
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/users', {
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

const toggleStatus = async (row, val) => {
    try {
        await httpPost(`/api/users/${row.id}/status`, { status: val ? 1 : 0 })
        row.status = val ? 1 : 0
        notice.success('状态更新成功')
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

const viewUser = (row) => {}

const handleSearch = () => {
    pagination.page = 1
    fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="用户管理" style="margin-bottom:16px">
      <n-space>
        <n-input v-model:value="search.username" placeholder="用户名" style="width:180px" clearable />
        <n-input v-model:value="search.phone" placeholder="手机号" style="width:180px" clearable />
        <n-button type="primary" @click="handleSearch">搜索</n-button>
        <n-button @click="() => { search.username=''; search.phone=''; handleSearch() }">重置</n-button>
      </n-space>
    </n-card>
    <n-card>
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :bordered="false"
      />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="total"
          @update:page="fetchData"
        />
      </div>
    </n-card>
  </div>
</template>
