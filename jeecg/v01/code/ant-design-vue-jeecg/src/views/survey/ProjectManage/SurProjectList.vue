<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24"> </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="12">
            <a-form-item label="项目名称">
              <j-input placeholder="输入项目名称模糊查询" v-model="queryParam.projectName"></j-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="4">
            <a-form-item label="项目类型">
              <a-select v-model="queryParam.type" placeholder="请选择项目类型">
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value="调查">调查</a-select-option>
                <a-select-option value="测评">测评</a-select-option>
                <a-select-option value="360度评估">360度评估</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <a-form-item label="项目发布状态">
              <a-select v-model="queryParam.isPublish" placeholder="请选择项目发布状态">
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value="true">已发布</a-select-option>
                <a-select-option value="false">未发布</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <!-- <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="租户编号">
                <a-input placeholder="请输入租户编号" v-model="queryParam.tenantId"></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="6" :sm="8">
            <a-form-item label="更新时间">
              <a-select v-model="queryParam.order" placeholder="请选择更新时间">
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value= true>降序</a-select-option>
                <a-select-option value= false>升序</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          </template> -->

          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('问卷项目表')">导出</a-button>
      <a-upload
        name="file"
        :showUploadList="false"
        :multiple="false"
        :headers="tokenHeader"
        :action="importExcelUrl"
        @change="handleImportExcel"
      >
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query
        :fieldList="superFieldList"
        ref="superQueryModal"
        @handleSuperQuery="handleSuperQuery"
      ></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete" />删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>
    <!-- 模式 -->
    <div class="mode">
      <!-- <a-button-group>
        <a-button type="primary" icon="appstore" />
        <a-button type="primary" icon="bars" />
      </a-button-group> -->
      <a-radio-group :default-value="mode" button-style="solid" @change="handleModeChange">
        <a-radio-button value="card">
          <a-icon type="appstore"></a-icon>
        </a-radio-button>
        <a-radio-button value="list">
          <a-icon type="bars"></a-icon>
        </a-radio-button>
      </a-radio-group>
    </div>

    <a-spin :spinning="loading">
      <!-- 卡片模式 -->
      <div class="card-list" v-if="mode === 'card'">
        <a-row type="flex" justify="start">
          <template v-for="record in dataSource">
            <a-col v-bind="colLayout">
              <a-card class="item">
                <div class="main-content">
                  <div class="card-header">
                    <a-tag color="#dfab5c" class="type" v-if="record.type === '测评'">
                      {{ record.type }}
                    </a-tag>
                    <a-tag color="#59c075" class="type" v-else-if="record.type === '调查'">
                      {{ record.type }}
                    </a-tag>
                    <a-tag color="#5190de" class="type" v-if="record.type === '360度评估'">
                      {{ record.type }}
                    </a-tag>
                    <div class="title">
                      <a-tooltip>
                        <template slot="title">
                          {{ record.projectName }}
                        </template>
                        <div class="ellipsis">{{ record.projectName }}</div>
                      </a-tooltip>
                    </div>
                  </div>
                  <img :src="templatePng" alt="" />
                  <div class="cover" @click="handleRelease(record)" v-if="record.isPublish">
                    <a-icon class="icon-eye" type="eye" />
                    <span class="desc">查看二维码</span>
                  </div>
                  <div class="cover" @click="handleRelease(record)" v-else>
                    <a-icon class="icon-upload" type="cloud-upload" />
                    <span class="desc">发布项目</span>
                  </div>
                </div>
                <div class="time">
                  {{ record.createTime }}
                </div>
                <div class="btns">
                  <span class="btn-item">
                    <a-icon type="edit" style="color: #1890ff; margin-right: 2px" />
                    <a @click="handleEdit(record)">编辑</a>
                  </span>

                  <span class="btn-item">
                    <a-icon type="setting" style="color: #1890ff; margin-right: 2px" />
                    <a @click="handleProjectSetting(record)">设置</a>
                  </span>

                  <span class="btn-item">
                    <a-dropdown placement="topLeft">
                      <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
                      <a-menu slot="overlay">
                        <a-menu-item>
                          <a @click="handleCollectView(record)">收集情况</a>
                        </a-menu-item>
                        <a-menu-item>
                          <a @click="handleStatisticalAnalysis(record)">统计&分析</a>
                        </a-menu-item>
                        <a-menu-item>
                          <a @click="handleDataScreen(record)">数据大屏</a>
                        </a-menu-item>
                        <a-menu-item v-if="record.type === '360度评估'">
                          <a @click="handleViewTable(record)">查看报表</a>
                        </a-menu-item>
                        <a-menu-item>
                          <a @click="handleDetail(record)">详情</a>
                        </a-menu-item>
                        <a-menu-item>
                          <a-popconfirm
                            title="确定发布吗?"
                            @confirm="() => handleIsPublic(record)"
                            v-show="!record.isPublish"
                          >
                            <a>发布</a>
                          </a-popconfirm>
                          <a-popconfirm
                            title="确定取消发布吗?"
                            @confirm="() => handleIsPublic(record)"
                            v-show="record.isPublish"
                          >
                            <a style="color: #ff6500">取消发布</a>
                          </a-popconfirm>
                        </a-menu-item>
                        <a-menu-item>
                          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                            <a style="color: #ff4d4f">删除</a>
                          </a-popconfirm>
                        </a-menu-item>
                      </a-menu>
                    </a-dropdown>
                  </span>
                  <!-- <a @click="handleEvaluationSystem(record)">评价体系</a>
          <a-divider type="vertical" /> -->
                  <!-- <a @click="handleRelease(record)"
              ><template v-if="!record.isPublish">发布问卷</template>
              <template v-else>查看二维码</template>
            </a> -->
                </div>
              </a-card>
            </a-col>
          </template>
        </a-row>
      </div>
    </a-spin>
    <!-- 列表 -->
    <div class="list-mode" v-if="mode === 'list'">
      <!-- table区域-begin -->
      <div>
        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px">
          <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
          <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
          >项
          <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        </div>

        <a-table
          ref="table"
          size="middle"
          :scroll="{ x: true }"
          bordered
          rowKey="id"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          class="j-table-force-nowrap"
          @change="handleTableChange"
        >
          <template slot="publish" slot-scope="text, record">
            <a-tag v-if="record.isPublish == true" color="green">已发布</a-tag>
            <a-tag v-else color="red">未发布</a-tag>
          </template>

          <template slot="leader" slot-scope="text, record">
            {{ record.name }}
          </template>
          <template slot="htmlSlot" slot-scope="text">
            <div v-html="text"></div>
          </template>
          <template slot="imgSlot" slot-scope="text, record">
            <span v-if="!text" style="font-size: 12px; font-style: italic">无图片</span>
            <img
              v-else
              :src="getImgView(text)"
              :preview="record.id"
              height="25px"
              alt=""
              style="max-width: 80px; font-size: 12px; font-style: italic"
            />
          </template>
          <template slot="fileSlot" slot-scope="text">
            <span v-if="!text" style="font-size: 12px; font-style: italic">无文件</span>
            <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="downloadFile(text)">
              下载
            </a-button>
          </template>

          <span slot="action" slot-scope="text, record">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="handleProjectSetting(record)">项目设置</a>
            <a-divider type="vertical" />
            <!-- <a @click="handleEvaluationSystem(record)">评价体系</a>
          <a-divider type="vertical" /> -->
            <a @click="handleRelease(record)"
              ><template v-if="!record.isPublish">发布问卷</template>
              <template v-else>查看二维码</template>
            </a>
            <a-divider type="vertical" />
            <a @click="handleCollectView(record)">收集情况</a>
            <a-divider type="vertical" />
            <a @click="handleStatisticalAnalysis(record)">统计&分析</a>
            <a-divider type="vertical" />
            <a-dropdown>
              <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a @click="handleDataScreen(record)">数据大屏</a>
                </a-menu-item>
                <a-menu-item v-if="record.type === '360度评估'">
                  <a @click="handleViewTable(record)">查看报表</a>
                </a-menu-item>
                <a-menu-item>
                  <a @click="handleDetail(record)">详情</a>
                </a-menu-item>
                <a-menu-item>
                  <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                    <a>删除</a>
                  </a-popconfirm>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>
        </a-table>
      </div>
    </div>
    <!-- <a-pagination
   
      size="small"
      :total="ipagination.total"
      show-size-changer
      show-quick-jumper
    /> -->
    <a-pagination
      v-if="mode === 'card'"
      class="card-pagination"
      :total="ipagination.total"
      :show-total="(total, range) => `${range[0]}-${range[1]} 共 ${total} 条`"
      :page-size="cardPageSize"
      :default-current="1"
      show-size-changer
      show-quick-jumper
      @change="handleCardPaginationChange"
      @showSizeChange="handleShowCardSizeChange"
      :pageSizeOptions="ipagination.pageSizeOptions"
    />
    <sur-project-modal ref="modalForm" @ok="modalFormOk"></sur-project-modal>
    <!-- 二维码 -->
    <j-modal
      :title="projectName"
      class="qr-modal"
      width="640px"
      :visible="visible"
      @cancel="handleCancel"
      cancelText="关闭"
      @ok="handleOk"
    >
      <div class="qrcode-wrapper">
        <div class="qrcode-title">
          <a-icon type="scan" :style="{ fontSize: '16px', color: '#08c', marginRight: '5px', marginTop: '3px' }" />
          <span> 使用其他设备扫描二维码使用此 问卷</span>
        </div>
        <qrcode-vue class="qrcode-code" level="H" :size="qrCodeSize" :value="value" />
      </div>
      <div class="link-survey">
        <div class="link">{{ value }}</div>
        <a-button type="primary" size="small" @click="copyLink">复制链接</a-button>
      </div>
    </j-modal>
    <!-- 收集情况 -->
    <j-modal
      title="收集详情"
      width="50%"
      :visible="collectVisible"
      @cancel="handleCancel"
      cancelText="关闭"
      @ok="handleOk"
      switchFullscreen
    >
      <a-spin :spinning="confirmLoading">
        <a-card :bordered="false">
          <a-row>
            <a-col :sm="8" :xs="24">
              <head-info title="总人数" :content="currentProjectPeople + '人'" :bordered="true" />
            </a-col>
            <a-col :sm="8" :xs="24">
              <head-info title="收集问卷数" :content="currentProjectCollect + '人'" :bordered="true" />
            </a-col>
            <a-col :sm="8" :xs="24">
              <head-info title="完成率" :content="currentProjectRate" />
            </a-col>
          </a-row>
        </a-card>
        <a-table
          ref="collectTable"
          :scroll="{ y: 280, x: true }"
          rowKey="index"
          :columns="tableConfig.collectColumns"
          :dataSource="collectDataSource"
          :loading="collectLoading"
          :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        >
          <template slot="action" slot-scope="text, record">
            <a @click="handleViewDetail(record)">答题详情</a>
            <a-divider type="vertical"></a-divider>
            <a @click="handleAnalysis(record)">测评分析</a>
          </template>
          <template slot="gender" slot-scope="text, record">
            <span v-if="record.gender == 1">男</span>
            <span v-else>女</span>
          </template>
          <template slot="status" slot-scope="text, record">
            <a-tag v-if="record.status == 1" color="green">已完成</a-tag>
            <a-tag v-else color="red">未完成</a-tag>
          </template>
        </a-table>
      </a-spin>
    </j-modal>
    <!-- 查看问卷详情 -->
    <j-modal
      title="问卷详情"
      :visible="surveyVisible"
      :fullscreen="true"
      :footer="null"
      @cancel="handleSurveyCancel"
      cancelText="关闭"
    >
      <answer-preview :jsonAnswer="answer" :surveyId="surveyId" v-if="surveyVisible"></answer-preview>
    </j-modal>
    <!-- 测评分析 -->
    <j-modal
      width="40%"
      title="测评分析"
      :visible="analysisVisible"
      switchFullscreen
      @cancel="handleAnalysisCancel"
      cancelText="关闭"
      @ok="handleAnalysisOk"
    >
      <!-- <answer-analysis :jsonAnswer="answer" :surveyId="surveyId" v-if="analysisVisible"></answer-analysis> -->
      您的分数为: {{ score }}
    </j-modal>
    <!-- 评价体系 -->
    <j-modal
      title="评价体系"
      width="40%"
      :visible="evaluateVisible"
      @cancel="handleEvaluateCancel"
      cancelText="关闭"
      @ok="handleEvaluateOk"
      ref="modalRef"
    >
      <EvaluationSystem ref="evaluationSystemRef" :projectId="projectId"></EvaluationSystem>
    </j-modal>

    <!-- 项目设置 -->
    <j-modal
      title="项目设置"
      width="50%"
      switchFullscreen
      :fullscreen="true"
      :visible="projetcSettingVisible"
      @cancel="handleProjetcSettingCancel"
      cancelText="关闭"
      @ok="handleProjetcSettingOk"
      ref="projetcSettingModalRef"
    >
      <ProjectSetting :projectId="projectId" :projectType="projectType"></ProjectSetting>
    </j-modal>

    <!-- 数据大屏 -->
    <AuDialog
      title="数据大屏"
      :footer="false"
      :width="720"
      cancelText="取消"
      okText="确认"
      @close="onClose"
      @cancel="onCancel"
      @ok="onConfirm"
      v-if="showDialog"
    >
      <SurveyAnalysis :projectId="projectId"></SurveyAnalysis>
    </AuDialog>

    <!-- 统计与分析 -->
    <j-modal
      title="统计&分析"
      :dialog-style="{ top: '0px', bottom: '0px', left: '0px', right: '0px' }"
      :visible="visibleStatistical"
      :fullscreen="true"
      :footer="null"
      @cancel="handleStatisticalCancel"
    >
      <StatisticalAnalysis :projectId="projectId"></StatisticalAnalysis>
    </j-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

