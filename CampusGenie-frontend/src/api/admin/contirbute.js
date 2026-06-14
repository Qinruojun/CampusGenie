//审核用户贡献
import request from '../request'
import {USE_MORK   } from "@/constants/test.js";
import { SUCCESS} from "@/constants/code.js";

export function reject(id,data){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "审核驳回成功",
            "data": null
        })
    }
    return request({
        url: `/admin/contributions/${id}/reject`,
        method: 'PUT',
        data
    })
}
export function approve(id,data){
    if(USE_MORK){
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "审核通过成功",
            "data": null
        })
    }
    return request({
        url: `/admin/contributions/${id}/approve`,
        method: 'PUT',
        data
    })
}
export function getPage(params){
    if(USE_MORK){
        return Promise.resolve({
                "code": SUCCESS,
                "msg": "查询成功",
                "data": {
                    "total": 1,
                    "records": [
                        {
                            "id": 1001,
                            "username": "zhangsan",
                            "question": "图书馆假期开放时间是什么？",
                            "answer": "假期期间图书馆开放时间为9:00-17:00",
                            "categoryName": "图书馆服务",
                            "supplement": "官网公告",
                            "contact": "zhan***@example.com",
                            "statusDesc": "待审核",
                            "createdTime": "2026-06-02 10:30:00",
                            "reviewedTime": null,
                            "rejectReason": null
                        }
                    ]
                }
            }
        )
    }

        return request.get('/admin/contributions/page', {
            params
        })

}

export function getStatistics() {
    if (USE_MORK) {
        return Promise.resolve({
            "code": SUCCESS,
            "msg": "获取统计数据成功",
            "data": {
                "pendingCount": 154,
                "approvedCount": 32,
                "rejectedCount": 32
            }
        })
    }

    return request.get('/admin/contributions/statistics')
}
