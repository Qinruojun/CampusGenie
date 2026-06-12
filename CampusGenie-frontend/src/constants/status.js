

/**
 * 状态常量（启用/禁用）
 */


    /**
     * 知识条目启用
     */
   export const  ENABLE = 1;

    /**
     * 知识条目禁用
     */
    export const DISABLE = 0;

    /**
     * 知识库发布状态
     */
    export const PUBLISHED = 1;
    export const  STOPPED = 0;

    /**
     * 用户账号状态
     */
    export const  USER_NORMAL = 1;
    export const USER_BANNED = 0;

    /**
     * 用户角色
     */
    export const USER_ROLE = 0;
    export const ADMIN_ROLE = 1;

    /**
    * 知识条目排序
    * */
    export const SORT_ORDER_ASC = 'asc';
    export const SORT_ORDER_DESC = 'desc';
    /**
     * 用户贡献状态
     **/
//审核表三种状态，待审核，通过，驳回
export const  WAIT_FOR_REVIEW = 0;
export const REVIEW_PASS = 1;
export const  REVIEW_REJECT = 2;

export const  WAIT_FOR_REVIEW_MSG = '待审核';
export const REVIEW_PASS_MSG = '已通过';
export const  REVIEW_REJECT_MSG = '已驳回';