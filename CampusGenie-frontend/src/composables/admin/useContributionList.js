import {approve, getPage,reject} from '@/api/admin/contirbute.js'
import { SORT_ORDER_DESC } from '@/constants/status.js'
import { usePageList } from '@/composables/usePageList.js'
import {SUCCESS} from "@/constants/code.js";

export function useContributionList() {
    const pageList = usePageList({
        pageApi: getPage,

        defaultQueryForm: {
            keyword: '',
            categoryId: '',
            status: '',
            username: '',
            startTime: '',
            endTime: '',
            sortOrder: SORT_ORDER_DESC,
        },

        buildParams({ page, pageSize, queryForm }) {
            return {
                page,
                pageSize,
                keyword: queryForm.keyword,
                categoryId: queryForm.categoryId,
                status: queryForm.status,
                username: queryForm.username,
                startTime: queryForm.startTime,
                endTime: queryForm.endTime,
                sortOrder: queryForm.sortOrder,
            }
        },
    })
    const handleApprove= async (item) => {
        const ok = confirm('确定要通过这条贡献吗？')
        if (!ok) return
        const data = {
            editedQuestion:item.question,
            editedAnswer: item.answer,
        }
        try {
            const res = await approve(item.id,data)
            if (res.code === SUCCESS) {
                alert(res.msg || '审核通过成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '审核通过失败')
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，审核通过失败')
        }

    }
    const handleReject= async (item,rejectReason) => {
        const ok = confirm('确定要驳回这条贡献吗？')
        if (!ok) return
        const data = {
            rejectReason: rejectReason,
        }
        try {
            const res = await reject(item.id, data)
            if (res.code === SUCCESS) {
                alert(res.msg || '审核驳回成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '审核驳回失败')
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，审核驳回失败')
        }
    }



    return {
        ...pageList,

        // 给页面一个更明确的名字
        loadContributionList: pageList.loadList,
        handleApprove,
        handleReject
    }
}