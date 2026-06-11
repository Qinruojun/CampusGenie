<!--用户贡献页，查看自己的贡献-->
<template>
  <div class="contribution-page">
<!--    <section class="page-head">-->
<!--      <p class="eyebrow">-->
<!--        CampusGenie-->
<!--      </p>-->
<!--      <h1>我的贡献</h1>-->
<!--      <p class="desc">-->
<!--        查看你提交过的校园问答内容，按提交时间从近到远排列。-->
<!--      </p>-->
<!--    </section>-->


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
              @click="handleToggleSortOrder"
            >
              更新时间
              <span class="sort-arrow">
                {{ queryForm.sortOrder === SORT_ORDER_DESC ? '↓' : '↑' }}
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
              @delete="handleDelete"
            />
          </div>
          <>
            <div class="pagination">
              <button @click="handlePrevPage">
                上一页
              </button>
              <button
                  class="current"
                  @click="page = 1"
              >
                1
              </button>
              <button @click="page = 2">
                2
              </button>
              <button @click="page = 3">
                3
              </button>
              <button @click="handleNextPage">
                下一页
          </>
            </button>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

    <script setup>
      import { computed,ref, onMounted } from 'vue'
      import { useRouter } from 'vue-router'

      import CategorySelect from '@/components/CategorySelect.vue'
      import { useCategoryOptions } from '@/composables/useCategoryOptions'
      import UserContributionCard from '@/components/user/ContributionCard.vue'
      import { SORT_ORDER_ASC, SORT_ORDER_DESC } from "@/constants/status.js";
      import { WAIT_FOR_REVIEW,REVIEW_PASS,REVIEW_REJECT} from "@/constants/status.js";
      import StatCard from "@/components/StatCard.vue";
      import {useContributionList} from "@/composables/user/useContributionList.js";

      const router = useRouter()

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
        loading,
        loadList,
        handleSearch,
        handleReset,
        handlePrevPage,
        handleNextPage,
        handleToggleSortOrder,
        loadContributionList,

      } = useContributionList()



      onMounted(()=>{
        loadCategoryList()
        loadContributionList()
      })
      const handleView = item => {
        alert("hhh,什么都没写")
        //TODO
      }
      //TODO: 还要后端返回统计信息
      const statList = computed(() => [
        {
          title: '贡献总数',
          value: total.value,
          unit: '条',
          icon: '▧',
          tone: 'green'
        },
        {
          title: '已通过',
          value: '1,102',//HACK
          unit: '条',
          icon: '➤',
          tone: 'green'
        },
        {
          title: '待审核',
          value: 154, //HACK
          unit: '条',
          icon: '◷',
          tone: 'orange'
        },
        {
          title: '已驳回',
          value: 32,//HACK
          unit: '条',
          icon: '↗',
          tone: 'green',
          extra: '较上周 ↑18%'
        }
      ])

    </script>

    <style scoped src="@/styles/card-list.css"></style>
    <style>
    /* TODO  */
      .filter-panel {
        display: grid;
        grid-template-columns: minmax(280px, 1.7fr) 220px 160px 110px 90px 90px;
        gap: 16px;
        align-items: center;
        padding: 20px;
        margin-bottom: 18px;
        background: #fff;
        border-radius: 8px;
        box-shadow: 0 4px 14px rgba(15, 23, 42, 0.08);
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

      .sort-btn:hover {
        color: #16a34a;
        border-color: #16a34a;
      }

      .sort-arrow {
        margin-left: 4px;
      }
    </style>

<style scoped>
.contribution-page {
  width: 100%;
  min-height: calc(100vh - 72px);
  padding: 72px 10vw 96px;
  /*background: #f8f7f2;*/
  /*color: #1c241c;*/

}
.page {
 width: 100%;
  margin: 0 auto;
 padding: 70px 0 96px;
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
  max-width: 960px;
  margin: 26px auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
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