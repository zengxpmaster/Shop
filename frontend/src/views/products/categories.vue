<script setup>
import { ref, onMounted } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NSpace } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const showModal = ref(false)
const editingItem = ref(null)

const columns = [
    { title: 'ID', key: 'id', width: 80 },
    { title: '分类名称', key: 'name' },
    { title: '排序', key: 'sort' },
    {
        title: '创建时间', key: 'createdAt',
        render: (row) => row.createdAt ? new Date(row.createdAt * 1000).toLocaleString() : '-'
    },
    {
        title: '操作', key: 'actions',
        render: (row) => h(NSpace, {}, {
            default: () => [
                h(NButton, { size: 'small', type: 'primary', onClick: () => openEdit(row) }, { default: () => '编辑' }),
                h(NButton, { size: 'small', type: 'error', onClick: () => deleteItem(row) }, { default: () => '删除' })
            ]
        })
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/categories')
        tableData.value = res.data || []
    } catch(e) {
        tableData.value = []
    } finally {
        loading.value = false
    }
}

const openAdd = () => {
    editingItem.value = { name: '', sort: 0 }
    showModal.value = true
}

const openEdit = (row) => {
    editingItem.value = { ...row }
    showModal.value = true
}

const deleteItem = async (row) => {
    try {
        await httpPost(`/api/categories/${row.id}/delete`, {})
        notice.success('删除成功')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '删除失败')
    }
}

const handleSave = async () => {
    try {
        if (editingItem.value.id) {
            await httpPost(`/api/categories/${editingItem.value.id}`, editingItem.value)
        } else {
            await httpPost('/api/categories', editingItem.value)
        }
        notice.success('保存成功')
        showModal.value = false
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '保存失败')
    }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <n-card title="商品分类" style="margin-bottom:16px">
      <n-button type="primary" @click="openAdd">添加分类</n-button>
    </n-card>
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
    </n-card>
    <n-modal v-model:show="showModal" preset="dialog" :title="editingItem?.id ? '编辑分类' : '添加分类'" @positive-click="handleSave">
      <n-form v-if="editingItem" :model="editingItem" label-placement="left" label-width="80px" style="margin-top:16px">
        <n-form-item label="分类名称">
          <n-input v-model:value="editingItem.name" placeholder="请输入分类名称" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="editingItem.sort" :min="0" style="width:100%" />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>
