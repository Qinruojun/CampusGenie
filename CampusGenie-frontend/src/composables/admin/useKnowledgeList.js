import { getPage, Delete, changeStatus, getStatistics } from '@/api/admin/knowledge.js'
import {SORT_ORDER_ASC, SORT_ORDER_DESC} from '@/constants/status.js'
import { SUCCESS } from '@/constants/code.js'
import { DISABLE, ENABLE } from '@/constants/status.js'
import { usePageList } from '@/composables/usePageList.js'

export function useKnowledgeList() {
    //调用f分页接口加载页面
    const pageList = usePageList({
        pageApi: getPage,

        defaultQueryForm: {
            keyword: '',
            categoryId: '',
            status: '',
            sortOrder: SORT_ORDER_DESC,
        },

        buildParams({ page, pageSize, queryForm }) {
            return {
                page,
                pageSize,
                keyword: queryForm.keyword,
                categoryId: queryForm.categoryId,
                status: queryForm.status,
                sortOrder: queryForm.sortOrder,
            }
        },
    })

    const handleDelete = async (id) => {
        const ok = confirm('确定要删除这条知识吗？')
        if (!ok) return

        try {
            const res = await Delete(id)

            if (res.code === SUCCESS) {
                alert(res.msg || '删除成功')
                await pageList.loadList()
                // 注意：这里不需要调用 loadStatisticsData()，因为它在 KnowledgeManage.vue 中处理
            } else {
                alert(res.msg || '删除失败')
            }
        } catch (error) {
            console.error(error)
            // 尝试从错误响应中获取后端返回的错误信息
            const errorMsg = error.response?.data?.msg || error.message || '服务器异常，删除失败'
            alert(errorMsg)
        }
    }

    const handleToggleStatus = async (item) => {
        const nextStatus = item.status === ENABLE ? DISABLE : ENABLE
        const actionText = nextStatus === ENABLE ? '启用' : '停用'

        const ok = confirm(`确定要${actionText}这条知识吗？`)
        if (!ok) return

        try {
            const res = await changeStatus(item.id, nextStatus)

            if (res.code === SUCCESS) {
                alert(res.msg || `${actionText}成功`)
                // 先刷新列表
                await pageList.loadList()
                // 再刷新统计数据
                const statsData = await loadStatistics()
                console.log('刷新后的统计数据:', statsData)
            } else {
                alert(res.msg || `${actionText}失败`)
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，状态切换失败')
        }
    }

    const loadStatistics = async () => {
        try {
            const res = await getStatistics()
            if (res.code === SUCCESS) {
                return res.data
            } else {
                console.error('获取统计数据失败:', res.msg)
                return null
            }
        } catch (error) {
            console.error('获取统计数据异常:', error)
            return null
        }
    }


    return {
        ...pageList,

        // 给旧页面保留原来的名字，避免页面大改
        loadKnowledgeList: pageList.loadList,

        handleDelete,
        handleToggleStatus,
        loadStatistics

    }
}