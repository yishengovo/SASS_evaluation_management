<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <au-form v-bind="formConfig" v-model="model" slot="detail" ref="form"></au-form>
      <!-- <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="唯一标识" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fid">
              <a-input v-model="model.fid" placeholder="请输入唯一标识"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="调查问卷" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="surveyUid">
              <a-input v-model="model.surveyUid" placeholder="请输入调查问卷"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="问题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="questionUid">
              <a-input v-model="model.questionUid" placeholder="请输入问题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="矩阵标题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="titleUid">
              <a-input v-model="model.titleUid" placeholder="请输入矩阵标题"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="答案" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="answer">
              <a-input v-model="model.answer" placeholder="请输入答案"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="其他答案" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="answerOther">
              <a-input v-model="model.answerOther" placeholder="请输入其他答案"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="填写时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fillDate">
              <a-input v-model="model.fillDate" placeholder="请输入填写时间"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="用户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userUid">
              <a-input v-model="model.userUid" placeholder="请输入用户"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="员工" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="empUid">
              <a-input v-model="model.empUid" placeholder="请输入员工"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="多选答案" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="answers">
              <a-input v-model="model.answers" placeholder="请输入多选答案"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="问卷人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="responseUid">
              <a-input v-model="model.responseUid" placeholder="请输入问卷人"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="组织" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="orgUid">
              <a-input v-model="model.orgUid" placeholder="请输入组织"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="所属租户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="tenantUid">
              <a-input v-model="model.tenantUid" placeholder="请输入所属租户"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="有效开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="enableDate">
              <a-input v-model="model.enableDate" placeholder="请输入有效开始时间"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="有效截止时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="disableDate">
              <a-input v-model="model.disableDate" placeholder="请输入有效截止时间"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="删除标记" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="dr">
              <a-input-number v-model="model.dr" placeholder="请输入删除标记" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="时间戳" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="ts">
              <a-input-number v-model="model.ts" placeholder="请输入时间戳" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="创建时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createName">
              <a-input v-model="model.createName" placeholder="请输入创建时间"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="更新人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createDate">
              <a-input v-model="model.createDate" placeholder="请输入更新人"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="更新人名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateName">
              <a-input v-model="model.updateName" placeholder="请输入更新人名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="更新时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateDate">
              <a-input v-model="model.updateDate" placeholder="请输入更新时间"  ></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model> -->
    </j-form-container>
  </a-spin>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import { validateDuplicateValue } from '@/utils/util'
import AuForm from '@/components/base-ui/AuForm'

import { formConfig } from './config/form.config'
export default {
  name: 'SurResultForm',
  components: { AuForm },
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  data() {
    return {
      formConfig,
      model: {},
      confirmLoading: false,
      url: {
        add: '/survey/surResult/add',
        edit: '/survey/surResult/edit',
        queryById: '/survey/surResult/queryById'
      }
    }
  },
  computed: {
    formDisabled() {
      return this.disabled
    }
  },
  created() {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model))
  },
  methods: {
    add() {
      this.edit(this.modelDefault)
    },
    edit(record) {
      for (const item of this.formConfig.formItems) {
        this.model[`${item.field}`] = record[`${item.field}`]
      }
      this.$refs.form.formData = this.model
      console.log(this.model)
    },
    submitForm() {
      const that = this
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          httpAction(httpurl, this.model, method)
            .then(res => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    }
  }
}
</script>
