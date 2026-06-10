import { storeToRefs } from 'pinia'
import { useCategoryStore } from '@/stores/categoryStore'

export function useCategoryOptions() {
    const categoryStore = useCategoryStore()

    const {
        categoryOptions,
        categoryLoading,
        categoryError
    } = storeToRefs(categoryStore)

    const loadCategoryList = (options) => {
        return categoryStore.loadCategoryList(options)
    }

    const refreshCategoryList = () => {
        return categoryStore.refreshCategoryList()
    }

    const clearCategoryCache = () => {
        categoryStore.clearCategoryCache()
    }

    return {
        categoryOptions,
        categoryLoading,
        categoryError,
        loadCategoryList,
        refreshCategoryList,
        clearCategoryCache
    }
}