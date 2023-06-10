<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-24 22:30:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-07 20:32:59
 * @Description: file content
-->
<script lang="ts" setup>
import { getTemplateApi, buyTemplateApi, PushTemplateApi } from '/@src/api/surTemplate/surTemplate'
import { Record } from '/@src/api/surTemplate/type'
import { Notice } from '/@src/components/base/au-notice/Notice'
import type { TemplateInfo } from '/@src/api/createProject/type'
type propsType = {
  type: '测评' | '调查' | '360度评估' | '我的'
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
const router = useRouter()
const showSurveyPreview = ref(false)
const showPushMySurvey = ref(false)
const isLoaderActive = ref(false)
const searchName = ref('')
const currentSurveyId = ref('')
const currentSurveyCredit = ref()
const currentSurveyName = ref('')
const showPushSurveyModal = ref(false)
const templateData = ref<Record[]>([])

const saveTemplate = () => {
  getTemplate()
}

// 查询问卷模板
const getTemplate = async (searchName = '') => {
  isLoaderActive.value = true
  const res = await getTemplateApi({
    ...pageInfo.value,
    type: props.type,
    name: searchName,
  })
  console.log(res);
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
// 搜索问卷模板
const searchTemplate = async () => {
  await getTemplate(searchName.value)
}
// 购买问卷模板
const buyTemplate = async (id: string) => {
  isLoaderActive.value = true
  const res = await buyTemplateApi({surveyId: id})
  // console.log(res.data);
  if (res.data.code === 500) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '购买问卷失败！可能是积分不足或者网络问题！',
    })
  } else if (res.data.code === 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'success',
      message: '购买问卷成功！',
    })
  }
}
// 弹窗相关
const showDialog = ref(false)
const onClose = () => {
  showDialog.value = false
}
// 模板问卷（调查和360）
const templateInfo = ref<any>({})
// 编辑我的问卷模板
const editTemplate = async (item: any) => {
  // console.log(item.id, item.surName);
  templateInfo.value.id = item.id
  templateInfo.value.name = item.surName
  templateInfo.value.content = item.surContent
  templateInfo.value.type = item.type
  templateInfo.value.jsonPreview = item.jsonPreview
  // console.log(templateInfo);
  isLoaderActive.value = false
  showDialog.value = true
}

// 模板小卡片底部展示的三种情况
function isShowEditAndPush(type: string) : string {
  if(props.type === '我的' && type === '测评') {
    return '我的测评'
  } else if (props.type !== '我的') {
    return '市场'
  } else {
    return '我的调查和360'
  }
}

// 打开上传问卷的弹窗
const openPushModal = (id: string) => {
  currentSurveyId.value = id
  currentSurveyCredit.value = ''
  currentSurveyName.value = ''
  showPushSurveyModal.value = true
}
// 关闭上传问卷的弹窗
const cancelPushSurvey = () => {
  showPushSurveyModal.value = false
}
// 确定上传问卷
const pushMySurvey = () => {
  pushTemplate()
  console.log("currentSurveyId:",currentSurveyId.value);
  console.log("currentSurveyCredit:",currentSurveyCredit.value);
  console.log("currentSurveyName:",currentSurveyName.value);
}

// 上传问卷的表单校验
const pushFormValidation = () => {
  console.log(currentSurveyCredit.value.string);
  if(currentSurveyName.value === '') return Notice({
      notice_type: 'warning',
      message: '请填写问卷名称！',
    })
  else if(currentSurveyCredit.value === '') return Notice({
    notice_type: 'warning',
    message:'请填写问卷积分！'
  })
  return true
}

// 上传我的问卷模板
const pushTemplate = async () => {
  if(pushFormValidation() === true) {
    isLoaderActive.value = true
    showPushSurveyModal.value = false
    const res = await PushTemplateApi({surveyProjectId: currentSurveyId.value, 
                                     credit: parseInt(currentSurveyCredit.value),
                                     surName: currentSurveyName.value})
    console.log(res.data);
    if (res.data.code === 500) {
      isLoaderActive.value = false
      return Notice({
        notice_type: 'warning',
        message: '上传问卷失败！请检查您的积分余额和网络是否正常！',
      })
    } else if (res.data.code === 200) {
      isLoaderActive.value = false
      showPushSurveyModal.value = false
      return Notice({
        notice_type: 'success',
        message: '上传问卷成功! 扣除了您1积分',
      })
    }
  }
  // console.log("==================================",pushFormValidation()); // undefined | true
  
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
                <template v-if="isShowEditAndPush(item.type) === '我的调查和360'">
                  <div class="buy" @click="editTemplate(item)">编辑</div>
                  <div class="buy" @click="openPushModal(item.id)">上传</div>
                </template>
                <template v-else-if="isShowEditAndPush(item.type) === '市场'">
                  <div class="integral">{{ item.credit }} 积分</div>
                  <div class="buy" @click="buyTemplate(item.id)">购买</div>
                </template>
                <template v-else>
                  <div class="integral">{{ item.credit }} &nbsp;</div>
                  <div class="buy" @click="buyTemplate(item.id)"> &nbsp;</div>
                </template>
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

    <VModal
      :open="showPushSurveyModal"
      actions="right"
      title="设置问卷"
      size="small"
      @close="cancelPushSurvey"
    >
      <template #content>
        <div style="margin-right:1.8vw;">
        <VField label="名称:" horizontal>
          <VControl fullwidth>
            <template v-if="false">
              <span class="not-edit" @click="becomeEdit">{{
                currentSurveyName
              }}</span>
            </template>
            <template v-else>
              <VInput
                v-model="currentSurveyName"
                type="text"
                placeholder="请输入问卷名称"
              />
            </template>
          </VControl>
        </VField>
        </div>
        <br>
        <div style="margin-right:1.8vw;">
        <VField label="积分:" horizontal>
          <VControl fullwidth>
            <template v-if="false">
              <span class="not-edit" @click="becomeEdit">{{
                currentSurveyCredit
              }}</span>
            </template>
            <template v-else>
              <VInput
                v-model="currentSurveyCredit"
                type="number"
                placeholder="请输入售卖积分"
              />
            </template>
          </VControl>
        </VField>
        </div>
        <!-- <VInput v-model="currentSurveyCredit"></VInput> -->
      </template>
      <template #action>
        <VButton color="primary" raised @click="pushMySurvey">确定上传</VButton>
      </template>
    </VModal>
    
    <!-- 编辑问卷 -->
    <AuDialog v-if="showDialog" title="问卷设计" :footer="false" @close="onClose">
      <TemplateEdit
        :template-info="templateInfo"
        @save-template="saveTemplate"
      ></TemplateEdit>
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
