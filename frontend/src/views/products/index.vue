<script setup>
import { ref, onMounted, reactive } from 'vue'
import { httpGet, httpPost } from '@/http'
import { notice } from '@/utils'
import { NButton, NTag, NSwitch, NSpace } from 'naive-ui'
import { h } from 'vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const showDrawer = ref(false)
const editingProduct = ref(null)

const search = reactive({ name: '', status: null, categoryId: null })
const pagination = reactive({ page: 1, pageSize: 10 })
const categories = ref([])

const columns = [
    { title: 'ID', key: 'id', width: 80 },
    { title: '商品名', key: 'name' },
    { title: '分类', key: 'categoryName' },
    { title: '价格', key: 'price', render: (row) => `¥${(row.price / 100).toFixed(2)}` },
    { title: '库存', key: 'stock' },
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
        render: (row) => h(NSpace, {}, {
            default: () => [
                h(NButton, { size: 'small', type: 'primary', onClick: () => openEdit(row) }, { default: () => '编辑' }),
                h(NButton, { size: 'small', type: 'error', onClick: () => deleteProduct(row) }, { default: () => '删除' })
            ]
        })
    }
]

const fetchData = async () => {
    loading.value = true
    try {
        const res = await httpGet('/api/products', {
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

const fetchCategories = async () => {
    try {
        const res = await httpGet('/api/categories')
        categories.value = (res.data || []).map(c => ({ label: c.name, value: c.id }))
    } catch(e) {}
}

const toggleStatus = async (row, val) => {
    try {
        await httpPost(`/api/products/${row.id}/status`, { status: val ? 1 : 0 })
        row.status = val ? 1 : 0
        notice.success('状态更新成功')
    } catch(e) {
        notice.error(e?.msg || '操作失败')
    }
}

const openEdit = (row) => {
    editingProduct.value = { ...row }
    showDrawer.value = true
}

const openAdd = () => {
    editingProduct.value = { name: '', price: 0, stock: 0, categoryId: null, status: 1 }
    showDrawer.value = true
}

const deleteProduct = async (row) => {
    try {
        await httpPost(`/api/products/${row.id}/delete`, {})
        notice.success('删除成功')
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '删除失败')
    }
}

const handleSave = async () => {
    try {
        if (editingProduct.value.id) {
            await httpPost(`/api/products/${editingProduct.value.id}`, editingProduct.value)
        } else {
            await httpPost('/api/products', editingProduct.value)
        }
        notice.success('保存成功')
        showDrawer.value = false
        fetchData()
    } catch(e) {
        notice.error(e?.msg || '保存失败')
    }
}

const handleSearch = () => { pagination.page = 1; fetchData() }

onMounted(() => { fetchData(); fetchCategories() })
</script>

<template>
  <div>
    <n-card title="商品管理" style="margin-bottom:16px">
      <n-space>
        <n-input v-model:value="search.name" placeholder="商品名称" style="width:180px" clearable />
        <n-select v-model:value="search.categoryId" :options="categories" placeholder="选择分类" style="width:150px" clearable />
        <n-select v-model:value="search.status" :options="[{label:'上架',value:1},{label:'下架',value:0}]" placeholder="状态" style="width:120px" clearable />
        <n-button type="primary" @click="handleSearch">搜索</n-button>
        <n-button @click="() => { search.name=''; search.categoryId=null; search.status=null; handleSearch() }">重置</n-button>
        <n-button type="success" @click="openAdd">添加商品</n-button>
      </n-space>
    </n-card>
    <n-card>
      <n-data-table :columns="columns" :data="tableData" :loading="loading" :bordered="false" />
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <n-pagination v-model:page="pagination.page" :page-size="pagination.pageSize" :item-count="total" @update:page="fetchData" />
      </div>
    </n-card>
    <n-drawer v-model:show="showDrawer" :width="480" placement="right">
      <n-drawer-content :title="editingProduct?.id ? '编辑商品' : '添加商品'" closable>
        <n-form v-if="editingProduct" :model="editingProduct" label-placement="left" label-width="80px">
          <n-form-item label="商品名">
            <n-input v-model:value="editingProduct.name" placeholder="请输入商品名称" />
          </n-form-item>
          <n-form-item label="分类">
            <n-select v-model:value="editingProduct.categoryId" :options="categories" placeholder="选择分类" />
          </n-form-item>
          <n-form-item label="价格(分)">
            <n-input-number v-model:value="editingProduct.price" :min="0" style="width:100%" />
          </n-form-item>
          <n-form-item label="库存">
            <n-input-number v-model:value="editingProduct.stock" :min="0" style="width:100%" />
          </n-form-item>
          <n-form-item label="状态">
            <n-switch v-model:value="editingProduct.status" :checked-value="1" :unchecked-value="0" />
          </n-form-item>
        </n-form>
        <template #footer>
          <n-space>
            <n-button @click="showDrawer = false">取消</n-button>
            <n-button type="primary" @click="handleSave">保存</n-button>
          </n-space>
        </template>
      </n-drawer-content>
    </n-drawer>
  </div>
</template>
