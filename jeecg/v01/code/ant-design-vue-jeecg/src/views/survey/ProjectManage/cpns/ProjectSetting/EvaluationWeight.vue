<template>
  <a-spin :spinning="confirmLoading" style="margin-top: 20px;">
    <div v-if="isUpAndDown" class="evaluationWeight">
      <a-descriptions title="设置权重" bordered :column="{ xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }">
        <a-descriptions-item :label="item.name" v-for="item in evaluationWeight" :key="item.id">
          <a-input v-model="item.weight" />
        </a-descriptions-item>
      </a-descriptions>
      <span style="margin-top: 20px; display: inline-block;">权重值和必须为100%</span>
      <div style="display: flex; justify-content: center; margin-top: 20px;">
        <a-button type="primary" @click="handleWeightSave">保存</a-button>
      </div>
    </div>
    <div v-else>
      无上下级关系，无需设置权重
    </div>

    <div class="btns" style="margin-top: 20px;">
      <a-button type="primary" @click="nextStep">下一步</a-button>
      <a-button style="margin-left: 8px" @click="prevStep">上一步</a-button>
    </div>
  </a-spin>
</template>
<script>
import { axios } from '@/utils/request'
export default {
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
  mounted() {
    if (this.isUpAndDown) {
      this.getEvaluationWeight()
    }
  },
  data() {
    return {
      confirmLoading: false,
      dataSourceEvaluation: [],
      evaluationWeight: [],
      url: {
        getEvaluationWeight: '/survey/surEvaluationWeight/queryByProject/',
        saveEvaluationWeight: '/survey/surEvaluationWeight/add'
      }
    }
  },
  methods: {
    getEvaluationWeight() {
      const _this = this
      this.confirmLoading = true
      axios({
        method: 'get',
        url: this.url.getEvaluationWeight + this.projectId
      })
        .then(res => {
          this.dataSourceEvaluation = res.result

          if (this.dataSourceEvaluation) {
            Object.keys(this.dataSourceEvaluation).forEach(key => {
              if (key === 'superiorWeight') {
                this.evaluationWeight.push({
                  weight: this.dataSourceEvaluation[key],
                  name: '上级'
                })
              } else if (key === 'colleagueWeight') {
                this.evaluationWeight.push({
                  weight: this.dataSourceEvaluation[key],
                  name: '同事'
                })
              } else if (key === 'subordinateWeight') {
                this.evaluationWeight.push({
                  weight: this.dataSourceEvaluation[key],
                  name: '下级'
                })
              } else if (key === 'otherWeight') {
                this.evaluationWeight.push({
                  weight: this.dataSourceEvaluation[key],
                  name: '其它'
                })
              } else if (key === 'selfWeight') {
                this.evaluationWeight.push({
                  weight: this.dataSourceEvaluation[key],
                  name: '自评'
                })
              }
            })
            this.confirmLoading = false
          } else {
            this.evaluationWeight = [
              {
                name: '上级',
                weight: '0%'
              },
              {
                name: '下级',
                weight: '0%'
              },
              {
                name: '同事',
                weight: '0%'
              },
              {
                name: '其它',
                weight: '0%'
              },
              {
                name: '自评',
                weight: '0%'
              }
            ]
            this.confirmLoading = false
          }
        })
        .catch(err => {
          console.log(err)
          this.confirmLoading = false
          this.$message.error('获取数据失败，请稍后重试')
        })
    },
    // 保存权重
    async handleWeightSave() {
      if (this.$store.state.project.isPublished) {
        return this.$message.warning('该项目已经发布，不能修改该项目信息。')
      }
      let list = {}
      this.evaluationWeight.forEach(item => {
        if (item.name === '上级') {
          list.superiorWeight = item.weight
        } else if (item.name === '同事') {
          list.colleagueWeight = item.weight
        } else if (item.name === '下级') {
          list.subordinateWeight = item.weight
        } else if (item.name === '其它') {
          list.otherWeight = item.weight
        } else if (item.name === '自评') {
          list.selfWeight = item.weight
        }
      })
      // 遍历dataSourceLatitude，检查是否有权重之和是否为100%
      let sum = 0
      this.evaluationWeight.forEach(item => {
        sum += parseInt(item.weight.slice(0, -1))
      })
      if (sum !== 100) {
        this.$message.error('权重之和必须为100%')
      } else {
        const result = await axios({
          method: 'post',
          url: this.url.saveEvaluationWeight,
          data: {
            projectId: this.projectId,
            ...list
          }
        })
        if (result.code !== 200) {
          return this.$message.error('保存失败,请稍后再试')
        }
        this.$message.success('保存成功')
      }
    },
    nextStep() {
      if (this.evaluationWeight.length > 0) {
        this.$emit('nextStep360', true)
      } else {
        this.$emit('nextStep360')
      }
    },
    prevStep() {
      this.$emit('prevStep')
    }
  }
}
</script>
<style lang=""></style>
