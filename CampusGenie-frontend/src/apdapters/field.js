//使用适配器模式，统一接口，提高可维护性
export function normalizeKnowledgeItem(raw) {
    return {
        id: raw.id,
        username: raw.username ,
        question: raw.question ,
        answer: raw.answer ,
        categoryName: raw.categoryName ,
        source: raw.source || raw.supplement|| '未知来源',
        contact: raw.contact ,
        statusDesc:raw.statusDesc,
        status: raw.status,
        createTime: raw.createTime ,
        updateTime: raw.updateTime || raw.updatedTime ,
        rejectReason: raw.rejectReason,
        reviewedTime: raw.reviewedTime,
    }
}