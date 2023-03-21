<template>
  <div style="margin-top: 20px;">
    <a-tabs v-model="activeKey" @change="handleChangePanel">
      <!-- 题目列表 -->
      <a-tab-pane tab="题目列表" key="1">
        <a-spin :spinning="confirmLoading">
          <a-table
            ref="tableQuestionsRef"
            rowKey="id"
            :scroll="{ x: true }"
            :dataSource="dataSourceQuestions"
            :columns="tableConfig.questionColumns"
            :pagination="ipaginationQuestion"
            @change="handleTableQutionsChange"
          >
            <span slot="qid" slot-scope="text, records">
              <a-input v-if="records.edit" size="small" v-model="records.qid" />
              <span v-else> {{ records.qid }}</span>
            </span>
            <span slot="action" slot-scope="text, records">
              <a @click="handleEditQuestion(records)">设置选项</a>
              <!-- <a-divider type="vertical" />
              <a-button
                type="link"
                size="small"
                @click="handleSaveOption(records)"
                v-if="records.edit"
                style="color: #67c23a"
              >
                保存
              </a-button>
              <a-button type="link" size="small" @click="handleEditOption(records)" v-else>编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="text" size="small" @click="handleCancelSaveOption(records)" v-if="records.edit">
                取消
              </a-button> -->
            </span>
          </a-table>
        </a-spin>
      </a-tab-pane>
      <!-- 设置选项 -->
      <a-tab-pane tab="设置选项" key="2" :disabled="isDisabled">
        <a-table
          ref="tableOptionRef"
          rowKey="index"
          :scroll="{ x: true }"
          :dataSource="dataSourceOptions"
          :columns="tableConfig.answerColumns"
        >
          <span slot="basicScore" slot-scope="text, records">
            <a-input v-if="records.edit" size="small" v-model="records.basicScore" />
            <span v-else> {{ records.basicScore }}</span>
          </span>
          <span slot="action" slot-scope="text, records">
            <a-button
              type="link"
              size="small"
              @click="handleSaveOption(records)"
              v-if="records.edit"
              style="color: #67c23a"
            >
              保存
            </a-button>
            <a-button type="link" size="small" @click="handleEditOption(records)" v-else>编辑</a-button>
            <a-divider type="vertical" />
            <a-button type="text" size="small" @click="handleCancelSaveOption(records)" v-if="records.edit">
              取消
            </a-button>
          </span>
        </a-table>
      </a-tab-pane>
    </a-tabs>
    <div class="btns" style="margin-top: 20px;">
      <a-button type="primary" @click="nextStep">下一步</a-button>
      <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
    </div>
  </div>
</template>
<script>
import { axios } from '@/utils/request'
import { tableConfig } from './config/table.content'
import { getQuestionType } from '@/constant/survey'
export default {
  props: {
    projectType: {
      type: String,
      default: ''
    },
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tableConfig,
      //分数是否是编辑
      edit: false,
      isDisabled: true,
      confirmLoading: false,
      activeKey: '1',
      currentSurveyId: '',
      // 当前问题id
      currentQuestionId: '',
      // 选项
      dataSourceQuestions: [],
      // 设置分数
      dataSourceOptions: [],
      /* 分页参数 */
      ipaginationQuestion: {
        pageNum: 1,
        pageSize: 6,
        pageSizeOptions: ['6', '12', '18'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      url: {
        questionList: '/survey/surSurveyProject/question',
        optionList: '/survey/surSurveyProject/choice',
        saveOption: '/survey/surSurveyProject/setScore',
        getEmptySurvey: '/survey/surSurveyProject/projectId/'
      }
    }
  },
  mounted() {
    this.getEmptySurvey()
  },
  methods: {
    // 查询当前项目的问卷
    getEmptySurvey() {
      const _this = this
      this.confirmLoading = true
      axios({
        method: 'get',
        url: this.url.getEmptySurvey + this.projectId
      })
        .then(res => {
          if (res.result) {
            this.currentSurveyId = res.result.id
            this.getQuestionList({ id: this.currentSurveyId, pageNum: 1, pageSize: 6 })
            _this.confirmLoading = false
          }
          _this.confirmLoading = false
        })
        .catch(err => {
          this.$message.error(err.message)
          _this.confirmLoading = false
        })
    },
    // 获取问题列表
    async getQuestionList(data) {
      const { result: res } = await axios({
        method: 'post',
        url: this.url.questionList,
        data
      })
      this.dataSourceQuestions = res.records.map(item => {
        // 获取类型
        const type = getQuestionType(item.typeId)
        return {
          ...item,
          typeId: type
        }
      })
      this.ipaginationQuestion.total = res.total
    },
    // 获取问题选项
    async getOptionList(data) {
      // 先清空
      this.dataSourceOptions = []
      this.dataSourceBindings = []
      const { result: res } = await axios({
        method: 'post',
        url: this.url.optionList,
        data
      })

      // 将res对象变数组对象 (后端数据格式改动，废弃)
      // Object.keys(res).forEach(key => {
      //   this.dataSourceOptions.push({ content: key, score: res[key], edit: false })
      // })
      this.dataSourceOptions = res.map((item, index) => {
        return {
          ...item,
          index,
          edit: false
        }
      })
    },
    // 设置选项
    handleEditQuestion(records) {
      this.currentQuestionId = records.id
      this.getOptionList({ questionId: records.id })
      this.isDisabled = false
      this.activeKey = '2'
    },
    // 面板切换
    handleChangePanel(activeKey) {
      console.log(activeKey)
      if (activeKey === '1') {
        this.isDisabled = true
      }
      this.activeKey = activeKey
    },
    // 处理问题表格变化
    handleTableQutionsChange(pagination) {
      const pager = { ...this.ipaginationQuestion }
      pager.pageNum = pagination.current
      pager.pageSize = pagination.pageSize
      this.ipaginationQuestion = pager
      this.getQuestionList({ pageNum: pagination.current, pageSize: pagination.pageSize, id: this.currentSurveyId })
    },
    // 处理编辑分数
    handleEditOption(records) {
      // 将选项设置为编辑状态    this.modeTitle = '保存'
      records.edit = true
      console.log('设置分数')
    },
    // 处理保存分数
    async handleSaveOption(records) {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      // 判断是否有分数,并且分数的类型是否是数字
      if (records.basicScore == null || isNaN(records.basicScore)) {
        this.$message.error('分数只能是数字,请输入正确的分数')
        return
      }
      // 将选项设置为非编辑状态
      records.edit = false
      const score = []
      this.dataSourceOptions.forEach(item => {
        score.push(item.basicScore)
      })
      const res = await axios({
        method: 'post',
        url: this.url.saveOption,
        data: {
          id: records.id,
          score
        }
      })
      if (res.code !== 200) {
        return this.$message.error('保存失败,请稍后再试')
      }
      this.$message.success('保存成功')
    },
    // 取消编辑
    handleCancelSaveOption(record) {
      record.edit = false
    },
    nextStep() {
      if (this.projectType === '360度评估') {
        this.$emit('nextStep360')
      } else {
        this.$emit('nextStep')
      }
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style lang=""></style>
