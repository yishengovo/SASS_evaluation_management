<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <template slot="detail">
        <au-form v-bind="formConfig" v-model="model" ref="form" :isDisabled="isDisabled">
          <!-- <template #default>
            <a-col :span="24">
              <a-form-model-item label="问卷" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="surveyName">
                <div style="display: flex">
                  <a-input
                    v-model="model.surveyName"
                    style="margin-right: 10px"
                    :read-only="true"
                    placeholder="请选择问卷"
                  ></a-input>
                  <a-button @click="handleChooseSurvey" :disabled="isDisabledBtn">选择问卷</a-button>
                </div>
              </a-form-model-item>
            </a-col>
          </template> -->
        </au-form>

        <!-- 子表单区域 -->
        <!-- <a-tabs v-model="activeKey">
          <a-tab-pane tab="用户信息" key="1" :forceRender="true">
            <j-vxe-table
              ref="editableTable1"
              toolbar
              row-number
              keep-source
              :height="300"
              :loading="VxeTable.loading"
              :dataSource="VxeTable.dataSource"
              :columns="VxeTable.columns"
              style="margin-top: 8px;"
            />
          </a-tab-pane>
          <a-tab-pane tab="部门人员" key="2" :forceRender="true">
            部门选择：
            <a-cascader
              :fieldNames="{ label: 'departName', value: 'id', children: 'children' }"
              :options="departmentOptions"
              placeholder="请选择部门"
              @change="onSelectChange"
              style="margin-bottom: 20px;"
            />
            <a-table
              ref="tableDepartment"
              rowKey="id"
              :dataSource="dataSourceDepartment"
              :columns="tableConfig.deparntmentUser"
              :row-selection="{
                selectedRowKeys: selectedDeparntmentRowKeys,
                onChange: onSelectDeparntmentChange,
                getCheckboxProps: getCheckboxDeparntmentProps
              }"
              :pagination="ipaginationDeparntment"
              @change="
                (pagination, filters, sorter, { currentDataSource }) => {
                  return handleTableChanged(pagination, filters, sorter, { currentDataSource }, 'deparntment')
                }
              "
              class="j-table-force-nowrap"
            >
              <template #bodyCell="{ column, text, record }"></template>
            </a-table>
          </a-tab-pane>
        </a-tabs> -->
      </template>
    </j-form-container>
    <!-- 问卷选择 -->
    <j-modal
      :title="title"
      :width="widthSurvey"
      :visible="visibleSurvey"
      switchFullscreen
      @ok="handleSurveyOk"
      @cancel="handleCancel"
      cancelText="关闭"
    >
      <a-table
        ref="table"
        :dataSource="dataSourceSurvey"
        :columns="tableConfig.columsSurvey"
        :row-selection="{ selectedRowKeys: selectedSurveyRowKeys, onChange: onSelectSurveyChange }"
        :pagination="ipagination"
        @change="
          (pagination, filters, sorter, { currentDataSource }) => {
            return handleTableChanged(pagination, filters, sorter, { currentDataSource }, 'survey')
          }
        "
        class="j-table-force-nowrap"
      >
        <template #bodyCell="{ column, text, record }"></template>
      </a-table>
    </j-modal>
  </a-spin>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import { VALIDATE_FAILED, getRefPromise, validateFormModelAndTables } from '@/components/jeecg/JVxeTable/utils/vxeUtils'
