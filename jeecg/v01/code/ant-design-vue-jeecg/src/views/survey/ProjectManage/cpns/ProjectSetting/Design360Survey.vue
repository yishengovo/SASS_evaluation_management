<template>
  <div style="margin-top: 20px;">
    <a-spin :spinning="confirmLoading">
      <a-button type="primary" style="margin: 30px 0 20px 0" @click="showCreateSurvey" :disabled="isDisabled"
        >新建空白问卷</a-button
      >
      <a-table
        :dataSource="dataSource"
        :columns="tableConfig.surveySelectColumns"
        :row-key="row => row.id"
        :pagination="false"
        v-if="dataSource.length > 0"
        class="j-table-force-nowrap"
      >
        <template #bodyCell="{ column, text, record }"></template>
        <!-- 操作按钮区域 -->
        <template slot="action" slot-scope="text, record">
          <a-button type="primary" @click="designNewSurvey(record)">设计此问卷</a-button>
        </template>
      </a-table>

      <div class="btns" style="margin-top: 20px;">
        <a-button type="primary" @click="nextStep">下一步</a-button>
        <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
      </div>
      <!-- 新建空白问卷 -->
      <a-modal
        title="新建空白问卷"
        :visible="modal.visible"
        :width="modal.width"
        @ok="createSurvey"
        @cancel="modal.visible = false"
      >
        <a-form-model :model="modal.form" :rules="modal.rules" ref="form">
          <a-form-model-item label="问卷名称" prop="name">
            <a-input v-model="modal.form.name" placeholder="请输入问卷名称" />
          </a-form-model-item>
          <a-form-model-item label="问卷描述" prop="description">
            <a-input v-model="modal.form.description" placeholder="请输入问卷描述" />
          </a-form-model-item>
        </a-form-model>
      </a-modal>
      <!-- 设计问卷 -->
      <j-modal
        title="设计问卷"
        :visible="visible"
        :fullscreen="true"
        :footer="null"
        @cancel="handleCancel"
        cancelText="关闭"
      >
        <SurveyCreator :surveyJson="defaultJson" :surveyId="surveyId"></SurveyCreator>
      </j-modal>
    </a-spin>
  </div>
</template>
<script>
export default {
  data() {
    return {}
  },
  methods: {
    nextStep() {
      this.$emit('nextStep360')
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style lang=""></style>
