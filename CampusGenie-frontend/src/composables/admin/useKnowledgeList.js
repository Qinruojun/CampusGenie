import { getPage, Delete, changeStatus } from '@/api/admin/knowledge.js'
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
            } else {
                alert(res.msg || '删除失败')
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，删除失败')
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
                await pageList.loadList()
            } else {
                alert(res.msg || `${actionText}失败`)
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，状态切换失败')
        }
    }

    return {
        ...pageList,

        // 给旧页面保留原来的名字，避免页面大改
        loadKnowledgeList: pageList.loadList,

        handleDelete,
        handleToggleStatus,

    }
}