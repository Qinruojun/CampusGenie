
<template>
  <div class="category-select">
    <select
      class="category-input"
      :value="currentValue"
      :disabled="disabled || loading"
      @change="handleChange"
    >
<!--      下拉框默认提示选项-->
      <option value="">
        {{ loading ? '分类加载中...' : placeholder }}
      </option>
      <option
        v-for="item in options"
        :key="String(item[valueKey])"
        :value="String(item[valueKey])"
      >
        {{ item[labelKey]}}
      </option>
    </select>
    <p v-if="errorMessage" class="category-error">
      {{ errorMessage }}
    </p>
  </div>
</template>
<script setup>
import {computed } from 'vue'
//构造template里面用到的参数表
//父组件给子组件传递的参数

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  options: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择分类'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ''
  },
  valueKey: {
    type: String,
    default: 'id'
  },
  labelKey: {
    type: String,
    default: 'name'
  }
})
//定义这个组件能触发的事件,向父组件触发
const emit = defineEmits(['update:modelValue', 'change'])
//computed 是计算属性
const currentValue = computed(()=>{
  if(props.modelValue===null|| props.modelValue === undefined){
    return ''
  }
  return String(props.modelValue)
})
function handleChange(event) {
  const value = event.target.value

  if (value === '') {
    emit('update:modelValue', '')
    emit('change', null)
    return
  }

  const selectedCategory = props.options.find((item) => {
    return String(item[props.valueKey]) === value
  })
const finalValue = selectedCategory
    ? selectedCategory[props.valueKey]
    : value

emit('update:modelValue', finalValue)
emit('change', selectedCategory || null)
}
</script>

<style scoped>
.category-select {
  width: 100%;
  min-width: 0;
  position: relative;
}

.category-input {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #dfe3e8;
  border-radius: 8px;
  background: #ffffff;
  color: #374151;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  cursor: pointer;
}

.category-input:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
}

.category-input:disabled {
  cursor: not-allowed;
  opacity: 0.65;
  background: #f9fafb;
}

.category-error {
  position: absolute;
  left: 0;
  top: 48px;
  margin: 0;
  color: #b94848;
  font-size: 12px;
  line-height: 1.4;
}
</style>
