import {getPage, Delete} from '@/api/user/contribution.js'
import { SORT_ORDER_DESC } from '@/constants/status.js'
import { usePageList } from '@/composables/usePageList.js'
import {SUCCESS} from "@/constants/code.js";
import {reject} from "@/api/admin/contirbute.js";

export function useContributionList() {
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
                categoryId: queryForm.categoryId,
                status: queryForm.status,
                keyword: queryForm.keyword,
                sortOrder: queryForm.sortOrder,
            }
        },
    })
    const handleDelete=async (item)=> {
        try {
            const res = await Delete(item.id)
            if (res.code === SUCCESS) {
                alert(res.msg || '贡献删除成功')
                await pageList.loadList()
            } else {
                alert(res.msg || '贡献删除失败')
            }
        } catch (error) {
            console.error(error)
            alert('服务器异常，贡献删除失败')
        }
    }


    return {
        ...pageList,

        // 给页面一个更明确的名字
        loadContributionList: pageList.loadList,

    }
}