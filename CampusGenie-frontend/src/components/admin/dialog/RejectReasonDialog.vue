<template>
  <div v-if="visible" class="dialog-mask" @click.self="handleClose">
    <div class="reason-dialog">
      <div class="dialog-header">
        <h3>{{ title }}</h3>
        <button class="dialog-close" type="button" @click="handleClose">
          ×
        </button>
      </div>

      <div class="dialog-body">
        <p v-if="tip" class="dialog-tip">
          {{ tip }}
        </p>

        <textarea
            v-model="reason"
            class="reason-textarea"
            :placeholder="placeholder"
            :maxlength="maxlength"
        ></textarea>

        <div class="textarea-footer">
          <span class="error-text">{{ errorMessage }}</span>
          <span>{{ reason.length }}/{{ maxlength }}</span>
        </div>
      </div>

      <div class="dialog-footer">
        <button class="btn ghost small" type="button" @click="handleClose">
          取消
        </button>

        <button
            class="btn danger small"
            type="button"
            :disabled="loading"
            @click="handleConfirm"
        >
          {{ loading ? '提交中...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: {//决定了这个组件的弹出时机
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '驳回原因'
  },
  tip: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入原因'
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  maxlength: {
    type: Number,
    default: 200
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

const reason = ref('')
const errorMessage = ref('')

watch(
    () => props.visible,
    (newValue) => {
      if (newValue) {
        reason.value = ''
        errorMessage.value = ''
      }
    }
)

function handleClose() {
  if (props.loading) return

  emit('update:visible', false)
}

function handleConfirm() {
  const finalReason = reason.value.trim()

  if (!finalReason) {
    errorMessage.value = '原因不能为空'
    return
  }

  if (finalReason.length < 2) {
    errorMessage.value = '原因不能太短'
    return
  }

  errorMessage.value = ''
  emit('confirm', finalReason)//NOTE:再父组件中使用@confirm=submitReject"，那么父组件的submitReject函数就会自动接收finalReason作为参数
}
</script>

<style scoped>
.dialog-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  background: rgba(15, 23, 42, 0.35);
}

.reason-dialog {
  width: 460px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

.dialog-header {
  height: 58px;
  padding: 0 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e9f2;
}

.dialog-header h3 {
  margin: 0;
  color: #152033;
  font-size: 18px;
}

.dialog-close {
  border: 0;
  background: transparent;
  color: #6b7280;
  font-size: 24px;
  cursor: pointer;
}

.dialog-body {
  padding: 20px 22px;
}

.dialog-tip {
  margin: 0 0 12px;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
}

.reason-textarea {
  width: 100%;
  height: 120px;
  padding: 12px 14px;
  border: 1px solid #dfe3e8;
  border-radius: 10px;
  resize: none;
  outline: none;
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
  box-sizing: border-box;
}

.reason-textarea:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
}

.textarea-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  color: #9ca3af;
  font-size: 12px;
}

.error-text {
  color: #dc2626;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 22px;
  border-top: 1px solid #e5e9f2;
}

.btn {
  height: 38px;
  padding: 0 18px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  font-weight: 700;
  cursor: pointer;
}

.btn.ghost {
  color: #374151;
}

.btn.danger {
  color: #fff;
  background: #dc2626;
  border-color: #dc2626;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}
</style>