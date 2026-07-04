<!--用户贡献页，查看自己的贡献-->
<template>
  <div class="contribution-page">
    <div class="page">
      <main class="main">
        <section class="content">
          <div class="page-head">


            <div class="head-actions">

            </div>
          </div>
          <div class="filter-panel">
            <div class="search">
              <span>⌕</span>
              <input
                v-model="queryForm.keyword"
                placeholder="请输入问题关键词"
                @keyup.enter="handleSearch"
              >
            </div>

            <CategorySelect
              v-model="queryForm.categoryId"
              :options="categoryOptions"
              :loading="categoryLoading"
              :error-message="categoryError"
              placeholder="全部分类"
            />

            <select
              v-model="queryForm.status"
              class="filter-select"
            >
              <option value="">
                全部状态
              </option>
              <option :value="REVIEW_PASS">
                已通过
              </option>
              <option :value="REVIEW_REJECT">
                已驳回
              </option>
            </select>

            <button
              class="sort-btn"
              :class="{ 'sort-desc': queryForm.sortOrder === SORT_ORDER_DESC, 'sort-asc': queryForm.sortOrder === SORT_ORDER_ASC }"
              @click="handleToggleSortOrder"
            >
              更新时间
              <span class="sort-arrow">
                {{ queryForm.sortOrder === SORT_ORDER_DESC ? '⇩' : '⇧' }}
              </span>
            </button>

            <button
              class="btn primary small"
              @click="handleSearch"
            >
              搜索
            </button>
            <button
              class="btn ghost small"
              @click="handleReset"
            >
              重置
            </button>
          </div>

          <div class="stats">
            <StatCard
              v-for="item in statList"
              :key="item.title"
              :title="item.title"
              :value="item.value"
              :unit="item.unit"
              :icon="item.icon"
              :tone="item.tone"
              :extra="item.extra"
            />
          </div>


          <div class="card-list">
            <UserContributionCard
              v-for="item in list"
              :key="item.id"
              :item="item"
              @view="handleView"
              @delete="onDelete"
            />
          </div>

          <div class="pagination-bar">
            <RouterLink class="pagination-back-btn" to="/user/home">
              返回首页
            </RouterLink>
            <div
              v-if="totalPages > 1"
              class="pagination"
            >
              <button
                :disabled="page <= 1"
                @click="handlePrevPage"
              >
                上一页
              </button>
              <button
                v-for="pageNumber in pageNumbers"
                :key="pageNumber"
                :class="{ current: page === pageNumber }"
                @click="page = pageNumber"
              >
                {{ pageNumber }}
              </button>
              <button
                :disabled="page >= totalPages"
                @click="handleNextPage"
              >
                下一页
              </button>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

    <script setup>
      import { computed, onMounted, ref } from 'vue'

      import CategorySelect from '@/components/CategorySelect.vue'
      import { useCategoryOptions } from '@/composables/useCategoryOptions'
      import UserContributionCard from '@/components/user/ContributionCard.vue'
      import { SORT_ORDER_DESC } from "@/constants/status.js";
      import { WAIT_FOR_REVIEW_MSG,REVIEW_PASS,REVIEW_REJECT, REVIEW_PASS_MSG, REVIEW_REJECT_MSG} from "@/constants/status.js";
      import StatCard from "@/components/StatCard.vue";
      import { useContributionList } from "@/composables/user/useContributionList.js";
      import { useContributionViewStore } from '@/stores/contributionViewStore'
      import { useRouter } from 'vue-router'
      import { getUserStatistics } from '@/api/user/contribution.js'
      const router = useRouter()
      const contributionViewStore = useContributionViewStore()
      const {
        categoryOptions,
        categoryLoading,
        categoryError,
        loadCategoryList
      } = useCategoryOptions()


      const {
        queryForm,
        page,
        pageSize,
        total,
        list,
        handleSearch,
        handleReset,
        handlePrevPage,
        handleNextPage,
        handleToggleSortOrder,
        loadContributionList,
        handleDelete,

      } = useContributionList()

      const statistics = ref({
        pendingCount: 0,
        approvedCount: 0,
        rejectedCount: 0
      })

      const loadStatistics = async () => {
        try {
          const res = await getUserStatistics()
          if (res.code === 200) {
            statistics.value = res.data
          }
        } catch (e) {
          console.error('加载统计数据失败', e)
        }
      }


      onMounted(()=>{
        loadCategoryList()
        loadContributionList()
        loadStatistics()
      })
      const onDelete = async (item) => {
        await handleDelete(item)
        await loadStatistics()
      }

      const handleView = item => {
        contributionViewStore.setContribution(item)
        router.push(`/user/contribution/${item.id}`)
      }
      const currentPageStatusCount = statusDesc => {
        return list.value.filter(item => item.statusDesc === statusDesc).length
      }

      const totalPages = computed(() => {
        return Math.max(1, Math.ceil(total.value / pageSize.value))
      })

      const pageNumbers = computed(() => {
        return Array.from({ length: totalPages.value }, (_, index) => index + 1)
      })

      const statList = computed(() => [
        {
          title: '贡献总数',
          value: statistics.value.pendingCount + statistics.value.approvedCount + statistics.value.rejectedCount,
          unit: '条',
          icon: '▧',
          tone: 'green'
        },
        {
          title: '已通过',
          value: statistics.value.approvedCount,
          unit: '条',
          icon: '➤',
          tone: 'green'
        },
        {
          title: '待审核',
          value: statistics.value.pendingCount,
          unit: '条',
          icon: '◷',
          tone: 'orange'
        },
        {
          title: '已驳回',
          value: statistics.value.rejectedCount,
          unit: '条',
          icon: '↗',
          tone: 'green'
        }
      ])

    </script>

    <style scoped src="@/styles/card-list.css"></style>
    <style>
      .filter-panel .btn.small {
        width: 100%;
        min-width: 0;
        padding: 0 12px;
        font-size: 14px;
        box-sizing: border-box;
      }

      .filter-select {
        width: 100%;
        height: 44px;
        padding: 0 16px;
        border: 1px solid #dfe3e8;
        border-radius: 8px;
        background: #fff;
        color: #374151;
        font-size: 14px;
        outline: none;
        box-sizing: border-box;
      }

      .filter-select:focus {
        border-color: #16a34a;
        box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12);
      }

      .sort-btn {
        width: 100%;
        height: 44px;
        padding: 0 12px;
        border: 1px solid #dfe3e8;
        border-radius: 8px;
        background: #fff;
        color: #374151;
        font-size: 14px;
        font-weight: 700;
        cursor: pointer;
        box-sizing: border-box;
      }

      .sort-btn.sort-desc {
        color: #f97316;
        border-color: #f97316;
      }

      .sort-btn.sort-desc:hover {
        color: #ea580c;
        border-color: #ea580c;
      }

      .sort-btn.sort-asc {
        color: #16a34a;
        border-color: #16a34a;
      }

      .sort-btn.sort-asc:hover {
        color: #15803d;
        border-color: #15803d;
      }

      .sort-arrow {
        margin-left: 4px;
        font-size: 14px;
        font-weight: 700;
      }
    </style>

