<!--
 * @Author: August-Rushme
 * @Date: 2022-07-11 13:54:15
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-09 16:51:49
 * @FilePath: \ant-design-vue-jeecg\src\components\base-ui\AuForm\src\au-form.vue
 * @Description: 
 * 
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved. 
-->
<template>
  <div class="au-form">
    <a-form-model :model="formData" ref="formRef">
      <a-row>
        <template v-for="item in formItems">
          <a-col :span="24">
            <a-form-model-item
              :label="item.label"
              :labelCol="item.labelCol"
              :wrapperCol="item.wrapperCol"
              :prop="item.field"
              :rules="item.rules"
            >
              <a-input
                v-if="item.type === 'input'"
                v-model="formData[`${item.field}`]"
                :placeholder="item.placeholder"
                v-bind="item.otherOptions"
              ></a-input>
              <!-- 选择框 -->
              <a-select
                v-if="item.type === 'select' && item.mode !== 'multiple'"
                v-model="formData[`${item.field}`]"
                :placeholder="item.placeholder"
                :options="item.options"
                :disabled="isDisabled"
              >
                <a-select-option
                  v-for="(option, i) in item.options"
                  :value="option.value"
                  :key="(i + 9).toString(36) + i"
                  >{{ option.label }}</a-select-option
                >
              </a-select>
              <a-select
                v-if="item.type === 'select' && item.mode === 'multiple'"
                :default-value="formData[`${item.field}`]"
                :placeholder="item.placeholder"
                :mode="item.mode"
                :options="item.options"
                style="width: 100%"
                @change="handleChange"
              >
                <a-select-option
                  v-for="(option, i) in item.options"
                  :value="option.value"
                  :key="(i + 9).toString(36) + i"
                  >{{ option.label }}</a-select-option
                >
              </a-select>
              <!-- 文本域 -->
              <a-textarea
                v-if="item.type === 'textarea'"
                :placeholder="item.placeholder"
                v-model="formData[`${item.field}`]"
                v-bind="item.otherOptions"
              />

              <!-- 开关 -->
              <a-switch
                v-if="item.type === 'switch'"
                v-model="formData[`${item.field}`]"
                :checked-children="item.openText"
                :un-checked-children="item.closeText"
                defaultChecked
                @change="handleChangeSwitch"
              />
            </a-form-model-item>
          </a-col>
        </template>
        <slot></slot>
      </a-row>
    </a-form-model>
  </div>
</template>
<script>
export default {
  model: {
    prop: 'modelValue',
    event: 'update:modelValue',
  },
  props: {
    // 表单数据
    modelValue: {
      type: Object,
      default: () => ({}),
    },
    formItems: {
      type: Array,
      default: () => [],
    },
    // 是否禁用选择组件
    isDisabled: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      formData: { ...this.modelValue },
    }
  },
  mounted() {
    // console.log(this.formData)
  },
  // 监听formData变化，给父组件发送事件
  watch: {
    formData: {
      handler(val) {
        console.log(val)
        this.$emit('update:modelValue', val)
      },
      deep: true,
    },
  },
  methods: {
    handleChange(value) {
      this.formData.tenantIdList = value
    },
    handleChangeSwitch(value) {
      this.formData = { ...this.formData, isUse: value }
    },
  },
}
</script>
<style lang="less" scoped></style>
