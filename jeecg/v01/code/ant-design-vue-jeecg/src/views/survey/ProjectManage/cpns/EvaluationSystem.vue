<template>
  <div>
    <a-tabs v-model="activeKey">
      <a-tab-pane tab="评价体系设置" key="1" :forceRender="true">
        <j-vxe-table
          ref="editableTable"
          toolbar
          keep-source
          :reloadEffect="true"
          :height="300"
          :loading="VxeTable.loading"
          :dataSource="VxeTable.dataSource"
          :columns="VxeTable.columns"
          @added="handleAdded"
          style="margin-top: 8px;"
        >
        </j-vxe-table>
      </a-tab-pane>
      <a-tab-pane tab="评价权重设置" key="2" :forceRender="true">
        开发中...
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<script>
import { VALIDATE_FAILED, getRefPromise, validateFormModelAndTables } from '@/components/jeecg/JVxeTable/utils/vxeUtils'
import { VxeTableConfig } from './config/table.content'
import { axios } from '@/utils/request'
import { mapActions } from 'vuex'
export default {
  props: {
    projectId: {
      type: String,
      default: '',
      required: true
    }
  },
  data() {
    return {
      VxeTable: { ...VxeTableConfig },
      userList: [],
      activeKey: '1',
      url: {
        projectUserList: '/survey/surProject/userList/',
        getEvaluationList: '/survey/surEvaluationRelationship/listByProject'
      }
    }
  },
  async created() {
    const { result: res } = await axios({
      method: 'get',
      url: this.url.projectUserList + this.projectId
    })
    this.userList = res.map(item => {
      return {
        value: item.id,
        title: item.name
      }
    })
    this.VxeTable.columns[1].options = this.userList
  },
  async mounted() {
    const { result: res } = await axios({
      method: 'get',
      url: this.url.projectUserList + this.projectId
    })
    this.userList = res.map(item => {
      return {
        value: item.id,
        title: item.name
      }
    })
    console.log(this.$refs.editableTable.$refs.vxe.columns)
    this.$refs.editableTable.$children[1].columns[1].options = this.userList
    this.getUserList()
    this.getUserListAction(this.projectId)
    this.getEvaluationList()
  },
  methods: {
    // 处理添加行
    handleAdded(row) {
      let columns = this.VxeTable.columns
      columns.forEach(item => {
        if (item.key !== 'userName') {
          row.$table.columns.find(item2 => item2.key === item.key).options = this.$store.state.evaluation.userList.map(
            item3 => {
              return {
                value: item3.id,
                title: item3.name
              }
            }
          )
        }
      })
    },
    ...mapActions(['getUserListAction']),
    async getUserList() {
      const { result: res } = await axios({
        method: 'get',
        url: this.url.projectUserList + this.projectId
      })
      this.userList = res.map(item => {
        return {
          value: item.id,
          title: item.name
        }
      })
    },
    async getEvaluationList() {
      console.log(this.$refs.editableTable.$children[1].columns[1].options)
      console.log(this.$refs.editableTable)
      axios({
        method: 'post',
        url: this.url.getEvaluationList,
        data: {
          id: this.projectId
        }
      }).then(res => {
        this.VxeTable.dataSource = res.result.map(item => {
          return {
            ...item,
            // 将
            superiorList: ['1552473457571180545'],
            subordinateList: item.subordinateList.map(item2 => item2.evaluatorName).join(','),
            otherList: item.otherList.map(item2 => item2.evaluatorName).join(','),
            colleagueList: item.colleagueList.map(item2 => item2.evaluatorName).join(',')
          }
        })
        console.log(this.VxeTable.dataSource)
      })
    },
    handleSubmit() {
      console.log(this.$refs.editableTable)
      // 进行表单验证
      this.$refs.editableTable.validateTable().then(errMap => {
        if (!errMap) {
          this.$message.success('验证通过')
          // 获取数据
          let tableData = this.$refs.editableTable.getTableData()
          let deleteData = this.$refs.editableTable.getDeleteData()
          // 将通过后的数组提交到后台或自行进行其他处理
          console.log({ tableData, deleteData })
        } else {
          // 验证未通过，errMap里包含具体未验证通过的详情
          this.$message.error('验证未通过')
        }
      })
    }
  }
}
</script>
<style scoped></style>