import { axios } from '@/utils/request'
import AuForm from '@/components/base-ui/AuForm'
import { addFormConfig, editFormConfig } from './config/form.config'
import { tableConfig, VxeTable } from './config/table.config'
export default {
  name: 'SurProjectForm',
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
      // 是否禁用选择问卷类型选择
      isDisabled: false,
      editCallBackData: '',
      // 当前选中部门的id
      currentDeparntmentId: '',
      // 是否禁用选择问卷按钮
      isDisabledBtn: false,
      dataSourceDepartment: [],
      departmentOptions: [],
      departmentList: [],
      selectDeparntmentPeople: [],
      // 选择的用户数
      selectNumber: 0,
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
      ipaginationDeparntment: {
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
      activeKey: '1',
      formConfig: {},
      VxeTable,
      wrapper: '',
      // selectedUserRowKeys: [],
      // 选择的问卷rowkey
      selectedSurveyRowKeys: [],
      // 选择部门人的rowkey
      selectedDeparntmentRowKeys: [],
      title: '',
      // widthUser: 900,
      // visibleUser: false,
      widthSurvey: 900,
      visibleSurvey: false,
      model: {},
      // dataSource: [],
      dataSourceSurvey: [],
      tableConfig,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      url: {
        list: '/survey/surProject/surList',
        add: '/survey/surProject/add',
        edit: '/survey/surProject/edit',
        queryById: '/survey/surProject/queryById',
        departmentList: 'survey/depart/queryTreeList',
        loadUser: '/survey/surUser/queryByDep'
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
  mounted() {
    // this.getUserList()
    // this.getSurveyList({ pageNum: 1, pageSize: 6 })
    this.getDepartmentList()
  },
  methods: {
    // 处理分页
    handleTableChanged(pagination, filters, sorter, { currentDataSource }, type) {
      console.log(pagination)
      console.log(type)
      if (type === 'survey') {
        const pager = { ...this.pagination }
        pager.pageNum = pagination.current
        this.pagination = pager
        this.getSurveyList({ pageNum: pagination.current, pageSize: pagination.pageSize })
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
    // 选择问卷相关
    handleChooseSurvey() {
      this.visibleSurvey = true
    },
    handleSurveyOk() {
      this.model.surveyName = this.wrapper
      this.visibleSurvey = false
    },
    onSelectSurveyChange(selectedRowKeys, selectedRows) {
      this.wrapper = ''
      this.selectedSurveyRowKeys = selectedRowKeys
      console.log(this.selectedSurveyRowKeys)
      // 只能同时选择一个问卷
      if (selectedRowKeys.length > 1) {
        this.$message.error('只能同时选择一个问卷')
        this.selectedSurveyRowKeys = []
        this.selectedSurveyRowKeys.push(selectedRowKeys[0])
        this.wrapper = selectedRows[0].surName
        this.model.surveyId = selectedRows[0].id
        return
      } else if (selectedRows.length > 0) {
        this.wrapper = selectedRows[0].surName
        this.model.surveyId = selectedRows[0].id
      }
    },
    // 获取问卷列表
    // async getSurveyList(data) {
    //   this.confirmLoading = true
    //   const { result: res } = await axios({
    //     method: 'post',
    //     url: this.url.list,
    //     data
    //   })
    //   this.dataSourceSurvey = res.records.map((item, index) => {
    //     return {
    //       ...item,
    //       key: item.id
    //     }
    //   })
    //   this.ipagination.total = res.total
    //   this.confirmLoading = false
    // },
    // 获取部门
    async getDepartmentList() {
      const { result: res } = await axios({
        method: 'get',
        url: this.url.departmentList
      })

      // 递归res，如果children为null将children转换为数组
      const _recurseChildren = departmentList => {
        for (const item of departmentList) {
          if (item.children) {
            _recurseChildren(item.children)
          } else {
            item.children = []
          }
        }
      }
      _recurseChildren(res)
      this.departmentOptions = res
    },
    // 获取部门里面的人
    async getUserList(data) {
      const { result: res } = await axios({
        method: 'post',
        url: this.url.loadUser,
        data
      })
      this.dataSourceDepartment = res.records
      this.ipaginationDeparntment.total = res.total
      console.log(this.dataSourceDepartment)
    },
    onSelectChange(value) {
      this.currentDeparntmentId = value[value.length - 1]
      this.getUserList({ id: value[value.length - 1], pageNum: 1, pageSize: 6 })
    },
    onSelectDeparntmentChange(selectedRowKeys, selectedRows) {
      this.selectedDeparntmentRowKeys = selectedRowKeys
      this.selectDeparntmentPeople = selectedRows
    },
    getCheckboxDeparntmentProps(row) {
      if (this.editCallBackData !== null) {
        return {
          props: {
            disabled: this.editCallBackData.depUser.records.some(item => item.id === row.id)
          }
        }
      } else {
        return {
          props: {
            disabled: false
          }
        }
      }
    },
    // 关闭
    close() {
      // this.visibleUser = false
      this.visibleSurvey = false
      // this.getSurveyList({ pageNum: 1, pageSize: 6 })
      this.getAllTable().then(editableTables => {
        this.VxeTable.dataSource = []
      })
    },
    handleOk() {
      const formInstance = this.$refs.form
      formInstance.$refs.formRef.validate(valid => {
        if (valid) {
          this.requestAddOrEdit()
        } else {
          return false
        }
      })
      // this.model.userList = ''
      // this.model.userList = this.wrapper.substring(0, this.wrapper.length - 1)
      // this.validateFields()
      this.visibleUser = false
    },

    handleCancel() {
      this.close()
    },
    // 获取所有的editableTable实例
    getAllTable() {
      return Promise.all([getRefPromise(this, 'editableTable1')])
    },
    add() {
      this.isDisabledBtn = false
      // 默认新增一条数据
      this.getAllTable()
        .then(editableTables => {
          this.VxeTable.dataSource = []
          // editableTables[0].add()
          //editableTables[1].add()
        })
        .catch(err => {
          console.log(err)
        })
      this.edit(this.modelDefault)
    },
    // 编辑前的回调
    async beforeEdit(row) {
      const res = await axios({
        method: 'get',
        url: this.url.queryById,
        params: {
          id: row.id,
          pageNum: 1,
          pageSize: 10
        }
      })
      return res.result
    },
    async edit(record) {
      if (record.isPublish) {
        this.isDisabled = true
      }
      console.log(record)
      this.VxeTable.dataSource = []
      // const res = await this.beforeEdit(record)
      // this.editCallBackData = res
      this.activeKey = '1'

      if (Object.keys(record).length) {
        console.log(11)
        this.formConfig = { ...editFormConfig }
        // 额外配置
        this.model.id = record.id
      } else {
        console.log(22)
        this.formConfig = { ...addFormConfig }
      }
      // 付给model的值，会覆盖原始值
      for (const item of this.formConfig.formItems) {
        this.model[`${item.field}`] = record[`${item.field}`] || ''
      }
      // 额外配置
      this.model = {
        ...this.model,
        userList: '',
        surveyName: '',
        surveyId: ''
      }
      if (this.model.id) {
        this.isDisabledBtn = true
        this.model.surveyId = ''
        // this.selectedSurveyRowKeys = res.rowKeys
        // this.VxeTable.dataSource = res.user.records
        // this.model.surveyName = res.survey.surName
      }

      this.$refs.form.formData = this.model
      this.visible = true
    },

    /** 触发表单验证 */
    validateFields() {
      this.getAllTable()
        .then(tables => {
          const formInstance = this.$refs.form
          /** 一次性验证主表和所有的次表 */
          return validateFormModelAndTables(formInstance.$refs.formRef, this.model, tables)
        })
        .then(allValues => {
          this.classifyIntoFormData(allValues)
          // 发起请求
          return this.requestAddOrEdit()
        })
        .catch(e => {
          if (e.error === VALIDATE_FAILED) {
            // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
            this.activeKey = e.index == null ? this.activeKey : (e.index + 1).toString()
          } else {
            console.error(e)
          }
        })
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      this.model.surProject = {
        projectName: this.model.projectName,
        leader: this.model.leader
      }
      this.model.rowKeys = this.selectedSurveyRowKeys
      // 本地缓存获取用户
      this.model.userName = window.localStorage.getItem('pro__Login_Username').value
      this.model.userList = allValues.tablesValue[0].tableData
      this.model.selectNumber = parseInt(allValues.tablesValue[0].tableData.length)
      this.model.depUserList = this.selectDeparntmentPeople
      if (this.selectDeparntmentPeople.length > 0) {
        this.model.userRowKeys = this.selectedDeparntmentRowKeys
      }

      return this.model
    },
    requestAddOrEdit() {
      const that = this
      console.log(this.model)
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
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
