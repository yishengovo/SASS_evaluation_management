<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-24 22:30:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-07 20:32:59
 * @Description: file content
-->
<script lang="ts" setup>
import { getTemplateApi } from '/@src/api/surTemplate/surTemplate'
import { Record } from '/@src/api/surTemplate/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
type propsType = {
  type: '测评' | '调查' | '360度评估'
}
const props = withDefaults(defineProps<propsType>(), {
  type: '测评',
})
const pageInfo = ref({
  pageSize: 18,
  pageNum: 1,
})
const currentPage = ref(1)
let total = ref(0)
watch(currentPage, async (newValue) => {
  //直接监听
  pageInfo.value.pageNum = newValue
  await getTemplate()
})
const showSurveyPreview = ref(false)
const isLoaderActive = ref(false)
const searchName = ref('')
const templateData = ref<Record[]>([])
const getTemplate = async (searchName = '') => {
  isLoaderActive.value = true
  const res = await getTemplateApi({
    ...pageInfo.value,
    type: props.type,
    name: searchName,
  })
  templateData.value = res.data.result.records
  total.value = res.data.result.total
  if (res.data.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '获取问卷模板失败,请稍后重试!',
    })
  }
  isLoaderActive.value = false
}
const searchTemplate = async () => {
  await getTemplate(searchName.value)
}
const surveyJson = ref('')
const priviewSurTemplate = (jsonPreview: string) => {
  showSurveyPreview.value = true
  surveyJson.value = jsonPreview
}
onMounted(async () => {
  await getTemplate()
})
</script>
<template>
  <div class="sur-template">
    <div class="search-title">
      <div class="name">{{ type }}模板</div>
      <div class="is-flex">
        <VField class="is-flex" style="align-items: center">
          <VControl>
            <VInput v-model="searchName" type="text" placeholder="请输入模板名称" />
          </VControl>
        </VField>
        <VButton color="primary" raised class="btn" @click="searchTemplate">
          搜索
        </VButton>
      </div>
    </div>
    <VLoader size="large" :active="isLoaderActive" translucent class="list-loader">
      <div class="template-content">
        <div v-for="item in templateData" :key="item.id" class="item">
          <span class="previewIcon" @click="priviewSurTemplate(item.jsonPreview)">
            <i class="lnir lnir-eye" aria-hidden="true"></i
          ></span>
          <div class="templatePng">
            <img src="../../assets/surveyTemplate.png" alt="" />
          </div>
          <div class="bottom">
            <div class="name">{{ item.surName }}</div>
            <div class="pay">
              <div class="integral">29积分</div>
              <div class="buy">购买</div>
            </div>
          </div>
        </div>
      </div>
    </VLoader>
    <VFlexPagination
      v-model:current-page="currentPage"
      :item-per-page="pageInfo.pageSize"
      :total-items="total"
      :max-links-displayed="5"
      no-router
    />
    <AuDialog
      v-if="showSurveyPreview"
      title="问卷预览"
      :footer="false"
      @close="showSurveyPreview = false"
      @cancel="showSurveyPreview = false"
    >
      <SurveyPreview :survey-json="surveyJson"></SurveyPreview>
    </AuDialog>
  </div>
</template>
<style scoped lang="scss">
@media (max-width: 600px) {
  .template-content {
    .item:nth-child(2n) {
      margin-right: 0px !important;
    }
  }
}
.sur-template {
  width: 100%;
  .list-loader {
    min-height: 250px;
  }
  .search-title {
    display: flex;
    justify-content: space-between;
    .name {
      position: relative;
      font-size: 18px;
      line-height: 26px;
      padding-left: 10px;
      width: 8em;
      text-align: left;
      &::before {
        content: '';
        position: absolute;
        left: 0;
        width: 4px;
        height: 26px;
        border-radius: 2px;
        background-color: var(--primary);
      }
    }
    .btn {
      margin-left: 10px;
    }
  }
  .template-content {
    margin-top: 10px;
    display: flex;
    flex-wrap: wrap;
    .item {
      position: relative;
      width: 142px;
      min-height: 230px;
      margin-right: 20px;
      margin-bottom: 20px;
      border-radius: 3px;
      background-color: #dde5ed;
      border: 1px solid #eaeaea;
      cursor: pointer;
      &:hover {
        box-shadow: 5px 5px 5px #e6e6e6;
        .previewIcon {
          i {
            display: block;
          }
        }
      }
      .previewIcon {
        display: inline-block;
        position: absolute;
        right: 4px;
        top: 2px;
        i {
          display: none;
          color: hsl(153deg 3% 41%);
          font-size: 24px;
        }
      }
      .templatePng {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        width: 100px;
        bottom: 0px;
      }
      .bottom {
        width: 100%;
        position: absolute;
        padding: 10px;
        background-color: white;
        left: 0;
        bottom: 0;
        .name {
          color: #484848;
          font-size: 14px;
          line-height: 16px;
          text-align: left;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          word-wrap: break-word;
          margin-bottom: 9px;
        }
        .pay {
          display: flex;
          justify-content: space-between;
          align-items: center;
          .integral {
            color: #fd3f31;
          }
          .buy {
            color: #ff9000;
          }
        }
        &:hover {
          .name {
            overflow: visible;
            text-overflow: clip;
            white-space: normal;
          }
        }
      }
    }
  }
}
</style>
