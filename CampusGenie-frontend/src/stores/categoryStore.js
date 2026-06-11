import { defineStore  } from 'pinia'
import { getCategoryList } from '@/api/category'
import { SUCCESS } from '@/constants/code.js'

/**
 *
 * 创建SessionStorage
 */

const STORAGE_KEY = 'campusgenie:category-list'

function readCategoryCache() {
    const raw = sessionStorage.getItem(STORAGE_KEY)
    if(!raw ){
        return {
            list: [],
            cachedAt: 0
        }
    }
    try {
        return JSON.parse(raw)
    } catch (error) {
        console.error(error)

        return {
            list: [],
            cachedAt: 0
        }
    }


}
function writeCategoryCache(list) {
    sessionStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
            list,
            cachedAt: Date.now()
        })
    )
}
function removeCategoryCache() {
    sessionStorage.removeItem(STORAGE_KEY)
}

function isSuccessCode(code) {
    return code === SUCCESS
}

/**
 *
 * Pinia存储
 */
export const useCategoryStore = defineStore('category', {
    state: () => {
        const cache = readCategoryCache()

        return {
            categoryOptions: cache.list || [],
            categoryLoading: false,
            categoryError: '',
            cachedAt: cache.cachedAt || 0
        }
    },
    getters: {//类似于Vue中的计算属性
        hasCategoryCache(state) {
            return state.categoryOptions.length > 0
        }
    },
    actions: {
        setCategoryOptions(list) {
            this.categoryOptions = list || []
            this.cachedAt = Date.now()

            writeCategoryCache(this.categoryOptions)
        },
//可以决定是否通过刷新方式即重新从后端获取分类表
        async loadCategoryList(options = {}) {//表示options是可选参数对象
            const {force = false} = options//从option里解构出force, 如果options没有force属性，则默认为false

            if (!force && this.hasCategoryCache) {
                return this.categoryOptions
            }

            this.categoryLoading = true
            this.categoryError = ''

            try {
                debugger

                const result = await getCategoryList()

                console.log('分类接口返回 result：', result)
                console.log('result.code：', result?.code)
                console.log('SUCCESS：', SUCCESS)

                debugger

                if (!isSuccessCode(result?.code)) {
                    throw new Error(result?.msg || '分类加载失败')
                }

                this.setCategoryOptions(result.data || [])

                return this.categoryOptions
            } catch (error) {
                console.error('分类加载异常：', error)
                this.categoryError = '分类加载失败'

                return []
            } finally {
                this.categoryLoading = false
            }
        },


        refreshCategoryList() {
            return this.loadCategoryList({
                force: true
            })
        },

        clearCategoryCache() {
            this.categoryOptions = []
            this.categoryError = ''
            this.cachedAt = 0

            removeCategoryCache()
        }
    }
})