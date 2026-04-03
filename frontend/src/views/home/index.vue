<script setup>
import { h, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import authStore from '@/store/auth'
import configStore from '@/store/config'
import { NIcon } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const auth = authStore()
const config = configStore()

const collapsed = computed({
    get: () => config.isMenuCollapsed,
    set: (val) => config.setMenuCollapsed(val)
})

const activeKey = computed(() => route.path)

const renderIcon = (icon) => () => h(NIcon, null, { default: () => h('span', { class: 'material-icons' }, icon) })

const menuOptions = [
    {
        label: '数据概览',
        key: '/dashboard',
        icon: renderIcon('dashboard')
    },
    {
        label: '用户管理',
        key: '/users',
        icon: renderIcon('people')
    },
    {
        label: '商品管理',
        key: '/products',
        icon: renderIcon('shopping_cart'),
        children: [
            { label: '商品列表', key: '/products/list' },
            { label: '商品分类', key: '/products/categories' }
        ]
    },
    {
        label: '订单管理',
        key: '/orders',
        icon: renderIcon('list_alt')
    },
    {
        label: '分销管理',
        key: '/distribution',
        icon: renderIcon('share'),
        children: [
            { label: '分销商列表', key: '/distribution/list' },
            { label: '佣金记录', key: '/distribution/commissions' }
        ]
    },
    {
        label: '财务管理',
        key: '/finance',
        icon: renderIcon('account_balance_wallet'),
        children: [
            { label: '钱包管理', key: '/finance/wallets' },
            { label: '提现管理', key: '/finance/withdrawals' }
        ]
    },
    {
        label: '系统设置',
        key: '/settings',
        icon: renderIcon('settings')
    }
]

const handleMenuSelect = (key) => {
    router.push(key)
}

const handleLogout = () => {
    auth.logout()
    router.push('/login')
}
</script>

<template>
  <n-layout style="height:100vh" has-sider>
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="220"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div style="height:48px;display:flex;align-items:center;justify-content:center;font-weight:bold;font-size:16px;padding:0 16px;overflow:hidden;white-space:nowrap">
        {{ collapsed ? '后台' : '分销商城管理后台' }}
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>
    <n-layout>
      <n-layout-header bordered style="height:48px;display:flex;align-items:center;justify-content:flex-end;padding:0 24px">
        <n-space>
          <span style="margin-right:8px">{{ auth.nickname || auth.username }}</span>
          <n-button size="small" @click="handleLogout">退出登录</n-button>
        </n-space>
      </n-layout-header>
      <n-layout-content id="main-content" style="padding:24px;overflow-y:auto;height:calc(100vh - 48px)">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>
