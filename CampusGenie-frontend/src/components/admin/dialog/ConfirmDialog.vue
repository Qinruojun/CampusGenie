<template>
  <div v-if="visible" class="dialog-mask" @click.self="handleClose">
    <div class="confirm-dialog">
      <div class="dialog-header">
        <h3>{{ title }}</h3>
        <button class="dialog-close" type="button" @click="handleClose">
          ×
        </button>
      </div>

      <div class="dialog-body">
        <p class="dialog-message">
          {{ message }}
        </p>
      </div>

      <div class="dialog-footer">
        <button class="btn ghost small" type="button" @click="handleClose">
          取消
        </button>

        <button
          class="btn primary small"
          type="button"
          :disabled="loading"
          @click="handleConfirm"
        >
          {{ loading ? '处理中...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '确认操作'
  },
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  confirmText: {
    type: String,
    default: '确认'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

function handleClose() {
  if (props.loading) return
  emit('update:visible', false)
}

function handleConfirm() {
  emit('confirm')
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

.confirm-dialog {
  width: 400px;
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
  padding: 28px 22px;
}

.dialog-message {
  margin: 0;
  color: #374151;
  font-size: 15px;
  line-height: 1.6;
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

.btn.primary {
  color: #fff;
  background: #16a34a;
  border-color: #16a34a;
}

.btn.primary:hover {
  background: #15803d;
  border-color: #15803d;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.65;
}
</style>
