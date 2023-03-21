<template>
  <a-spin :spinning="confirmLoading" tip="数据加载中...">
    <div class="statisticalAnalysis">
      <div class="content">
        <template v-for="item in dataSource">
          <div class="statisticalAnalysisItem" v-if="item.choiceList.length > 0">
            <div class="question">
              <div class="title">{{ item.content }}</div>
              <div class="questionType">[{{ item.type }}]</div>
            </div>
            <div class="questionAverage">
              <b>本题平均分: {{ item.average }}</b>
            </div>
            <a-table
              :columns="tableConfig.columns"
              :data-source="item.choiceList"
              :pagination="false"
              bordered
              key="index"
            >
              <span slot="proportion" slot-scope="text, record">
                <a-progress :percent="record.proportion" status="normal" />
              </span>
              <template slot="footer" slot-scope="currentPageData">
                <b> 本题有效填写人次: {{ item.userNumber }}人</b>
              </template>
            </a-table>
          </div>
        </template>
        <div class="questionAverage" v-if="!confirmLoading">
          <b>
            题目平均分之和: <span class="averageTotal">{{ sumAverage }}</span>
          </b>
        </div>
      </div>
    </div>
  </a-spin>
</template>
<script>
import { tableConfig } from './config/table.content'
import { axios } from '@/utils/request'
import { getQuestionType } from '@/constant/survey'

import SurveyAnalysis from '@/components/survey/analytics'
export default {
  components: {
    SurveyAnalysis
  },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      confirmLoading: false,
      tableConfig,
      sumAverage: 0,
      dataSource: [],
      url: {
        getStatistics: '/survey/surProject/statistics/'
      }
    }
  },
  mounted() {
    this.getStatisticsAnalysis()
  },
  methods: {
    getStatisticsAnalysis() {
      const _this = this
      this.confirmLoading = true
      axios({
        method: 'GET',
        url: this.url.getStatistics + this.projectId
      })
        .then(res => {
          if (res.result.length > 0) {
            this.dataSource = res.result.map(item => {
              if (item.average) {
                _this.sumAverage += parseInt(item.average)
              } else {
                _this.sumAverage = 0
              }
              return {
                ...item,
                type: item.type == null ? null : getQuestionType(item.type),
                choiceList:
                  item.choiceList.length > 0
                    ? item.choiceList.map((choice, index) => {
                        return {
                          ...choice,
                          index,
                          proportion:
                            choice.proportion != 0
                              ? parseInt(choice.proportion.slice(0, choice.proportion.length - 1))
                              : 0
                        }
                      })
                    : []
              }
            })
            console.log(_this.sumAverage)
          } else {
            this.$message.info('暂无数据')
            _this.sumAverage = 0
          }

          this.confirmLoading = false
        })
        .catch(err => {
          console.log(err)
          this.$message.error('请求数据失败，请稍后重试')
          this.confirmLoading = false
        })
    }
  }
}
</script>
<style lang="less" scoped>
body {
  font-family: 'Helvetica Neue', Helvetica, Arial, 'PingFang SC', 'Microsoft YaHei', 'Microsoft YaHei UI', '微软雅黑',
    sans-serif;
}
/deep/ .ant-progress-outer {
  width: 90%;
}
.statisticalAnalysis {
  width: 846px;
  margin: 0 auto;
  .statisticalAnalysisItem {
    margin-bottom: 20px;
    .question {
      display: flex;
      font-size: 17px;
      .title {
        margin-right: 5px;
        font-weight: 600;
        color: #262626;
      }
      .quetionType {
        color: #a6a6a6;
      }
    }
  }

  .questionAverage {
    margin: 10px 0 10px;
    .averageTotal {
      color: #1ea0fa;
    }
  }
}
</style>
