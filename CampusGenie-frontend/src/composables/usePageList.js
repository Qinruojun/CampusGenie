import {ref, watch} from 'vue'
import { SUCCESS } from '@/constants/code.js'
import {SORT_ORDER_ASC, SORT_ORDER_DESC} from "@/constants/status.js";

function removeEmptyParams(params) {
    const result = {}

    Object.keys(params).forEach((key) => {
        const value = params[key]

        if (value !== '' && value !== null && value !== undefined) {
            result[key] = value
        }
    })

    return result
}

export function usePageList(options) {
    const {
        pageApi,
        defaultQueryForm = {},
        defaultPageSize = 10,
        buildParams,
    } = options

    const queryForm = ref({ ...defaultQueryForm })
    const page = ref(1)
    const pageSize = ref(defaultPageSize)
    const total = ref(0)
    const list = ref([])
    const loading = ref(false)

    watch(page, () => {
        loadList()
    })

    // 监听 pageSize 也加上（更完整）
    watch(pageSize, () => {
        page.value = 1 // 切换每页条数 → 回到第一页
        loadList()
    })
    const loadList = async () => {
        loading.value = true

        try {
            const params = buildParams
                ? buildParams({
                    page: page.value,
                    pageSize: pageSize.value,
                    queryForm: queryForm.value,
                })
                : {
                    page: page.value,
                    pageSize: pageSize.value,
                    ...queryForm.value,
                }

            const res = await pageApi(removeEmptyParams(params))

            if (res.code === SUCCESS) {
                list.value = res.data.records || []
                total.value = res.data.total || 0
            } else {
                alert(res.msg || '查询失败')
            }
        } catch (error) {
            console.error(error)
            const status = error.response?.status
            const message = error.response?.data?.msg || error.response?.data?.message

            if (status === 401) {
                alert('登录已失效，请重新登录')
            } else {
                alert(message || '服务器异常，查询失败')
            }
        } finally {
            loading.value = false
        }
    }

    const handleSearch = () => {
        page.value = 1
        loadList()
    }
    const handleToggleSortOrder = () => {
        queryForm.value.sortOrder =
            queryForm.value.sortOrder === SORT_ORDER_DESC
                ? SORT_ORDER_ASC
                : SORT_ORDER_DESC

        loadList()
    }

    const handleReset = () => {
        queryForm.value = { ...defaultQueryForm }
        page.value = 1
        loadList()
    }

    const handlePrevPage = () => {
        if (page.value > 1) {
            page.value--
            loadList()
        }
    }

    const handleNextPage = () => {
        const maxPage = Math.ceil(total.value / pageSize.value)

        if (page.value < maxPage) {
            page.value++
            loadList()
        }
    }

    return {
        queryForm,
        page,
        pageSize,
        total,
        list,
        loading,
        loadList,
        handleSearch,
        handleReset,
        handlePrevPage,
        handleNextPage,
        handleToggleSortOrder,
    }
}