<template>
  <div>
    <!-- 顶部查询 -->
    <sur-tag-query :pageSize="pageSize"></sur-tag-query>

    <!-- 顶部按钮 -->
    <sur-tag-button :selectedIds="selectedRowKeys" :params="params"></sur-tag-button>

    <!-- 显示选择项数 -->
    <div class="ant-alert ant-alert-info" style="margin-bottom: 16px">
      已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a
      >项
      <a style="margin-left: 24px" @click="clearSelected">清空</a>
    </div>

    <!-- 标签表 -->
    <a-table
      row-key="id"
      :columns="columns"
      :data-source="data"
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :loading="loading"
      :pagination="false"
    >
      <template slot="tag" slot-scope="text, record">
        <a-tag v-if="record.status === 1" color="green">启用</a-tag>
        <a-tag v-else color="red">禁用</a-tag>
      </template>

      <!-- 操作 -->
      <template slot="action" slot-scope="text, record">
        <a @click="showModalEdit(record)">编辑</a>
        <a-divider type="vertical" />

        <a-dropdown>
          <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
          <a-menu slot="overlay">
            <a-menu-item>
              <a @click="showModalDetails(record)">详情</a>
            </a-menu-item>
            <a-menu-item>
              <a-popconfirm title="确定删除吗?" @confirm="deleteTagById(record.id)">
                <a>删除</a>
              </a-popconfirm>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
    </a-table>
    <!-- 分页区域 -->
    <div style="display: flex; justify-content: end">
      <a-pagination
        style="margin-top: 10px"
        :total="total"
        :show-total="(total, range) => `${range[0]}-${range[1]} 共 ${total} 条`"
        :page-size="pageSize"
        :default-current="1"
        show-size-changer
        show-quick-jumper
        @change="handlePaginationChange"
        @showSizeChange="handleShowSizeChange"
        :pageSizeOptions="pageSizeOptions"
      />
    </div>

    <!-- 编辑的model -->
    <a-modal v-model="visibleForEdit" title="编辑标签" @ok="editTag()">
      <!-- 表单 -->
      <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item ref="name" label="标签名称" prop="name">
          <a-input
            v-model="form.name"
            @change="
              () => {
                $refs.name.onFieldChange()
              }
            "
          />
        </a-form-model-item>
        <a-form-model-item label="标签描述" prop="desc">
          <a-input v-model="form.desc" type="textarea" />
        </a-form-model-item>
        <a-form-model-item label="标签状态" prop="status">
          <a-switch v-model="form.status" checked-children="启用" un-checked-children="禁用" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>

    <!-- 详情的model -->
    <a-modal v-model="visibleForDetails" title="标签名称" @ok="handleOkDetails">
      <!-- 表单 -->
      <a-form-model ref="ruleForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item ref="name" label="标签名称" prop="name">
          <a-input :disabled="true" v-model="form.name" />
        </a-form-model-item>
        <a-form-model-item label="标签描述" prop="desc">
          <a-input v-model="form.desc" type="textarea" :disabled="true" />
        </a-form-model-item>
        <a-form-model-item label="标签状态" prop="status">
          <a-switch v-model="form.status" checked-children="启用" un-checked-children="禁用" :disabled="true" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>
<script>
import { getTag, removeTag, modifyTag } from '@/api/tag.js'
import SurTagQuery from './SurTagQuery.vue'
import SurTagButton from './SurTagButton.vue'

const columns = [
  {
    title: '#',
    dataIndex: '',
    key: 'rowIndex',
    width: 60,
    align: 'center',
    customRender: function (t, r, index) {
      return parseInt(index) + 1
    },
  },
  {
    title: '标签名称',
    align: 'center',
    dataIndex: 'tagName',
  },
  {
    title: '描述',
    align: 'center',
    dataIndex: 'description',
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    align: 'center',
    dataIndex: 'updateTime',
  },
  {
    title: '标签状态',
    align: 'center',
    dataIndex: 'status',
    scopedSlots: { customRender: 'tag' },
  },
  {
    title: '操作',
    dataIndex: 'action',
    align: 'center',
    fixed: 'right',
    width: 147,
    scopedSlots: { customRender: 'action' },
  },
]

export default {
  name: 'SurTagForm',
  components: {
    SurTagButton,
    SurTagQuery,
  },
  data() {
    return {
      // 表格属性
      data: [],
      columns,
      selectedRowKeys: [],
      loading: false,
      visibleForDetails: false,
      visibleForEdit: false,
      id: '',
      tagName: '',
      pageSizeOptions: ['10', '20', '30'],
      current: 1,
      pageSize: 10,
      total: 0,

      // 表单属性
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      other: '',
      form: {
        name: '',
        desc: '',
        status: true,
      },
      rules: {
        name: [{ required: true, message: '请输入标签名称', trigger: 'change' }],
        desc: [{ required: true, message: '请输入标签描述', trigger: 'change' }],
      },
    }
  },
  computed: {
    params: {
      get() {
        return {
          pageNum: this.current,
          pageSize: this.pageSize,
          tagName: this.tagName,
        }
      },
      set(newValue) {
        this.params.current = newValue
      },
    },
  },
  created() {
    this.queryTagList(this.params)
  },
  methods: {
    // 清空选中的
    clearSelected() {
      this.selectedRowKeys = []
    },
    // 是否选中
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    // 展示详情的Modal
    showModalDetails(data) {
      this.id = data.id
      this.tagName = data.tagName
      this.visibleForDetails = true

      this.form.name = data.tagName
      this.form.desc = data.description
      this.form.status = data.status === 1 ? true : false
    },
    // 处理详情的Modal
    handleOkDetails() {
      this.visibleForDetails = false
    },
    // 展示编辑的Modal
    showModalEdit(data) {
      this.id = data.id
      this.tagName = data.tagName
      this.visibleForEdit = true

      this.form.name = data.tagName
      this.form.desc = data.description
      this.form.status = data.status === 1 ? true : false
    },
    // 编辑
    async editTag() {
      let data = {
        id: this.id,
        tagName: this.form.name,
        description: this.form.desc,
        status: this.form.status === true ? 1 : 0,
      }
      console.log('data===========================', data)
      const res = await modifyTag(data)
      if (res.code === 200) {
        this.$message.success(res.result, 1)
      } else {
        this.$message.error(res.result, 1)
      }
      this.tagName = ''
      this.visibleForEdit = false
      this.queryTagList(this.params)
    },
    // 删除单个标签
    async deleteTagById(id) {
      let params = { id: id }
      const res = await removeTag(params)
      if (res.code === 200) {
        this.$message.success(res.result, 1)
      } else {
        this.$message.error(res.result, 1)
      }
      this.queryTagList(this.params)
    },
    // 分页查询
    async queryTagList(params) {
      this.loading = true
      const res = await getTag(params)
      if (res.code === 200) {
        this.data = res.result.records
        this.current = res.result.current
        this.total = res.result.total
        this.size = res.result.size
      }
      this.loading = false
    },
    // 处理分页
    handlePaginationChange(page, pageSize) {
      this.current = page
      // console.log(page, pageSize)
      const res = getTag(this.params)
      if (res.code === 200) {
        this.data = res.result.records
        this.current = res.result.current
        this.total = res.result.total
        this.size = res.result.size
      }
      this.queryTagList(this.params)
    },
    // 页码变化
    handleShowSizeChange(current, size) {
      // console.log(current, size)
      this.pageSize = size
      this.queryTagList(this.params)
    },

    onSubmit() {
      let _this = this
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          _this.editTag()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
  },
}
</script>