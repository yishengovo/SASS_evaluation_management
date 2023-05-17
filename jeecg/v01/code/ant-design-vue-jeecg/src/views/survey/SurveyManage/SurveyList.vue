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
            <a-form-item label="问卷名称">
              <j-input placeholder="输入问卷名称模糊查询" v-model="queryParam.surName"></j-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="4">
            <a-form-item label="问卷类型">
              <a-select v-model="queryParam.type" placeholder="请选择问卷类型">
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value="调查">调查</a-select-option>
                <a-select-option value="测评">测评</a-select-option>
                <a-select-option value="360度评估">360度评估</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="问卷启用状态">
                <a-select v-model="queryParam.isUse" placeholder="请选择项问卷启用状态">
                  <a-select-option value="">请选择</a-select-option>
                  <a-select-option value="true">已启用</a-select-option>
                  <a-select-option value="false">已停用</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <!-- <a-col :md="6" :sm="8">
            <a-form-item label="更新时间">
              <a-select v-model="queryParam.sort" placeholder="请选择更新时间">
                <a-select-option value="">请选择</a-select-option>
                <a-select-option value= true>降序</a-select-option>
                <a-select-option value= false>升序</a-select-option>
              </a-select>
            </a-form-item>
          </a-col> -->
          </template>

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
      <a-button type="primary" icon="download" @click="handleExportXls('survey')">导出</a-button>
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
                          {{ record.surName }}
                        </template>
                        <div class="ellipsis">{{ record.surName }}</div>
                      </a-tooltip>
                    </div>
                  </div>
                  <img src="@/assets/template02.png" alt="" />
                  <div @click="handleDesign(record)" class="cover">
                    <a-icon class="icon-form" type="form" />
                    <span class="desc">设计问卷</span>
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
                    <a @click="handleSurveySetting(record)">设置</a>
                  </span>

                  <span class="btn-item">
                    <a-dropdown placement="topLeft">
                      <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
                      <a-menu slot="overlay">
                        <a-menu-item>
                          <a @click="handleDetail(record)">详情</a>
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
    <!-- 列表模式 -->
    <!-- table区域-begin -->
    <div v-if="mode === 'list'">
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
          <a @click="handleDesign(record)">设计问卷</a>
          <a-divider type="vertical" />
          <a @click="handleSurveySetting(record)">调查项设置</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
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

    <survey-modal ref="modalForm" @ok="modalFormOk"></survey-modal>

    <j-modal
      :title="title"
      :visible="visible"
      :fullscreen="true"
      :footer="null"
      @cancel="handleCancel"
      cancelText="关闭"
    >
      <survey-creator class="creator" :surveyId="creatorId" v-if="visible"></survey-creator>
    </j-modal>
    <!-- <a-modal v-model:visible="visible" title="问卷设计" width="100%" wrap-class-name="full-modal" :footer="null">
      <survey-creator :surveyId="creatorId" v-if="visible"></survey-creator>
    </a-modal> -->
    <!-- 问题列表 -->
    <j-modal
      title="编辑选项"
      :visible="visibleOPtion"
      @cancel="handleCancel"
      cancelText="关闭"
      @ok="handleOk"
      width="50%"
      switchFullscreen
    >
      <!-- 纬度设置 -->
      <a-tabs v-model="activeKey" @change="handleChangePanel">
        <!-- <a-tab-pane tab="纬度设置" key="1">
          <a-button style="margin-bottom:10px" type="primary" @click="handleAddLatitude">添加纬度</a-button>
          <a-table
            ref="tableLatitudeRef"
            rowKey="id"
            :scroll="{ x: true, y: 380 }"
            :dataSource="dataSourceLatitude"
            :columns="tableConfig.latitudeColumns"
            :pagination="false"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          >
            <span slot="action" slot-scope="text, records">
              <a @click="handleEditLatitude(records)">编辑</a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                <a-menu slot="overlay">
                  <a-menu-item>
                    <a @click="handleDeleteDimension(records)">删除</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </span>
          </a-table>
        </a-tab-pane> -->
        <!-- 绑定题目 -->
        <!-- <a-tab-pane tab="绑定题目" key="4">
          选择纬度:
          <a-select
            :model="selectLatitude"
            placeholder="请选择"
            :options="latitudeOptions"
            :value="selectLatitude"
            style="width: 200px;margin-bottom: 10px"
            @change="handleSelectChange"
          >
            <a-select-option v-for="option in latitudeOptions" :value="option.value" :key="option.value">{{
              option.label
            }}</a-select-option>
          </a-select>

          <a-table
            ref="tableBindingsRef"
            rowKey="content"
            :scroll="{ x: true, y: 380 }"
            :dataSource="dataSourceBindings"
            :columns="tableConfig.bindLatitudeColumn"
            :pagination="false"
            v-if="!isBindingsEidt"
          >
            <template slot="content" slot-scope="text, records">
              <span v-if="records.questionList.length === 0">此维度未绑定题目</span>
              <span v-else class="ellipsis">{{ records.content }}</span>
            </template>
            <span slot="action" slot-scope="text, records">
              <a @click="handleBindLatitude(records)">编辑</a>
            </span>
          </a-table>
          <a-spin :spinning="confirmLoading">
            <a-table
              ref="tableBindingsEditRef"
              rowKey="id"
              :scroll="{ x: true, y: 380 }"
              :dataSource="dataSourceEditBindings"
              :columns="tableConfig.bindLatitudeEditColumns"
              :pagination="false"
              :row-selection="{
                selectedRowKeys: selectedDimensionRowKeys,
                onChange: onSelectDimensionChange,
                getCheckboxProps: getCheckboxBindingsProps
              }"
              v-if="isBindingsEidt"
            >
            </a-table>
          </a-spin>

          <div style="display: flex; justify-content: center; margin-top: 20px;">
            <a-button type="primary" @click="handleBindLatitudeSave" v-if="isBindingsEidt">保存</a-button>
          </div>
        </a-tab-pane> -->
        <!-- 设置纬度权重 -->
        <!-- <a-tab-pane tab="纬度权重" key="5">
          <a-descriptions title="设置一级维度权重" bordered :column="{ xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }">
            <a-descriptions-item :label="item.name" v-for="item in dataSourceLatitude" :key="item.id">
              <a-input v-model="item.weight" />
            </a-descriptions-item>
          </a-descriptions>
          <span style="margin-top: 20px; display: inline-block;">权重值和必须为100%</span>
          <div style="display: flex; justify-content: center; margin-top: 20px;">
            <a-button type="primary" @click="handleWeightSave">保存</a-button>
          </div>
        </a-tab-pane> -->
        <!-- 题目列表 -->
        <a-tab-pane tab="题目列表" key="1">
          <a-table
            ref="tableQuestionsRef"
            rowKey="id"
            :scroll="{ x: true }"
            :dataSource="dataSourceQuestions"
            :columns="tableConfig.questionColumns"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            :pagination="ipaginationQuestion"
            @change="handleTableQutionsChange"
          >
            <span slot="pid" slot-scope="text, records">
              <a-input v-if="records.edit" size="small" v-model="records.pid" />
              <span v-else> {{ records.pid }}</span>
            </span>
            <span slot="action" slot-scope="text, records">
              <a @click="handleEditQuestion(records)">设置选项</a>
              <a-divider type="vertical" />

              <a-button
                type="link"
                size="small"
                @click="handleSaveQuestionOption(records)"
                v-if="records.edit"
                style="color: #67c23a"
              >
                保存
              </a-button>
              <a-button type="link" size="small" @click="handleEditQuestionOption(records)" v-else>编辑</a-button>
              <a-divider type="vertical" />
              <a-button type="text" size="small" @click="handleCancelSaveQuestionOption(records)" v-if="records.edit">
                取消
              </a-button>
            </span>
          </a-table>
        </a-tab-pane>
        <!-- 设置选项 -->
        <a-tab-pane tab="设置选项" key="2" :disabled="isDisabled">
          <a-table
            ref="tableOptionRef"
            :scroll="{ x: true }"
            :dataSource="dataSourceOptions"
            :columns="tableConfig.answerColumns"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          >
            <span slot="pid" slot-scope="text, records">
              <a-input v-if="records.edit" size="small" v-model="records.pid" />
              <span v-else> {{ records.pid }}</span>
            </span>
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
              <!-- <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
                <a-menu slot="overlay">
                  <a-menu-item>
                    <a @click="handleDeleteQuestion(records)">删除</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown> -->
            </span>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </j-modal>
    <!-- 添加纬度弹窗 -->
    <j-modal
      title="纬度设置"
      :visible="isAddLatitudeVisible"
      cancelText="关闭"
      @ok="handleAddLatitudeOk"
      @cancel="handleAddLatitudeCancel"
      :width="600"
    >
      <au-form v-bind="formConfig" v-model="model" ref="form"></au-form>
    </j-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import SurveyModal from '../modules/SurveyManage/SurveyModal'
