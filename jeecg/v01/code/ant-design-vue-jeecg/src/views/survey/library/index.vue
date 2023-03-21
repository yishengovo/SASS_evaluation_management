<template>
  <div>
    <div class="survey-container container" v-if="step === 1">
      <survey :survey="survey" />
    </div>
    <j-modal title="身份认证" :visible="visible" :closable="false">
      <au-form v-bind="formConfig" v-model="model" ref="form"></au-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="loading" @click="handleOk"> 确认 </a-button>
      </template>
    </j-modal>
    <j-modal title="提示" :visible="visibleInfo" :closable="false">
      选择要评价的人:
      <a-select
        :model="evaluatedId"
        style="width: 320px"
        placeholder="请选择"
        :options="evaluatorOptions"
        @change="handleSelectChange"
      >
        <a-select-option v-for="option in evaluatorOptions" :value="option.value" :key="option.value">{{
          option.label
        }}</a-select-option>
      </a-select>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="loading" @click="handleOkSelect"> 确认 </a-button>
      </template>
    </j-modal>
  </div>
</template>

<script>
import { StylesManager, Model } from 'survey-vue'
import 'survey-vue/defaultV2.css'

StylesManager.applyTheme('defaultV2')

import { Survey } from 'survey-vue-ui'
import AuForm from '@/components/base-ui/AuForm'
import { axios } from '@/utils/request'

import { formConfig } from './config/form.config'

const json = {}
// 获取问卷项目表的json

export default {
  name: 'surveyjs-component',
  components: {
    Survey,
    AuForm,
  },
  async mounted() {
    const _this = this
    const data = await axios({
      url: '/survey/surProject/queryById',
      method: 'get',
      params: {
        id: this.projectId,
        pageNum: 1,
        pageSize: 10,
      },
    })
    let surveyJson = ''
    if (data.result.copy_survey) {
      surveyJson = data.result.copy_survey.jsonPreview
    } else {
      surveyJson = data.result.survey
    }
    // let surveyJson = data.result.jsonPreview
    // 初始化Survey
    this.survey = new Model(surveyJson)
    // 完成问卷填写后的回调函数
    this.survey.onComplete.add(async function (sender, options) {
      _this.loadingHandle()
      await axios({
        url: '/survey/surResult/saveAnswer',
        method: 'post',
        data: {
          surveyId: _this.surveyId,
          answer: JSON.stringify(sender.data),
          projectId: _this.projectId,
          user: _this.userInfo,
          result: sender.data,
        },
      }).then((res) => {
        console.log(res)
        if (res.code == 500) {
          _this.$message.destroy()
          _this.$message.warning('您已经提交过了请勿重复提交!')
        } else {
          _this.$message.destroy()
          _this.$message.success('提交成功')
        }
      })
    })
  },
  data() {
    var model = new Model(json)
    return {
      evaluatedId: '',
      evaluatorOptions: [],
      visibleInfo: false,
      formConfig,
      model: {},
      step: 0,
      visible: true,
      loading: false,
      surveyId: this.$route.query.creatorId,
      projectId: this.$route.query.projectId,
      projectType: this.$route.query.projectType,
      userInfo: {},
      survey: model,
    }
  },
  methods: {
    handleOk() {
      this.model.projectId = this.projectId
      console.log(this.model)
      // 校验表单
      const formInstance = this.$refs.form
      formInstance.$refs.formRef.validate(async (valid) => {
        if (valid) {
          const res = await axios({
            method: 'post',
            url: '/survey/surProject/judge',
            data: this.model,
          })
          if (res.code !== 200) {
            // 提示用户无权访问
            return this.$message.error('您无权访问该问卷')
          }
          // 保存用户信息
          this.userInfo = res.result
          this.evaluatorOptions = res.result.evaluator.map((item) => {
            return {
              value: item.id,
              label: item.userName,
            }
          })
          console.log(this.userInfo)

          if (this.projectType == 0) {
            this.visibleInfo = true
            this.visible = false
          } else {
            // 如果认证成功则显示问卷
            this.step = 1
            this.visible = false
          }
        }
      })
    },
    // 处理选择被评价人
    handleSelectChange(e) {
      this.evaluatedId = e
      console.log(this.evaluatedId)
    },
    // 处理确认选人
    handleOkSelect() {
      if (this.evaluatedId == '') {
        return this.$message.error('请选择被评价人')
      }
      this.step = 1
      this.visibleInfo = false
      console.log(this.evaluatedId)
    },
    loadingHandle() {
      const hide = this.$message.loading('提交中，请耐心等待..', 0)
      return hide
    },
  },
}
</script>

<style scoped lang="less">
@media (min-width: 1200px) {
  .container {
    width: 1170px;
  }
}
// @media (min-width: 992px) {
//   .container {
//     width: 970px;
//   }
// }
// @media (min-width: 768px) {
//   .container {
//     width: 750px;
//   }
// }
.container {
  padding-right: 15px;
  padding-left: 15px;
  margin-right: auto;
  margin-left: auto;
}
</style>
