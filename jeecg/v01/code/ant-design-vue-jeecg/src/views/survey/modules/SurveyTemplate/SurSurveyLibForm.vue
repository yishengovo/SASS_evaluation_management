<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <au-form ref="form" v-bind="formConfig" v-model="model" slot="detail"></au-form>
    </j-form-container>
  </a-spin>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import { validateDuplicateValue } from '@/utils/util'
import AuForm from '@/components/base-ui/AuForm'
import { formConfig } from './config/form.config'

export default {
  name: 'SurSurveyLibForm',
  components: {
    AuForm
  },
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
        add: '/survey/surSurveyLib/add',
        edit: '/survey/surSurveyLib/edit',
        queryById: '/survey/surSurveyLib/queryById'
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
      this.model = Object.assign({}, record)
      this.$refs.form.formData = this.model
      this.visible = true
    },
    submitForm() {
      const that = this
      const formInstance = this.$refs.form
      // 触发表单验证
      formInstance.$refs.formRef.validate(valid => {
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
