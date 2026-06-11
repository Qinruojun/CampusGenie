import { computed, onMounted, onUnmounted, ref } from 'vue'
import { getHotlist } from '@/api/user/hotQuestion'
import {adminGetHotlist} from "@/api/admin/hotQuestion.js";
import {SUCCESS} from '@/constants/code.js'

import {USER_ROLE ,USERNAME_KEY} from '../constants/storage'
import {TOKEN_KEY,ADMIN_ROLE,ROLE_KEY} from '../constants/storage'
export function useHotQuestions(options = {}) {
    const {
        autoLoad = true,//决定是不是要页面一挂载就自动加载hotquestions
        polling = true,
        pollingInterval =10* 60 * 1000
    } = options

    const list = ref([])//存的是热点问题数据
    const loading = ref(false)
    const errorMessage = ref('')

    let timer = null

    const hasData = computed(() => {
        return list.value.length > 0
    })
//
    const totalQueryCount = computed(() => {
        return list.value.reduce((sum, item) => {
            return sum + Number(item.queryCount || 0)
        }, 0)
    })

    const topQuestion = computed(() => {
        return list.value[0] || null
    })

    async function loadHotQuestions() {
        loading.value = true
        errorMessage.value = ''

        try {
            const role = localStorage.getItem(ROLE_KEY)

            const res = role === ADMIN_ROLE ? await adminGetHotlist() : await getHotlist()

            errorMessage.value = res.msg || res.message || '热点问题加载失败'
            return []
        } catch (error) {
            console.error(error)
            errorMessage.value = '服务器异常，热点问题加载失败'
            return []
        } finally {
            loading.value = false
        }
    }

    function refreshHotQuestions() {
        return loadHotQuestions()
    }

    function startPolling() {
        stopPolling()//确保之前的计时器没有在跑

        timer = setInterval(() => {
            loadHotQuestions()
        }, pollingInterval)
    }

    function stopPolling() {
        if (timer) {
            clearInterval(timer)//关掉计时器
            timer = null
        }
    }
//判断网页标签是不是被用户切到后台了，切到后台就停止不断请求hotquesion列表
    function handleVisibilityChange() {
        if (document.hidden) {
            stopPolling()
            return
        }

        loadHotQuestions()

        if (polling) {
            startPolling()
        }
    }

    onMounted(() => {
        if (autoLoad) {
            loadHotQuestions()
        }

        if (polling) {
            startPolling()
        }

        document.addEventListener('visibilitychange', handleVisibilityChange)
    })

    onUnmounted(() => {
        stopPolling()
        document.removeEventListener('visibilitychange', handleVisibilityChange)
    })

    return {
        list,
        loading,
        errorMessage,
        hasData,
        totalQueryCount,
        topQuestion,
        loadHotQuestions,
        refreshHotQuestions,
        startPolling,
        stopPolling
    }
}