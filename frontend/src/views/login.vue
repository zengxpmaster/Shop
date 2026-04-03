<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import authStore from '@/store/auth'
import { notice } from '@/utils'

const router = useRouter()
const auth = authStore()

const loading = ref(false)
const formRef = ref(null)

const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: { required: true, message: '请输入用户名', trigger: 'blur' },
    password: { required: true, message: '请输入密码', trigger: 'blur' }
}

const handleLogin = async () => {
    try {
        await formRef.value?.validate()
    } catch {
        return
    }
    loading.value = true
    try {
        await auth.login(form)
        router.push('/')
    } catch(e) {
        notice.error(e?.msg || e?.message || '登录失败')
    } finally {
        loading.value = false
    }
}
</script>

<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f0f2f5">
    <n-card style="width:400px" title="分销商城管理后台">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80px">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" @keyup.enter="handleLogin" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" block :loading="loading" @click="handleLogin">登录</n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>
