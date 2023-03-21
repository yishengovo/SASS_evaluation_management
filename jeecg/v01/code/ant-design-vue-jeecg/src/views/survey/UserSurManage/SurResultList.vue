<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24"> </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <!-- <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button> -->
      <a-button type="primary" icon="download" @click="handleExportXls('sur_result')">导出</a-button>
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
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down"/></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
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
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img
            v-else
            :src="getImgView(text)"
            :preview="record.id"
            height="25px"
            alt=""
            style="max-width:80px;font-size: 12px;font-style: italic;"
          />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button v-else :ghost="true" type="primary" icon="download" size="small" @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleView(record)">查看答卷情况</a>
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

    <sur-result-modal ref="modalForm" @ok="modalFormOk"></sur-result-modal>
    <j-modal
      :title="title"
      :visible="visible"
      :fullscreen="true"
      :footer="null"
      @cancel="handleCancel"
      cancelText="关闭"
    >
      <answer-preview :jsonAnswer="answer" :surveyId="surveyUid" v-if="visible"></answer-preview>
    </j-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import SurResultModal from '../modules/UserSurManage/SurResultModal'
import AnswerPreview from './cpns/AnswerPreview.vue'

export default {
  name: 'SurResultList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    SurResultModal,
    AnswerPreview
  },
  data() {
    return {
      title: '答卷情况查看',
      answer: {},
      surveyUid: '',
      visible: false,
      description: 'sur_result管理页面',
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '调查问卷编号',
          align: 'center',
          dataIndex: 'surveyUid'
        },
        {
          title: '项目编号',
          align: 'center',
          dataIndex: 'projectUid'
        },
        {
          title: '用户编号',
          align: 'center',
          dataIndex: 'userUid'
        },
        {
          title: '项目名称',
          align: 'center',
          dataIndex: 'project'
        },
        {
          title: '问卷名称',
          align: 'center',
          dataIndex: 'survey'
        },
        {
          title: '答卷人',
          align: 'center',
          dataIndex: 'name'
        },
        {
          title: '年龄',
          align: 'center',
          dataIndex: 'age'
        },
        {
          title: '性别',
          align: 'center',
          dataIndex: 'gender'
        },
        {
          title: '手机号',
          align: 'center',
          dataIndex: 'phone'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/survey/surResult/list',
        delete: '/survey/surResult/delete',
        deleteBatch: '/survey/surResult/deleteBatch',
        exportXlsUrl: '/survey/surResult/exportXls',
        importExcelUrl: 'survey/surResult/importExcel'
      },
      dictOptions: {},
      superFieldList: []
    }
  },
  created() {
    this.getSuperFieldList()
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    // 处理查看答题情况
    handleView(record) {
      this.answer = JSON.parse(record.answer)
      this.surveyUid = record.surveyUid
      this.visible = true
      console.log('查看答卷情况')
    },
    close() {
      this.visible = false
    },
    handleOk() {
      console.log('确定')
    },
    submitCallback() {
      this.visible = false
    },
    handleCancel() {
      this.close()
    },
    initDictConfig() {},
    getSuperFieldList() {
      let fieldList = []
      fieldList.push({ type: 'string', value: 'fid', text: '唯一标识' })
      fieldList.push({ type: 'string', value: 'surveyUid', text: '调查问卷' })
      fieldList.push({ type: 'string', value: 'questionUid', text: '问题' })
      fieldList.push({ type: 'string', value: 'titleUid', text: '矩阵标题' })
      fieldList.push({ type: 'string', value: 'answer', text: '答案' })
      fieldList.push({ type: 'string', value: 'answerOther', text: '其他答案' })
      fieldList.push({ type: 'string', value: 'fillDate', text: '填写时间' })
      fieldList.push({ type: 'string', value: 'userUid', text: '用户' })
      fieldList.push({ type: 'string', value: 'empUid', text: '员工' })
      fieldList.push({ type: 'string', value: 'answers', text: '多选答案' })
      fieldList.push({ type: 'string', value: 'responseUid', text: '问卷人' })
      fieldList.push({ type: 'string', value: 'orgUid', text: '组织' })
      fieldList.push({ type: 'string', value: 'tenantUid', text: '所属租户' })
      fieldList.push({ type: 'string', value: 'enableDate', text: '有效开始时间' })
      fieldList.push({ type: 'string', value: 'disableDate', text: '有效截止时间' })
      fieldList.push({ type: 'int', value: 'dr', text: '删除标记' })
      fieldList.push({ type: 'int', value: 'ts', text: '时间戳' })
      fieldList.push({ type: 'string', value: 'createName', text: '创建时间' })
      fieldList.push({ type: 'string', value: 'createDate', text: '更新人' })
      fieldList.push({ type: 'string', value: 'updateName', text: '更新人名称' })
      fieldList.push({ type: 'string', value: 'updateDate', text: '更新时间' })
      this.superFieldList = fieldList
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>
