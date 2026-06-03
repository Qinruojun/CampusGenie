<!--分类卡片组件-->
<template>
  <div class="category-select">
    <select
        class="category-input"
        :value="modelValue"
        :disabled="disabled || loading"
        @change="handleChange"
    >
      <option value="">
        {{ loading ? '分类加载中...' : placeholder }}
      </option>

      <option
          v-for="item in categoryList"
          :key="item.id"
          :value="item.id"
      >
        {{ item.name }}
      </option>
    </select>

    <p v-if="errorMessage" class="category-error">
      {{ errorMessage }}
    </p>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getCategoryList } from '@/api/category'
import {SUCCESS} from "@/constants/code.js";

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  placeholder: {
    type: String,
    default: '请选择分类'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'loaded'])

const categoryList = ref([])
const loading = ref(false)
const errorMessage = ref('')

async function loadCategoryList() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getCategoryList()
    const result = response

    if (result?.code !== SUCCESS) {
      throw new Error(result?.msg || '分类加载失败')
    }

    categoryList.value = result.data || []

    emit('loaded', categoryList.value)// 触发 loaded 事件
  } catch (error) {
    console.error(error)
    errorMessage.value = '分类加载失败'
  } finally {
    loading.value = false
  }
}

function handleChange(event) {
  const value = event.target.value

  const finalValue = value === '' ? '' : Number(value)

  const selectedCategory = categoryList.value.find((item) => {
    return Number(item.id) === Number(finalValue)
  })

  emit('update:modelValue', finalValue)
  emit('change', selectedCategory || null)
}

onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.category-select {
  width: 100%;
}

.category-input {
  width: 100%;
  height: 44px;
  padding: 0 14px;
  border: 1px solid #d8d6cf;
  border-radius: 10px;
  background: #ffffff;
  color: #1c241c;
  font-size: 14px;
  outline: none;
}

.category-input:focus {
  border-color: #219c55;
  box-shadow: 0 0 0 3px rgba(33, 156, 85, 0.12);
}

.category-input:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}

.category-error {
  margin: 8px 0 0;
  color: #b94848;
  font-size: 13px;
}
</style>