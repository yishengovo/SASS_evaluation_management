<template>
  <div>
    <a-spin :spinning="confirmLoading">
      <a-button type="primary" style="margin: 30px 0 20px 0" @click="showCreateSurvey" :disabled="true"
        >新建空白问卷</a-button
      >
      <a-table
        :dataSource="dataSource"
        :columns="tableConfig.surveySelectColumns"
        :row-key="row => row.id"
        :pagination="mPagination"
        v-if="dataSource.length > 0"
        class="j-table-force-nowrap"
      >
        <template #bodyCell="{ column, text, record }"></template>
        <!-- 操作按钮区域 -->
        <template slot="action" slot-scope="text, record">
          <a-button type="danger" @click="deleteChoseSurvey(record)">删除</a-button> &nbsp;&nbsp;
          <a-button type="primary" @click="designNewSurvey(record)">设计此问卷</a-button>
        </template>
      </a-table>
      <div class="example" v-if="projectType !== '360度评估'">
        <!-- :row-selection="{ selectedRowKeys: selectedSurveyRowKeys, onChange: onSelectSurveyChange }" -->
        <div style="margin: 20px auto 10px;font-size: 18px;font-weight: 700;">从问卷模板中选择：</div>
        <a-table
          ref="table"
          rowKey="id"
          :dataSource="dataSourceSurvey"
          :columns="tableConfig.surveySelectColumns"
          :pagination="ipagination"
          @change="
            (pagination, filters, sorter, { currentDataSource }) => {
              return handleTableChanged(pagination, filters, sorter, { currentDataSource }, 'survey')
            }
          "
          class="j-table-force-nowrap"
        >
          <!-- 操作按钮区域 -->
          <template slot="action" slot-scope="text, record">
            <a-button type="primary" @click="designSelectSurvey(record)">选择此问卷</a-button>
          </template>
        </a-table>
      </div>
      <div style="margin-top: 20px;">
        <a-button type="primary" @click="nextStep">下一步</a-button>
        <a-button style="margin-left: 8px;" @click="prevStep">上一步</a-button>
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
import SurveyCreator from '../SurveyCreator/SurveyCreator.vue'

import { axios } from '@/utils/request'
import { tableConfig } from './config/table.content'

