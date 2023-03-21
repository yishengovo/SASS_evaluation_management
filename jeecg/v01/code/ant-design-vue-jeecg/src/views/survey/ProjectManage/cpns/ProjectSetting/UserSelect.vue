<template>
  <div>
    <a-spin :spinning="confirmLoading">
      <a-tabs v-model="activeKey" style="margin: 40px auto 0;">
        <a-tab-pane tab="用户信息" key="1" :forceRender="true">
          <j-vxe-table
            ref="userInfoTableRef"
            toolbar
            row-number
            :rowSelection="true"
            keep-source
            :toolbarConfig="{ slot: ['prefix', 'suffix'], btn: ['add', 'remove', 'clearSelection'] }"
            :height="300"
            :loading="tableConfig.vxeTableConfig.loading"
            :dataSource="tableConfig.vxeTableConfig.dataSource"
            :columns="tableConfig.vxeTableConfig.columns"
            style="margin-top: 8px;"
          />
        </a-tab-pane>
        <a-tab-pane tab="部门人员" key="2" :forceRender="true">
          部门选择：
          <a-cascader
            :fieldNames="{ label: 'departName', value: 'id', children: 'children' }"
            :options="departmentOptions"
            placeholder="请选择部门"
            :allowClear="false"
            @change="onSelectChange"
            style="margin-bottom: 20px;"
          />
          <a-spin :spinning="confirmLoadingDepartment">
            <a-table
              ref="tableDepartment"
              rowKey="id"
              :dataSource="dataSourceDepartment"
              :columns="tableConfig.deparntmentUserColumns"
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
          </a-spin>
        </a-tab-pane>
      </a-tabs>
      <div class="btns" style="margin-top: 20px;">
        <a-button type="primary" @click="nextStep">下一步</a-button>
        <!-- <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button> -->
      </div>
    </a-spin>
  </div>
</template>
<script>
import { axios } from '@/utils/request'
import { tableConfig } from './config/table.content'
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
      activeKey: '1',
      deparntmentCallBackData: {},
      currentDeparntmentId: '',
      dataSourceDepartment: [],
      departmentOptions: [],
      departmentList: [],
      selectDeparntmentPeople: [],
      // 选择部门人的rowkey
      selectedDeparntmentRowKeys: [],
      // 选择的用户数
      selectNumber: 0,
      confirmLoading: false,
      confirmLoadingDepartment: false,
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
      url: {
        queryById: '/survey/surProject/queryById',
        departmentList: 'survey/depart/queryTreeList',
        loadUser: '/survey/surUser/queryByDep',
        saveUserInfo: '/survey/surProject/setUser'
      }
    }
  },
  mounted() {
    this.callback()
    this.getDepartmentList()
  },
  methods: {
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
      this.dataSourceDepartment = []
      this.departmentList = []
      this.confirmLoadingDepartment = true
      const { result: res } = await axios({
        method: 'post',
        url: this.url.loadUser,
        data
      })
      this.dataSourceDepartment = res.records
      this.ipaginationDeparntment.total = res.total
      this.confirmLoadingDepartment = false
    },
    // 选人前的回调
    async callback() {
      this.confirmLoading = true
      this.tableConfig.vxeTableConfig.dataSource = []
      const res = await axios({
        method: 'get',
        url: this.url.queryById,
        params: {
          id: this.projectId,
          pageNum: 1,
          pageSize: 10
        }
      })

      this.tableConfig.vxeTableConfig.dataSource = res.result.user.records
      this.deparntmentCallBackData = res.result
      this.selectedDeparntmentRowKeys = res.result.userRowKeys
      this.confirmLoading = false
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
      if (this.deparntmentCallBackData !== null) {
        return {
          props: {
            disabled: this.selectedDeparntmentRowKeys.some(item => item === row.id)
          }
        }
      }
    },
    validateFields() {
      const _this = this
      console.log(this.$refs.userInfoTableRef)
    },
    nextStep() {
      if (this.$store.state.project.isPublished) {
        this.$emit('nextStep')
        return
      }
      this.$refs.userInfoTableRef.validateTable().then(errMap => {
        if (!errMap) {
          // 获取数据
          let tableData = this.$refs.userInfoTableRef.getTableData()
          axios({
            method: 'post',
            url: this.url.saveUserInfo,
            data: {
              id: this.projectId,
              userList: tableData,
              userRowKeys: this.selectedDeparntmentRowKeys,
              depUserList: this.selectDeparntmentPeople
            }
          })
            .then(res => {
              console.log(this.selectDeparntmentPeople)
              console.log(this.selectedDeparntmentRowKeys)
              this.$emit('nextStep')
            })
            .catch(err => {
              this.$message.error('保存数据失败，请稍后重试')
            })
        } else {
        }
      })
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style lang=""></style>