import SurProjectModal from '../modules/ProjectManage/SurProjectModal'
import QrcodeVue from 'qrcode.vue'
import HeadInfo from '@/components/tools/HeadInfo'
import AnswerPreview from './cpns/AnswerPreview.vue'
import EvaluationSystem from './cpns/EvaluationSystem.vue'
import ProjectSetting from './cpns/ProjectSetting/index.vue'
import SurveyAnalysis from '@/components/survey/analytics'
import AuDialog from '@/components/base-ui/DiaLog/src/au-dialog.vue'
import StatisticalAnalysis from './cpns/StatisticalAnalysis'
import { getProjectList } from '@/api/project'

import { tableConfig } from './config/table.content'
import { axios } from '@/utils/request'
import { mapActions } from 'vuex'

// 图片
import templatePng from '@/assets/template.png'
export default {
  name: 'SurProjectList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    SurProjectModal,
    ProjectSetting,
    QrcodeVue,
    HeadInfo,
    AnswerPreview,
    EvaluationSystem,
    SurveyAnalysis,
    AuDialog,
    StatisticalAnalysis,
  },
  props: {
    colLayout: {
      type: Object,
      default: () => ({
        xl: 4, // ≥1920px
        lg: 8, // ≥1200px
        md: 12, // ≥992px
        sm: 16, // ≥768px
        xs: 24, // <768px
      }),
    },
  },
  data() {
    return {
      queryParam: {},
      params: {},
      cardPageSize: 12,
      // 项目的展示模式
      mode: 'card',
      token: JSON.parse(localStorage.getItem('pro__Access-Token')).value,
      // 项目分析
      showDialog: false,
      //项目统计与分析
      visibleStatistical: false,
      projectName: '',
      projectType: '',
      projetcSettingVisible: false,
      projectId: '',
      evaluateVisible: false,
      userWapper: 1,
      // 当前收集情况的id
      currentCollectId: '',
      confirmLoading: false,
      // 当前项目的总人数
      currentProjectPeople: 0,
      // 当前项目收集的问卷数
      currentProjectCollect: 0,
      // 当前项目的完成率
      currentProjectRate: '0%',
      // 用户答案json
      answer: '',
      // 答卷入分数
      score: 0,
      // 问卷id
      surveyId: '',
      tableConfig,
      collectDataSource: [],
      collectPagination: {
        pageNum: 1,
        pageSize: 6,
        pageSizeOptions: ['6', '12', '18'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0,
      },
      collectLoading: false,
      collectVisible: false,
      surveyVisible: false,
      analysisVisible: false,
      visible: false,
      value: '', //二维码链接
      qrCodeSize: '300',
      description: '问卷项目表管理页面',
      // 表头
      columns: [
        {
          title: '项目名称',
          align: 'center',
          dataIndex: 'projectName',
        },
        // {
        //   title: '项目负责人',
        //   align: 'center',
        //   dataIndex: 'leader',
        //   scopedSlots: { customRender: 'leader' }
        // },
        {
          title: '项目类型',
          align: 'center',
          dataIndex: 'type',
        },
        {
          title: '发布状态',
          align: 'center',
          dataIndex: 'isPublish',
          scopedSlots: { customRender: 'publish' },
        },
        {
          title: '创建时间',
          align: 'center',
          dataIndex: 'createTime',
        },
        {
          title: '修改时间',
          align: 'center',
          dataIndex: 'updateTime',
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' },
        },
      ],
      url: {
        list: '/survey/surProject/list',
        delete: '/survey/surProject/delete',
        deleteBatch: '/survey/surProject/deleteBatch',
        exportXlsUrl: '/survey/surProject/exportXls',
        importExcelUrl: 'survey/surProject/importExcel',
        collectList: '/survey/surProject/getAnalysis',
        changeProjectState: '/survey/surProject/publish',
        changeProjectIsPubilc: '/client/userProject/changeStatus',
        devQRCodeUrl: 'http://127.0.0.1:3000/survey/library/',
        prodQRCodeUrl: 'http://hrtools.stalent.net:27521/survey/library/',
      },
      dictOptions: {},
      superFieldList: [],
      // 图片
      templatePng,
    }
  },
  created() {
    this.getSuperFieldList()
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    },
  },
  methods: {
    // 条件查询
    async search() {
      let queryParam = this.param
      queryParam.pageNum = this.collectPagination.pageNum
      queryParam.pageSize = this.collectPagination.pageSize
      const { result: res } = await getProjectList(this.param)
      this.dataSource = res.records
      // this.ordersData = res.orders
      // this.paysData = res.pays
      // this.projectBarData = res.projectList
      // console.log(this.projectBarData)
      // this.rankList = res.rankList
      // this.surveyBarData = res.surveyList
      // this.talentsData = res.talents
      // this.totalSalesData = res.totalSales
    },
    ...mapActions(['getCallBackListAction']),
    // 处理展示模式的改变
    handleModeChange(e) {
      this.mode = e.target.value
      this.cardPageSize = this.ipagination.pageSize
    },
    // 处理卡片的分页
    handleCardPaginationChange(page, pageSize) {
      console.log(page, pageSize)
      this.loadData({ type: 'card', params: { page, pageSize } })
    },
    // 卡片页码变化
    handleShowCardSizeChange(current, size) {
      this.cardPageSize = size
      this.loadData({ type: 'card', params: { page: current, pageSize: size } })
      console.log(current, size)
    },
    // 发布与取消发布
    handleIsPublic(record) {
      const _this = this
      axios({
        method: 'post',
        url: _this.url.changeProjectIsPubilc,
        data: {
          id: record.id,
          isPublish: !record.isPublish,
        },
      }).then(res=>{
        if (res.success) {
          _this.$message.success(res.message)
          _this.loadData()
        }else{
          _this.$message.success(res.message)
        }
      })
      
    },
    // 处理问卷发布
    handleRelease(record) {
      console.log(record)
      let projectType = ''
      if (record.type === '360度评估') {
        projectType = 0
      } else {
        projectType = 1
      }
      this.projectName = record.projectName
      const _this = this
      if (!record.isPublish) {
        this.$confirm({
          title: '确定要发布项目吗?',
          content: '发布项目后，该项目的信息不允许再修改。',
          onOk() {
            axios({
              method: 'post',
              url: _this.url.changeProjectState,
              data: {
                id: record.id,
              },
            })
            // 生成链接
            // let url = `${window._CONFIG['domianURL']}/survey/library/?creatorId=${record.id}`
            let url = ''
            if (process.env.NODE_ENV === 'development') {
              // 开发环境
              // url = 'http://127.0.0.1:3000/survey/library/?creatorId=' + record.surveyId
              url =
                _this.url.devQRCodeUrl +
                '?creatorId=' +
                record.surveyCurrentId +
                '&projectId=' +
                record.id +
                '&projectType=' +
                projectType
            }
            if (process.env.NODE_ENV === 'production') {
              // 线上环境
              url =
                _this.url.prodQRCodeUrl +
                '?creatorId=' +
                record.surveyCurrentId +
                '&projectId=' +
                record.id +
                '&projectType=' +
                projectType
            }
            _this.value = url
            _this.visible = true
          },
          onCancel() {},
        })
      } else {
        // 生成链接
        // let url = `${window._CONFIG['domianURL']}/survey/library/?creatorId=${record.id}`
        let url = ''
        if (process.env.NODE_ENV === 'development') {
          // 开发环境
          // url = 'http://127.0.0.1:3000/survey/library/?creatorId=' + record.surveyId
          url =
            _this.url.devQRCodeUrl +
            '?creatorId=' +
            record.surveyCurrentId +
            '&projectId=' +
            record.id +
            '&projectType=' +
            projectType
        }
        if (process.env.NODE_ENV === 'production') {
          // 线上环境
          url =
            _this.url.prodQRCodeUrl +
            '?creatorId=' +
            record.surveyCurrentId +
            '&projectId=' +
            record.id +
            '&projectType=' +
            projectType
        }
        _this.value = url
        _this.visible = true
      }
    },
    async getCollectList(data) {
      // 设置为空，避免数据重复
      this.collectDataSource = []
      this.currentProjectCollect = 0
      this.currentProjectPeople = 0
      this.currentProjectRate = '0%'
      this.confirmLoading = true
      const { result: res } = await axios({
        method: 'post',
        url: this.url.collectList,
        data,
      })
      this.collectPagination.total = res.users.total
      this.currentProjectPeople = res.users.total
      // 如果答案不为空则计算答案的完成人数
      res.users.user.map((item, index) => {
        if (item.result !== null) {
          this.currentProjectCollect++
          this.collectDataSource.push({ ...item, status: 1, index })
        } else {
          this.collectDataSource.push({ ...item, status: 0, index })
        }
      })
      this.surveyId = res.surveyId
      // 计算完成率
      this.currentProjectRate = (this.currentProjectCollect / this.currentProjectPeople).toFixed(2) * 100 + '%'
      if (this.currentProjectRate === 'NaN%') this.currentProjectRate = '0%'
      this.confirmLoading = false
    },
    // 处理点击收集情况
    handleCollectView(records) {
      console.log(records)
      this.currentCollectId = records.id
      this.getCollectList({ id: records.id, pageNum: 1, pageSize: 100 })
      this.collectVisible = true
    },
    // 处理收集情况分页
    // handleTableCollectChange(pagination) {
    //   const pager = { ...this.pagination }
    //   pager.pageNum = pagination.current
    //   this.collectPagination = pager
    //   this.getCollectList({ id: this.currentCollectId, pageNum: pagination.current, pageSize: pagination.pageSize })
    // },
    // 处理查看问卷答题情况
    handleViewDetail(records) {
      console.log(records)

      this.answer = JSON.parse(records.result)

      this.surveyVisible = true
    },
    // 处理测评分析
    handleAnalysis(records) {
      console.log(records)
      this.score = records.score
      this.analysisVisible = true
      console.log('测评分析')
    },
    handleAnalysisCancel() {
      this.analysisVisible = false
    },
    close() {
      this.visible = false
      this.collectVisible = false
      this.surveyVisible = false
      this.analysisVisible = false
    },
    handleOk() {
      this.close()
    },
    handleSurveyCancel() {
      this.surveyVisible = false
    },
    handleAnalysisOk() {
      this.analysisVisible = false
    },
    handleCancel() {
      this.close()
    },
    // 评价体系
    handleEvaluateCancel() {
      this.evaluateVisible = false
    },
    handleEvaluateOk() {
      this.evaluateVisible = false
      this.$refs.evaluationSystemRef.handleSubmit()
    },
    // 处理评价体系
    handleEvaluationSystem(record) {
      this.projectId = record.id

      this.evaluateVisible = true

      // this.$refs.evaluationSystemRef.test()

      console.log('评价体系')
    },
    // 处理项目设置
    handleProjectSetting(record) {
      // 项目设置前的回调
      this.getCallBackListAction({ id: record.id, pageNum: 1, pageSize: 10 })
      this.projectId = record.id
      this.projectType = record.type
      this.projetcSettingVisible = true
      console.log('项目设置')
    },
    handleProjetcSettingCancel() {
      this.projetcSettingVisible = false
    },
    handleProjetcSettingOk() {
      this.projetcSettingVisible = false
    },
    // 复制项目链接
    copyLink() {
      const input = document.createElement('input')
      document.body.appendChild(input)
      input.setAttribute('readonly', 'readonly')
      input.setAttribute('value', this.value)
      input.focus()
      input.setSelectionRange(0, 9999)
      if (document.execCommand('copy')) {
        document.execCommand('copy')
        this.$message.success('复制成功')
      }
      document.body.removeChild(input)
    },
    // 数据大屏
    handleDataScreen(record) {
      this.projectId = record.id
      // this.visibleStatistical = true
      this.showDialog = true
    },
    // 统计与分析
    handleStatisticalAnalysis(record) {
      console.log(record)
      window._CONFIG['test2'] = record.id
      console.log(window._CONFIG['test2'])
      this.projectId = record.id
      this.visibleStatistical = true
    },
    // 取消统计与分析
    handleStatisticalCancel() {
      this.visibleStatistical = false
    },
    onClose() {
      // 关闭dialog
      this.showDialog = false
    },
    onCancel() {
      // “取消”按钮回调
      this.showDialog = false
    },
    onConfirm() {
      // “确定”按钮回调
      this.showDialog = false
    },

    //处理查看报表
    handleViewTable(record) {
      if (process.env.NODE_ENV === 'development') {
        window.open(
          window._CONFIG['domianURL'] +
            '/jmreport/view/727382886038007808?token=' +
            this.token +
            '&dd=' +
            record.projectName,
          '_blank'
        )
      } else if (process.env.NODE_ENV === 'production') {
        // 线上环境
        window.open(
          window._CONFIG['domianURL'] +
            '/jmreport/view/727382886038007808?token=' +
            this.token +
            '&dd=' +
            record.projectName,
          '_blank'
        )
      }
    },
    initDictConfig() {},
    getSuperFieldList() {
      let fieldList = []
      fieldList.push({ type: 'string', value: 'fid', text: '唯一标识', dictCode: '' })
      fieldList.push({ type: 'string', value: 'projectName', text: '项目名称', dictCode: '' })
      fieldList.push({ type: 'string', value: 'leader', text: '项目负责人', dictCode: '' })
      this.superFieldList = fieldList
    },
  },
}
</script>
<style scoped lang="less">
@import '~@assets/less/common.less';
.ellipsis {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.card-pagination {
  display: flex;
  justify-content: end;
}
.mode {
  display: flex;
  justify-content: end;
  margin-bottom: 10px;
}

.card-list {
  .item {
    position: relative;
    width: 200px;
    margin-bottom: 25px;
    border-radius: 8px;
    box-shadow: 0 0 8px 2px rgba(0, 0, 0, 0.1);
    .main-content {
      position: relative;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 5px;
        .title {
          width: 80px;
          text-align: end;
          color: #101010;
        }
      }
      img {
        width: 148px;
        height: 121px;
      }
      .cover {
        display: none;
        position: absolute;
        top: 17%;
        bottom: 0;
        left: 0;
        right: 0;
        margin: 0;
        background-color: rgba(0, 0, 0, 0.3);
        .icon-eye,
        .icon-upload {
          display: block;
          position: absolute;
          top: 0;
          bottom: 0;
          left: 0;
          right: 0;
          margin: 0;
          width: 40px;
          height: 40px;
          font-size: 40px;
          color: #2f54eb;
          margin: auto;
        }
        .desc {
          display: block;
          position: absolute;
          top: 65%;
          left: 50%;
          transform: translateX(-50%);
          font-size: 14px;
          color: #2f54eb;
          margin: auto;
        }
      }
      &:hover .cover {
        display: block;
        cursor: pointer;
      }
    }

    .time {
      margin-top: 4px;
      margin-bottom: -10px;
      text-align: center;
      font-size: 12px;
      line-height: 20px;
      color: #909399;
    }
    .btns {
      display: none;
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      text-align: center;
      // display: flex;
      // justify-content: space-between;
      padding: 5px;
      background-color: #f0f0f0;
      .btn-item {
        margin: 0 5px;
      }
    }

    &:hover .btns {
      display: block;
    }
  }
}
.qr-modal {
  padding: 0 10px;
  .qrcode-wrapper {
    width: 300px;
    height: 360px;
    margin: 0 auto;
    .qrcode-title {
      margin-bottom: 10px;

      margin-left: 26px;
    }
  }
  // 超出部分用省略号
  .link-survey {
    display: flex;
    .link {
      margin-right: 10px;
      font-size: 10px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }
}
</style>