export default {
  components: {
    SurveyCreator
  },
  props: {
    projectType: {
      type: String,
      default: ''
    },
    projectId: {
      type: String,
      default: ''
    },
    isUpAndDown: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false,
      // 问卷的默认json
      defaultJson: {},
      dataSource: [],
      surveyId: '',
      isDisabled: false,
      modal: {
        visible: false,
        width: '600px',
        form: {
          name: '',
          description: ''
        },
        rules: {
          name: [{ required: true, message: '请输入问卷名称', trigger: 'blur' }],
          description: [{ required: true, message: '请输入问卷描述', trigger: 'blur' }]
        }
      },
      currentSurveyId: '',
      tableConfig,
      wrapper: '',
      dataSourceSurvey: [],
      model: {},
      // 选择的问卷rowkey
      selectedSurveyRowKeys: [],
      /* 分页参数 */
      ipagination: {
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
      mPagination: {
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
      confirmLoading: false,
      url: {
        list: '/survey/surProject/surveyByType',
        queryById: '/survey/surProject/queryById',
        // saveSelectSurvey: '/survey/surProject/setSurvey',
        saveSelectSurvey:'/survey/surProject/setSurvey',
        addEmptySurvey: '/survey/surSurveyProject/add',
        // getEmptySurvey: '/survey/surSurveyProject/projectId/'
        deleteSelectSurvey:'survey/surProject/deleteSelectSurvey'
      }
    }
  },
  mounted() {
    this.callback()
    // this.getEmptySurvey()
    this.getSurveyList({ pageNum: 1, pageSize: 6, type: this.projectType })
  },
  methods: {
    // 选择问卷前的回调
    async callback() {
      const { result: res } = await axios({
        method: 'get',
        url: this.url.queryById,
        params: {
          id: this.projectId,
          pageNum: 1,
          pageSize: 10
        }
      })
      this.selectedSurveyRowKeys = res.rowKeys || []
      if (res.surveyProjectList) {
        this.currentSurveyId = res.surveyProjectList.id
        // this.dataSource = [{ surName: res.surveyProjectList.surName, surContent: res.surveyProjectList.surContent }]
        this.dataSource = res.surveyProjectList
        // this.defaultJson = res.survey.jsonPreview
        console.log("=====",this.dataSource)
      }
      console.log("=====",res)
    },
    // 获取问卷列表
    async getSurveyList(data) {
      this.confirmLoading = true
      const { result: res } = await axios({
        method: 'post',
        url: this.url.list,
        data
      })
      this.dataSourceSurvey = res.records.map((item, index) => {
        return {
          ...item,
          key: item.id
        }
      })
      this.ipagination.total = res.total
      this.confirmLoading = false
    },
    // 处理分页
    handleTableChanged(pagination, filters, sorter, { currentDataSource }, type) {
      if (type === 'survey') {
        const pager = { ...this.pagination }
        pager.pageNum = pagination.current
        this.pagination = pager
        this.getSurveyList({ pageNum: pagination.current, pageSize: pagination.pageSize, type: this.projectType })
      } else if (type === 'deparntment') {
        const pager = { ...this.ipaginationDeparntment }
        pager.pageNum = pagination.current
        this.ipaginationDeparntment = pager
        this.getDeparntmentList({
          id: this.currentDeparntmentId,
          pageNum: pagination.current,
          pageSize: pagination.pageSize
        })
      }
    },
    // onSelectSurveyChange(selectedRowKeys, selectedRows) {
    //   this.wrapper = ''
    //   this.selectedSurveyRowKeys = selectedRowKeys
    //   if (selectedRows.length > 0) {
    //     this.currentSurveyId = selectedRows[0].id || ''
    //   } else {
    //     this.currentSurveyId = ''
    //   }
    //   // 只能同时选择一个问卷
    //   if (selectedRowKeys.length > 1) {
    //     this.$message.error('只能同时选择一个问卷')
    //     this.selectedSurveyRowKeys = []
    //     this.selectedSurveyRowKeys.push(selectedRowKeys[0])
    //     this.wrapper = selectedRows[0].surName
    //     this.model.surveyId = selectedRows[0].id
    //     return
    //   } else if (selectedRows.length > 0) {
    //     this.wrapper = selectedRows[0].surName
    //     this.model.surveyId = selectedRows[0].id
    //   }
    // },
    deleteChoseSurvey(record){
      const _this = this
      this.$confirm({
          title: '确定要删除该问卷吗?',
          onOk() {
            _this.loadingDeleteHandle()
            axios({
              method: 'delete',
              url: _this.url.deleteSelectSurvey,
              params: {
                surveyId: record.id
              }
            })
              .then(res => { 
                _this.$message.destroy()
                _this.$message.success('删除成功')
                // _this.getEmptySurvey()
                // this.$message.success('保存成功')
                _this.callback()
              })
              .catch(err => {
                _this.$message.error('选择问卷失败，请稍后重试')
              })
          },
          onCancel() {}
        })
      console.log(record)
    },
    showCreateSurvey() {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      this.modal.visible = true
    },
    // 新建空白问卷
    createSurvey() {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      console.log('新建空白问卷')
      // 校验表单
      this.$refs.form.validate(valid => {
        if (valid) {
          // this.saveSurvey()
          // 调接口
          axios({
            method: 'post',
            url: this.url.addEmptySurvey,
            data: {
              id: this.projectId,
              type: this.projectType,
              surName: this.modal.form.name,
              surContent: this.modal.form.description
            }
          })
            .then(res => {
              this.modal.visible = false
              // this.getEmptySurvey()
            })
            .catch(err => {
              this.$message.error(err.message)
            })
          this.isDisabled = true
          this.modal.visible = false
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 查询空白问卷
    // getEmptySurvey() {
    //   this.dataSource = []
    //   axios({
    //     method: 'get',
    //     url: this.url.getEmptySurvey + this.projectId
    //   })
    //     .then(res => {
    //       console.log(res)
    //       if (res.result) {
    //         this.dataSource.push(res.result)
    //         this.defaultJson = res.result.jsonPreview
    //         this.surveyId = res.result.id
    //         this.isDisabled = true
    //       } else {
    //         this.dataSource = []
    //       }
    //     })
    //     .catch(err => {
    //       this.$message.error(err.message)
    //     })
    // },
    // 设计空白问卷
    designNewSurvey(record) {
      console.log(record)
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      this.defaultJson = record.jsonPreview
      console.log('设计空白问卷')
      this.callback()
      this.visible = true
    },

    handleCancel() {
      this.visible = false
    },
    // 处理设计选择的问卷
    // designSelectSurvey(record) {
    //   const _this = this
    //   if (this.$store.state.project.isPublished) {
    //     return this.$message.warning('该项目已经发布，不能修改该项目信息。')
    //   } else {
    //     this.$confirm({
    //       title: '确定要选择该问卷吗?',
    //       content: '选择后将会覆盖已有的问卷',
    //       onOk() {
    //         _this.loadingHandle()
    //         axios({
    //           method: 'post',
    //           url: _this.url.saveSelectSurvey,
    //           data: {
    //             id: _this.projectId,
    //             rowKeys: [],
    //             surveyId: record.id
    //           }
    //         })
    //           .then(res => {
    //             _this.$message.destroy()
    //             _this.$message.success('复制成功')
    //             _this.getEmptySurvey()
    //             // this.$message.success('保存成功')
    //           })
    //           .catch(err => {
    //             _this.$message.error('选择问卷失败，请稍后重试')
    //           })
    //       },
    //       onCancel() {}
    //     })
    //   }

    //   console.log(record)

    //   console.log(record)
    // },
  // 处理设计选择的问卷
    designSelectSurvey(record) {
      const _this = this
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      } else {
        this.$confirm({
          title: '确定要选择该问卷吗?',
          onOk() {
            _this.loadingHandle()
            axios({
              method: 'post',
              url: _this.url.saveSelectSurvey,
              data: {
                id: _this.projectId,
                rowKeys: [],
                surveyId: record.id
              }
            })
              .then(res => { 
                _this.$message.destroy()
                _this.$message.success('复制成功')
                // _this.getEmptySurvey()
                // this.$message.success('保存成功')
                _this.callback()
              })
              .catch(err => {
                _this.$message.error('选择问卷失败，请稍后重试')
              })
          },
          onCancel() {}
        })
      }

      console.log(record)

      console.log(record)
    },

    loadingHandle() {
      const hide = this.$message.loading('复制问卷中，请耐心等待..', 0)
      return hide
    },
    loadingDeleteHandle() {
      const hide = this.$message.loading('删除问卷中，请耐心等待..', 0)
      return hide
    },
    // 下一步
    nextStep() {
      if (this.selectedSurveyRowKeys.length === 0) {
        if (this.projectType === '360度评估') {
          this.$emit('nextStep360')
        } else {
          this.$emit('nextStep')
        }
      } else {
        if (this.projectType === '360度评估') {
          this.$emit('nextStep360')
        } else {
          this.$emit('nextStep')
        }
      }
    },
    // 上一步
    prevStep() {
      this.$emit('prevStep', this.isUpAndDown)
    }
  }
}
</script>
<style lang=""></style>
