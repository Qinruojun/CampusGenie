import { getPage, approve, reject, edit } from '@/api/admin/knowledgeDraft.js'
import { SORT_ORDER_ASC, SORT_ORDER_DESC } from '@/constants/status.js'
import { SUCCESS } from '@/constants/code.js'
import { usePageList } from '@/composables/usePageList.js'

export function useKnowledgeDraftList() {
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

    const handleApprove = async (id) => {
        const ok = confirm('确定要审核通过这条知识草稿吗？')
        if (!ok) return

        try {
            const res = await approve(id)

            if (res.code === SUCCESS) {
                alert(res.msg || '审核通过成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '审核通过失败')
            }
        } catch (error) {
            console.error(error)
            const errorMsg = error.response?.data?.msg || error.message || '服务器异常，审核通过失败'
            alert(errorMsg)
        }
    }

    const handleReject = async (id) => {
        const ok = confirm('确定要驳回这条知识草稿吗？')
        if (!ok) return

        try {
            const res = await reject(id)

            if (res.code === SUCCESS) {
                alert(res.msg || '驳回成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '驳回失败')
            }
        } catch (error) {
            console.error(error)
            const errorMsg = error.response?.data?.msg || error.message || '服务器异常，驳回失败'
            alert(errorMsg)
        }
    }

    const handleEdit = async (data) => {
        try {
            const res = await edit(data)

            if (res.code === SUCCESS) {
                alert(res.msg || '修改成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '修改失败')
            }
        } catch (error) {
            console.error(error)
            const errorMsg = error.response?.data?.msg || error.message || '服务器异常，修改失败'
            alert(errorMsg)
        }
    }

    return {
        ...pageList,
        loadKnowledgeDraftList: pageList.loadList,
        handleApprove,
        handleReject,
        handleEdit
    }
}