import SurveyCreator from '@/components/survey/creator/SurveyCreator.vue'
import AuForm from '@/components/base-ui/AuForm'

import { contentConfig } from './config/content.config'
import { tableConfig } from './config/table.content'
import { axios } from '@/utils/request'
import { getQuestionType } from '@/constant/survey'
import { formConfig } from './config/form.config'
import { setQuestId } from '@/api/survey'

export default {
  name: 'SurveyList',
  mixins: [JeecgListMixin, mixinDevice],
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
  components: {
    AuForm,
    SurveyModal,
    SurveyCreator,
  },
  data() {
    return {
      cardPageSize: 12,
      // 项目的展示模式
      mode: 'card',
      confirmLoading: false,
      // 当前维度id
      currentDimensionId: '',
      // 需要绑定的题目列表
      dimensionList: [],
      selectedDimensionRowKeys: [],
      formConfig,
      isAddLatitudeVisible: false,
      model: {},
      // 绑定题目是否处于编辑状态
      isBindingsEidt: false,
      // 选择的纬度
      selectLatitude: '全部',
      latitudeOptions: [],
      // 设置分数的文字，编辑还是保存
      // modeTitle: '编辑',
      //分数是否是编辑
      edit: false,
      isDisabled: true,
      activeKey: '1',
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
        total: 0,
      },
      tableConfig,
      // 当前问卷id
      currentSurveyId: '',
      // 当前问题id
      currentQuestionId: '',
      title: '问卷设计',
      columns: contentConfig.columns,
      description: 'survey管理页面',
      // 表头
      url: {
        list: '/survey/survey/list',
        delete: '/survey/survey/delete',
        deleteBatch: '/survey/survey/deleteBatch',
        exportXlsUrl: '/survey/survey/exportXls',
        importExcelUrl: 'survey/survey/importExcel',
        questionList: '/survey/survey/question',
        optionList: '/survey/survey/choice',
        saveOption: 'survey/survey/setScore',
        getLatitude: 'survey/surQuestionDimension/dimensionList/',
        addLatitude: 'survey/surQuestionDimension/add',
        saveWeight: 'survey/surQuestionDimension/setDimensionWeight',
        getBindingsQuestionById: 'survey/surQuestionDimension/queryBindQuestion/',
        saveBindings: 'survey/surQuestionDimension/bindQuestion',
        deleteDimension: 'survey/surQuestionDimension/delete',
        editDimension: 'survey/surQuestionDimension/edit',
      },
      dictOptions: {},
      superFieldList: [],
      dataSourceQuestions: [],
      dataSourceOptions: [],
      dataSourceLatitude: [],
      dataSourceBindings: [],
      dataSourceEditBindings: [],
      visible: false,
      visibleOPtion: false,
      creatorId: '', //问卷id
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
      console.log(current, size, 222)
    },
    handleDesign(record) {
      // 显示调研设计页面
      this.creatorId = record.id
      this.visible = true
    },
    /** ATab 选项卡切换事件 */
    handleChangeTabs(key) {
      console.log(key)
    },
    // 获取问题列表
    async getQuestionList(data) {
      const { result: res } = await axios({
        method: 'post',
        url: this.url.questionList,
        data,
      })
      this.dataSourceQuestions = res.records.map((item) => {
        // 获取类型
        const type = getQuestionType(item.typeId)
        return {
          ...item,
          typeId: type,
          edit: false,
        }
      })
      this.ipaginationQuestion.total = res.total
    },
    // 获取题目纬度
    async getLatitude(id) {
      this.dataSourceBindings = []
      this.latitudeOptions = []
      const { result: res } = await axios({
        method: 'get',
        url: this.url.getLatitude + id,
      })
      this.latitudeOptions.push({
        value: '全部',
        label: '全部',
      })
      if (res !== null && res.length > 0) {
        res.map((item) => {
          if (item.weight === null) {
            item.weight = '0%'
          }
          this.latitudeOptions.push({
            label: item.name,
            value: item.id,
          })
          if (item.questionList.length > 1) {
            //将question数组中的content合并在一起
            const content = item.questionList
              .map((item) => {
                return item.content
              })
              .join('；')

            this.dataSourceBindings.push({ ...item, content })

            // 去除重复的纬度
          } else if (item.questionList.length === 0) {
            this.dataSourceBindings.push({ ...item })
          } else {
            item.questionList.forEach((question) => {
              this.dataSourceBindings.push({ ...item, ...question, id: item.id })
            })
          }
        })
      }

      this.dataSourceLatitude = res
    },
    // 处理问题编辑后的保存
    async handleSaveQuestionOption(record) {
      const res = await setQuestId({ pid: record.pid, surveyId: record.surveyUid, questionId: record.id })
      record.edit = false
      if (res.code !== 200) {
        return this.$message.error('保存失败，请稍后再试')
      }
      this.$message.success('保存成功')
      console.log(record)
    },
    // 处理问题的编辑
    handleEditQuestionOption(record) {
      record.edit = true
      console.log(record)
    },
    // 处理编辑问题取消保存
    handleCancelSaveQuestionOption(record) {
      record.edit = false
      console.log(record)
    },
    // 处理调查项设置
    handleSurveySetting(record) {
      this.currentSurveyId = record.id
      this.getQuestionList({ id: record.id, pageNum: 1, pageSize: 6 })
      this.getLatitude(record.id)

      this.visibleOPtion = true
    },
    // 处理问题表格变化
    handleTableQutionsChange(pagination) {
      const pager = { ...this.ipaginationQuestion }
      pager.pageNum = pagination.current
      pager.pageSize = pagination.pageSize
      this.ipaginationQuestion = pager
      this.getQuestionList({ pageNum: pagination.current, pageSize: pagination.pageSize, id: this.currentSurveyId })
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
      if (activeKey === '1') {
        this.isDisabled = true
      }
      this.activeKey = activeKey
    },
    // 获取问题选项
    async getOptionList(data) {
      // 先清空
      this.dataSourceOptions = []
      this.dataSourceBindings = []
      const { result: res } = await axios({
        method: 'post',
        url: this.url.optionList,
        data,
      })
      // 将res对象变数组对象 (后端数据格式改动，废弃)
      // Object.keys(res).forEach(key => {
      //   this.dataSourceOptions.push({ content: key, score: res[key], edit: false })
      // })
      this.dataSourceOptions = res.map((item, index) => {
        return {
          ...item,
          index,
          edit: false,
        }
      })
    },
    // 处理编辑分数
    handleEditOption(records) {
      // 将选项设置为编辑状态    this.modeTitle = '保存'
      records.edit = true
      console.log('设置分数')
    },
    // 处理保存分数
    async handleSaveOption(records) {
      // 判断是否有分数,并且分数的类型是否是数字
      if (records.basicScore == null || isNaN(records.basicScore)) {
        this.$message.error('分数只能是数字,请输入正确的分数')
        return
      }
      // 将选项设置为非编辑状态
      records.edit = false
      const score = []
      this.dataSourceOptions.forEach((item) => {
        score.push(item.basicScore)
      })
      const res = await axios({
        method: 'post',
        url: this.url.saveOption,
        data: {
          id: records.id,
          score,
          pid: records.pid,
        },
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
    // 处理添加纬度
    handleAddLatitude() {
      this.currentDimensionId = ''
      this.model.name = ''
      this.isAddLatitudeVisible = true
    },
    // 处理编辑纬度
    handleEditLatitude(record) {
      this.model.name = record.name
      this.isAddLatitudeVisible = true
      this.currentDimensionId = record.id

      console.log('editing')
    },
    // 处理删除维度
    handleDeleteDimension(record) {
      const _this = this
      this.$confirm({
        title: '提示',
        content: '确定删除吗？',
        onOk() {
          axios({
            method: 'delete',
            url: _this.url.deleteDimension,
            params: {
              id: record.id,
            },
          }).then((res) => {
            if (res.code === 200) {
              _this.$message.success('删除成功')
              _this.getLatitude(_this.currentSurveyId)
            } else {
              _this.$message.error(res.message)
            }
          })
        },
        onCancel() {},
      })
    },
    // 根据维度id获取绑定的问题
    async getBindQuestion(id) {
      this.confirmLoading = true
      this.dataSourceEditBindings = []
      const { result: res } = await axios({
        method: 'post',
        url: this.url.getBindingsQuestionById + id,
      })
      res.forEach((item, index) => {
        if (item.questionList.length > 0) {
          item.questionList.forEach((question) => {
            this.dataSourceEditBindings.push({
              name: item.name,
              bindingsId: item.id,
              ...question,
              questionId: question.id,
            })
          })
        }
      })
      // 根据重复的id去重
      this.dataSourceEditBindings = this.dataSourceEditBindings.filter((item, index, arr) => {
        return arr.findIndex((item2) => item2.id === item.id) === index
      })
      this.confirmLoading = false
      // this.dataSourceBindings = res
    },
    // 处理绑定纬度
    handleBindLatitude(record) {
      this.selectedDimensionRowKeys = []
      if (record.rowKeys) {
        this.selectedDimensionRowKeys = JSON.parse(record.rowKeys)
      }
      this.selectLatitude = record.id
      this.getBindQuestion(this.currentSurveyId)
      this.isBindingsEidt = true
    },
    // 处理题目绑定保存
    handleBindLatitudeSave() {
      const _this = this
      this.$confirm({
        title: '提示',
        content: '确定保存吗？',
        onOk() {
          axios({
            method: 'post',
            url: _this.url.saveBindings,
            data: {
              dimensionId: _this.selectLatitude,
              questionId: _this.selectedDimensionRowKeys,
            },
          }).then((res) => {
            if (res.code !== 200) {
              return _this.$message.error('保存失败,请稍后再试')
            }
            _this.$message.success('保存成功')
            _this.getBindQuestion(_this.currentSurveyId)
            _this.getLatitude(_this.currentSurveyId)
          })
        },
        onCancel() {},
      })
    },
    // 处理禁选
    getCheckboxBindingsProps(row) {
      if (this.dataSourceEditBindings !== null) {
        return {
          props: {
            disabled: row.name !== null && this.selectLatitude !== row.dimensionId,
          },
        }
      }
    },
    // 处理纬度选择
    handleSelectChange(value) {
      if (value === '全部') {
        this.selectLatitude = value
        this.selectedDimensionRowKeys = []
        this.isBindingsEidt = false
        return
      }
      this.selectLatitude = value
      this.getBindQuestion(this.currentSurveyId)
      this.dataSourceBindings.forEach((item) => {
        if (item.id == value && item.rowKeys) {
          this.selectedDimensionRowKeys = JSON.parse(item.rowKeys)
        }
      })
      this.isBindingsEidt = true
    },
    // 处理绑定问题选择
    onSelectDimensionChange(selectedRowKeys, selectedRows) {
      this.selectedDimensionRowKeys = selectedRowKeys
    },
    // 处理权重
    async handleWeightSave() {
      // 遍历dataSourceLatitude，检查是否有权重之和是否为100%

      let sum = 0
      this.dataSourceLatitude.forEach((item) => {
        sum += parseInt(item.weight.slice(0, -1))
      })
      if (sum !== 100) {
        this.$message.error('权重之和必须为100%')
      } else {
        const result = await axios({
          method: 'post',
          url: this.url.saveWeight,
          data: {
            id: this.currentSurveyId,
            list: this.dataSourceLatitude.map((item) => {
              return {
                id: item.id,
                weight: item.weight,
              }
            }),
          },
        })
        if (result.code !== 200) {
          return this.$message.error('保存失败,请稍后再试')
        }
        this.$message.success('保存成功')
      }
    },
    close() {
      this.visible = false
      this.visibleOPtion = false
      this.activeKey = '1'
      this.isDisabled = true
      this.isBindingsEidt = false
    },
    handleOk() {
      this.visibleOPtion = false
      this.activeKey = '1'
      this.isDisabled = true
      this.isBindingsEidt = false
      console.log(this.dataSourceLatitude)
      console.log('确定')
    },
    // 确认添加或编辑纬度
    handleAddLatitudeOk() {
      const formInstance = this.$refs.form
      formInstance.$refs.formRef.validate((valid) => {
        if (valid) {
          if (this.currentDimensionId) {
            axios({
              method: 'post',
              url: this.url.editDimension,
              data: {
                id: this.currentDimensionId,
                name: this.model.name,
              },
            }).then((res) => {
              if (res.code === 200) {
                this.$message.success('编辑成功')
                this.isAddLatitudeVisible = false
                this.getLatitude(this.currentSurveyId)
              } else {
                this.$message.error('编辑失败')
              }
            })
          } else {
            axios({
              method: 'post',
              url: this.url.addLatitude,
              data: {
                surveyId: this.currentSurveyId,
                name: this.model.name,
              },
            }).then((res) => {
              if (res.code !== 200) {
                return this.$message.error('添加失败,请稍后再试')
              }
              this.$message.success('添加成功')
              this.isAddLatitudeVisible = false
              this.getLatitude(this.currentSurveyId)
            })
          }
        }
      })
    },
    handleAddLatitudeCancel() {
      this.isAddLatitudeVisible = false // 关闭添加纬度弹框
    },
    submitCallback() {
      this.visible = false
      this.visibleOPtion
    },
    handleCancel() {
      this.close()
    },
    initDictConfig() {},
    getSuperFieldList() {
      let fieldList = []
      fieldList.push({ type: 'string', value: 'surName', text: '问卷名称' })
      fieldList.push({ type: 'string', value: 'surContent', text: '描述' })
      fieldList.push({ type: 'string', value: 'sur', text: '租户' })
    },
  },
}
</script>
<style scoped lang="less">
@import '~@assets/less/common.less';

/deep/ .ant-modal-wrap {
  z-index: 2;
}
/deep/ aside {
  z-index: 0;
}
/deep/ .ant-modal-mask {
  z-index: 1;
}
/** 文字过长省略 */
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
        .icon-form,
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
</style>
