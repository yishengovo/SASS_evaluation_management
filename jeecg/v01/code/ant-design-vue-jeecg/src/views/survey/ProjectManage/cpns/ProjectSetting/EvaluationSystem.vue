<template>
  <div style="margin-top: 20px;">
    <a-spin :spinning="confirmLoading">
      <a-tabs v-model="activeKey" style="margin: 40px auto 0;" @change="handleChange">
        <a-tab-pane tab="用户信息" key="1" :forceRender="true">
          <j-vxe-table
            ref="userInfoTableRef"
            toolbar
            row-number
            :rowSelection="true"
            keep-source
            :toolbarConfig="{ slot: ['prefix', 'suffix'], btn: ['add', 'save', 'remove', 'clearSelection'] }"
            :height="300"
            :loading="tableConfig360.vxeTableUserInfoConfig.loading"
            :dataSource="tableConfig360.vxeTableUserInfoConfig.dataSource"
            :columns="tableConfig360.vxeTableUserInfoConfig.columns"
            @save="handleSave"
            style="margin-top: 8px;"
          />
        </a-tab-pane>
        <a-tab-pane tab="评价关系" key="2" :forceRender="true">
          <a-radio-group :defaultValue="evaluationType" button-style="solid" @change="handleSelectChange">
            <a-radio-button value="a" :disabled="isDisabledUp">
              有上下级
            </a-radio-button>
            <a-radio-button value="b" :disabled="isDisabled">
              无上下级
            </a-radio-button>
          </a-radio-group>
          <div v-if="evaluationType == 'b'">
            <j-vxe-table
              ref="userInfoEvaluationTableRef"
              v-if="evaluationType == 'b'"
              toolbar
              row-number
              keep-source
              :rowSelection="true"
              :toolbarConfig="{ slot: ['prefix', 'suffix'], btn: ['add', 'save', 'remove', 'clearSelection'] }"
              :height="300"
              :loading="tableConfig360.vxeTableConfig.loading"
              :dataSource="tableConfig360.vxeTableConfig.dataSource"
              :columns="tableConfig360.vxeTableConfig.columns"
              @added="handleAddEvaluation"
              @save="handleSaveEvaluation"
              style="margin-top: 8px;"
            ></j-vxe-table>
          </div>
          <template v-if="evaluationType == 'a'">
            <j-vxe-table
              rowKey="id"
              ref="userInfoEvaluationTableRef2"
              toolbar
              row-number
              keep-source
              :rowSelection="true"
              :toolbarConfig="{ slot: ['prefix', 'suffix'], btn: ['add', 'save', 'remove', 'clearSelection'] }"
              :height="300"
              :loading="vxeTableUpAndDownConfig.loading"
              :dataSource="vxeTableUpAndDownConfig.dataSource"
              :columns="vxeTableUpAndDownConfig.columns"
              @added="handleAddEvaluation"
              @save="handleSaveEvaluation"
              @valueChange="handleValueChange"
              @cell-click="handleCellClick"
              style="margin-top: 8px;"
            ></j-vxe-table>
          </template>
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
import { tableConfig, tableConfig360, vxeTableUpAndDownConfig } from './config/table.content'

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
      isDisabled: false,
      isDisabledUp: false,
      evaluationList: [],
      evaluationListUp: [],
      userList: [],
      // a有上下级 b无上下级
      evaluationType: 'b',
      tableConfig360,
      vxeTableUpAndDownConfig,
      tableConfig,
      confirmLoading: false,
      activeKey: '1',
      url: {
        queryById: '/survey/surProject/queryById',
        saveUserInfo: '/survey/surProject/setUser',
        saveEvaluation: '/survey/surEvaluationRelationship/setEvaluationRelationship',
        getEvaluation: '/survey/surEvaluationRelationship/listByProject',
        saveEvaluationUp: '/survey/surEvaluationRelationship/setLevel',
        getEvaluationUp: '/survey/surEvaluationRelationship/levelByProject'
      }
    }
  },
  mounted() {
    this.isDisabled = false
    this.isDisabledUp = false
    this.evaluationType = 'b'
    // this.getEvaluation()
    this.callback()
  },
  methods: {
    // 选人前的回调
    async callback() {
      this.vxeTableUpAndDownConfig.dataSource = []
      this.tableConfig360.vxeTableConfig.dataSource = []
      this.confirmLoading = true
      this.tableConfig360.vxeTableUserInfoConfig.dataSource = []
      const res = await axios({
        method: 'get',
        url: this.url.queryById,
        params: {
          id: this.projectId,
          pageNum: 1,
          pageSize: 10
        }
      })

      this.tableConfig360.vxeTableUserInfoConfig.dataSource = res.result.user.records
      this.userList = res.result.user.records
      this.$refs.userInfoEvaluationTableRef.$refs.vxe.columns.find(
        item => item.key === 'evaluatedName'
      ).options = this.userList.map(item => {
        return {
          label: item.name,
          value: item.id,
          ...item
        }
      })
      this.$refs.userInfoEvaluationTableRef.$refs.vxe.columns.find(
        item => item.key === 'evaluatorName'
      ).options = this.userList.map(item => {
        return {
          label: item.name,
          value: item.id,
          ...item
        }
      })

      // 获取评价关系,无上下级
      const data = await axios({
        method: 'post',
        url: this.url.getEvaluation,
        data: {
          id: this.projectId
        }
      })
      this.evaluationList = data.result
      this.tableConfig360.vxeTableConfig.dataSource = this.evaluationList.map(item => {
        return {
          evaluatedName: item.evaluatedName,
          evaluatorName: item.evaluatorName
        }
      })
      if (data.result.length > 0) {
        console.log(111)
        this.evaluationType = 'b'
        this.isDisabledUp = true
      }
      const data2 = await axios({
        method: 'post',
        url: this.url.getEvaluationUp,
        data: {
          id: this.projectId
        }
      })
      if (data2.result.length > 0) {
        console.log(222)
        this.evaluationType = 'a'
        this.isDisabled = true
        console.log(this.evaluationType)
        console.log(this.isDisabled)
        console.log(this.isDisabledUp)
      }
      this.evaluationListUp = data2.result
      this.confirmLoading = false
    },
    // 获取评价关系
    async getEvaluation() {
      this.tableConfig360.vxeTableConfig.dataSource = []

      const res = await axios({
        method: 'post',
        url: this.url.getEvaluation,
        data: {
          id: this.projectId
        }
      })
      this.evaluationList = res.result
    },
    // 切换面板
    handleChange(key) {
      console.log(key)
    },
    // 保存用户信息
    handleSave() {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
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
              userList: tableData
            }
          })
            .then(res => {
              if (res.code == '200') {
                this.$message.success('保存成功')

                this.callback()
              } else {
                this.$message.error('保存数据失败，请稍后重试')
              }
            })
            .catch(err => {
              this.$message.error('保存数据失败，请稍后重试')
            })
        }
      })
    },
    // 处理行添加
    handleAddEvaluation(row) {
      if (this.evaluationType == 'b') {
        let columns = this.tableConfig360.vxeTableConfig.columns
        columns.forEach(item => {
          if (item.key !== 'userName') {
            row.$table.columns.find(
              item2 => item2.key === item.key
            ).options = this.tableConfig360.vxeTableUserInfoConfig.dataSource.map(item3 => {
              return {
                value: item3.id,
                title: item3.name
              }
            })
          }
        })
      } else if (this.evaluationType == 'a') {
        let columns = this.vxeTableUpAndDownConfig.columns
        columns.forEach(item => {
          if (item.key !== 'isSelf') {
            row.$table.columns.find(item2 => item2.key === item.key).options = this.userList.map(item3 => {
              return {
                value: item3.id,
                title: item3.name
              }
            })
          }
        })
      }
    },
    // 保存评价关系
    handleSaveEvaluation(row) {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      if (this.evaluationType == 'b') {
        this.$refs.userInfoEvaluationTableRef.validateTable().then(errMap => {
          if (!errMap) {
            // 获取数据
            let tableData = this.$refs.userInfoEvaluationTableRef.getTableData()
            let evaluationList = tableData.map(item => {
              return {
                projectId: this.projectId,
                evaluatedName: item.evaluatedName,
                evaluatorName: item.evaluatorName
              }
            })
            axios({
              method: 'post',
              url: this.url.saveEvaluation,
              data: {
                evaluationList
              }
            }).then(res => {
              if (res.code == '200') {
                this.$message.success('保存成功')
                this.getEvaluation()
              } else {
                this.$message.error('保存数据失败，请稍后重试')
              }
            })
            console.log(tableData)
            console.log(evaluationList)
          }
        })
      } else if (this.evaluationType == 'a') {
        this.$refs.userInfoEvaluationTableRef2.validateTable().then(errMap => {
          if (!errMap) {
            // 获取数据
            let tableData = this.$refs.userInfoEvaluationTableRef2.getTableData()
            console.log(tableData)
            let evaluationList = tableData.map((item, index) => {
              return {
                id: item.id,
                projectId: this.projectId,
                evaluatedName: item.evaluatedName,
                superiorName: item.superiorName,
                colleagueName: item.colleagueName,
                subordinateName: item.subordinateName,
                otherName: item.otherName,
                isSelf: item.isSelf
              }
            })
            axios({
              method: 'post',
              url: this.url.saveEvaluationUp,
              data: {
                evaluationList
              }
            }).then(res => {
              if (res.code == '200') {
                this.$message.success('保存成功')
                this.getEvaluation()
              } else {
                this.$message.error('保存数据失败，请稍后重试')
              }
            })
          }
        })
      }
    },
    //处理选择上下级改变
    handleSelectChange(e) {
      this.evaluationType = e.target.value

      if (e.target.value === 'a') {
        this.vxeTableUpAndDownConfig.loading = true
        setTimeout(() => {
          //获取评价关系,有上下级
          axios({
            method: 'post',
            url: this.url.getEvaluationUp,
            data: {
              id: this.projectId
            }
          }).then(data2 => {
            this.evaluationListUp = data2.result
            this.vxeTableUpAndDownConfig.dataSource = this.evaluationListUp.map(item => {
              return {
                id: item.id,
                evaluatedName: item.evaluatedName,
                superiorName: item.superiorName,
                colleagueName: item.colleagueName,
                subordinateName: item.subordinateName,
                otherName: item.otherName,
                isSelf: item.isSelf
              }
            })
            this.vxeTableUpAndDownConfig.loading = false
          })
          let columns = this.vxeTableUpAndDownConfig.columns
          columns.forEach(item => {
            if (item.key !== 'isSelf') {
              this.$refs.userInfoEvaluationTableRef2.$refs.vxe.columns.find(
                item2 => item2.key === item.key
              ).options = this.userList.map(item3 => {
                return {
                  value: item3.id,
                  title: item3.name
                }
              })
            }
          })
        }, 200)
      }
    },
    // 处理选中值改变
    handleValueChange(data) {
      // let selectedArr = []
      // Object.keys(data.row).forEach(key => {
      //   if (key !== 'id') {
      //     const item = data.row[key].split(',')
      //     selectedArr.push(...item)
      //   }
      // })
      // // 比较selectedArr和userList的不同,返回不同
      // let columns = this.vxeTableUpAndDownConfig.columns
      // columns.forEach(item => {
      //   if (item.key !== 'userName') {
      //     this.$refs.userInfoEvaluationTableRef2.$refs.vxe.columns.find(
      //       item2 => item2.key === item.key
      //     ).options = this.userList.map(item3 => {
      //       if (
      //         selectedArr.some(item4 => {
      //           return item4 === item3.id
      //         })
      //       ) {
      //         return {
      //           value: item3.id,
      //           title: item3.name,
      //           disabled: true
      //         }
      //       } else {
      //         return {
      //           value: item3.id,
      //           title: item3.name
      //         }
      //       }
      //     })
      //   }
      // })
    },
    // 处理行变化
    handleCellClick(data) {
      let selectedArr = []
      Object.keys(data.row).forEach(key => {
        if (key !== 'id' && key !== 'isSelf') {
          const item = data.row[key].split(',')
          selectedArr.push(...item)
        }
      })
      // 比较selectedArr和userList的不同,返回不同
      let columns = this.vxeTableUpAndDownConfig.columns
      columns.forEach(item => {
        if (item.key !== 'isSelf') {
          this.$refs.userInfoEvaluationTableRef2.$refs.vxe.columns.find(
            item2 => item2.key === item.key
          ).options = this.userList.map(item3 => {
            if (
              selectedArr.some(item4 => {
                return item4 === item3.id
              })
            ) {
              return {
                value: item3.id,
                title: item3.name,
                disabled: true
              }
            } else {
              return {
                value: item3.id,
                title: item3.name
              }
            }
          })
        }
      })
    },
    nextStep() {
      if (this.evaluationListUp.length > 0) {
        this.$emit('nextStep360', true)
      } else {
        this.$emit('nextStep360')
      }
    }
  }
}
</script>
<style lang=""></style>
