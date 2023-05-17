<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <au-form v-bind="formConfig" v-model="model" slot="detail" ref="form">
        <!-- <template #formItem>
          <a-col :span="24">
            <span>
              可见租户：
            </span>
            <a-select
              mode="multiple"
              placeholder="请选择租户"
              :default-value="['a1', 'b2']"
              size="Large"
              style="width: 200px"
              @change="handleChange"
              @popupScroll="popupScroll"
            >
              <a-select-option v-for="i in 25" :key="(i + 9).toString(36) + i">
                {{ (i + 9).toString(36) + i }}
              </a-select-option>
            </a-select>
          </a-col>
        </template> -->
        <div class="ant-col ant-col-24">
          <div class="ant-row ant-form-item">
            <div class="ant-col ant-col-xs-24 ant-col-sm-5 ant-form-item-label">
              <label title="标签选择">标签选择</label>
            </div>
            <div class="ant-col ant-col-xs-24 ant-col-sm-16">
              <a-checkbox-group v-model="selectedTags" @change="onChange">
                <a-col :span="6" v-for="item in tagsInfos" :key="item.value">
                  <a-checkbox :value="item.value">{{ item.label }}</a-checkbox>
                </a-col>
              </a-checkbox-group>
            </div>
          </div>
        </div>
      </au-form>
    </j-form-container>
  </a-spin>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import AuForm from '@/components/base-ui/AuForm'
import { addFormConfig, editFormConfig } from './config/form.config'
import { getTenant } from '../../../../api/survey'
import { getTag } from '@/api/tag.js'

export default {
  name: 'SurveyForm',
  components: { AuForm },
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false,
    },
  },
  data() {
    return {
      tagsInfos: [], // 存储所有启用标签
      selectedTags: [], // 该问卷已有的标签
      ischecked: true,
      formConfig: {},
      model: {},
      confirmLoading: false,
      tenant: [],
      url: {
        add: '/survey/survey/add',
        edit: '/survey/survey/edit',
        queryById: '/survey/survey/queryById',
      },
    }
  },
  computed: {
    addFormConfigRef() {
      const tenanIdListItem = addFormConfig.formItems.find((item) => item.field === 'tenantIdList')
      tenanIdListItem.options = []
      this.tenant.forEach((item, i) => {
        tenanIdListItem.options.push({
          label: item.name,
          value: item.id,
        })
      })

      return addFormConfig
    },
    editFormConfigRef() {
      const tenanIdListItem = editFormConfig.formItems.find((item) => item.field === 'tenantIdList')
      tenanIdListItem.options = []
      this.tenant.forEach((item, i) => {
        tenanIdListItem.options.push({
          label: item.name,
          value: item.id,
        })
      })

      return editFormConfig
    },
    formDisabled() {
      return this.disabled
    },
  },
  watch: {
    formConfig: {
      handler(val) {
        console.log(val)
        // this.formConfig = val
      },
    },
  },
  created() {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model))
  },
  mounted() {
    getTenant().then((data) => {
      if (data.code !== 200) {
        return this.$message.error('获取租户数据失败')
      }
      this.tenant = data.result
      this.formConfig = this.addFormConfigRef
    })
  },
  methods: {
    onChange(checkedValues) {
      this.model.tagRowkeys = checkedValues
    },
    add() {
      this.edit(this.modelDefault)
    },
    edit(record) {
      this.selectedTags = record.tagRowkeys
      this.queryTagList({ pageNum: 1, pageSize: 99999, tagName: '' })
      // 判断是编辑还是新增
      if (Object.keys(record).length) {
        this.formConfig = this.editFormConfigRef
        console.log(1)
        this.model.id = record.id
        for (const item of this.formConfig.formItems) {
          this.model[`${item.field}`] = record[`${item.field}`]
        }
        console.log(this.checkedValues)

        console.log(this.model)
      } else {
        this.formConfig = this.addFormConfigRef
      }

      // 付给model的值，会覆盖原始值
      this.$refs.form.formData = this.model
    },
    submitForm() {
      const that = this
      // 触发子组件的表单验证
      const formInstance = this.$refs.form
      formInstance.$refs.formRef.validate((valid) => {
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
            .then((res) => {
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
    },

    // 查询所有标签
    async queryTagList(params) {
      let _this = this
      const res = await getTag(params)
      if (res.code === 200) {
        res.result.records.forEach(function (item) {
          if (item.status === 1) {
            let tagsInfo = {
              label: '',
              value: '',
            }
            tagsInfo.label = item.tagName
            tagsInfo.value = item.id
            _this.tagsInfos.push(tagsInfo)
          }
        })
        // console.log('_this.tagsInfo===============', _this.tagsInfo)
      }
    },
  },
}
</script>

<style scoped>
.ant-checkbox-group {
  display: block;
  margin-top: 10px;
}
</style>