<style scoped>
.contribution-page {
  width: 100%;
  min-height: 100vh;
  padding: 0;
  background: radial-gradient(circle at top, #ffffff 0%, #fbfaf7 56%, #f7f5ef 100%);
}

.filter-panel {
  display: grid;
  grid-template-columns: minmax(200px, 1.3fr) 180px 140px 90px 80px 80px;
  gap: 12px;
  align-items: center;
  padding: 20px;
  margin-bottom: 18px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
}

@media (max-width: 1200px) {
  .filter-panel {
    grid-template-columns: minmax(160px, 1.3fr) 140px 120px 85px 75px 75px;
    gap: 8px;
  }
}

.search {
  box-sizing: border-box;
}

.pagination-bar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 26px;
}

.pagination-back-btn {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #374151;
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.pagination-back-btn:hover {
  border-color: #16a34a;
  color: #16a34a;
}

.page {
  width: min(1100px, calc(100% - 48px));
  margin: 0 auto;
  padding: 0;
  background: transparent !important;
}
.page-head {
  text-align: center;
  margin-bottom: 48px;
}

.eyebrow {
  margin: 0 0 12px;
  color: #219c55;
  font-size: 18px;
  font-weight: 700;
}

.page-head h1 {
  margin: 0;
  font-size: 56px;
  line-height: 1.1;
  font-weight: 600;
  color: #1d281d;
}

.desc {
  max-width: 560px;
  margin: 18px auto 0;
  color: #7c7a72;
  font-size: 16px;
  line-height: 1.8;
}

.toolbar {
  max-width: 960px;
  margin: 0 auto 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #68665f;
}

.toolbar strong {
  color: #219c55;
  font-size: 22px;
}

.refresh-btn {
  border: 1px solid #d9d7cf;
  background: #ffffff;
  color: #1d281d;
  border-radius: 999px;
  padding: 10px 22px;
  cursor: pointer;
}

.refresh-btn:hover {
  border-color: #219c55;
  color: #219c55;
}

.content-card {
  max-width: 960px;
  margin: 0 auto;
  background: #ffffff;
  border: 1px solid #e3e1da;
  border-radius: 22px;
  padding: 12px;
  box-shadow: 0 22px 70px rgba(31, 40, 31, 0.06);
}

.state {
  padding: 64px 20px;
  text-align: center;
  color: #88867e;
}

.state.error {
  color: #b94848;
}

.contribution-list {
  display: flex;
  flex-direction: column;
}

.contribution-item {
  padding: 26px 28px;
  border-radius: 18px;
}

.contribution-item + .contribution-item {
  border-top: 1px solid #eeeeea;
}

.contribution-item:hover {
  background: #fbfaf7;
}

.item-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.item-top h3 {
  margin: 0;
  color: #1d281d;
  font-size: 20px;
  font-weight: 700;
}

.time {
  margin: 8px 0 0;
  color: #96938b;
  font-size: 14px;
}

.status {
  flex: 0 0 auto;
  border-radius: 999px;
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 700;
}

.status.pending {
  background: #f5f2e8;
  color: #9a7a2f;
}

.status.approved {
  background: #eaf6ef;
  color: #219c55;
}

.status.rejected {
  background: #f8ecea;
  color: #b94848;
}

.answer {
  margin: 18px 0 0;
  color: #3a4038;
  line-height: 1.8;
}

.meta {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #8b887f;
  font-size: 14px;
}

.reject-reason {
  margin: 18px 0 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #fff6f4;
  color: #b94848;
  line-height: 1.7;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 14px;
}

.pagination button {
  border: 1px solid #d9d7cf;
  background: #ffffff;
  color: #1d281d;
  border-radius: 999px;
  padding: 10px 22px;
  cursor: pointer;
}

.pagination button:hover:not(:disabled) {
  border-color: #219c55;
  color: #219c55;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination span {
  color: #77746d;
}

@media (max-width: 768px) {
  .contribution-page {
    padding: 48px 20px 72px;
  }

  .page-head h1 {
    font-size: 40px;
  }

  .item-top {
    flex-direction: column;
  }

  .toolbar {
    flex-direction: column;
    gap: 16px;
  }
}
</style>