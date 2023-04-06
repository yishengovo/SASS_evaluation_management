<template>
  <div>
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="showModalForAdd">新增</a-button>
      <a-modal v-model="visibleForAdd" title="新增" @ok="onSubmit">
        <!-- 添加标签的表单 -->
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
            <a-switch v-model="form.status" checked-children="启用" un-checked-children="禁用" default-checked />
          </a-form-model-item>
        </a-form-model>
      </a-modal>

      <a-button type="primary" icon="download">导出</a-button>
      <a-upload :showUploadList="false" :multiple="false">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-button type="danger" icon="import" @click="showModalForDelete">删除</a-button>
    </div>
  </div>
</template>

<script>
import { saveTag, removeTags } from '@/api/tag.js'

export default {
  name: 'SurTagButton',
  components: {},
  props: ['selectedIds', 'params'],
  data() {
    return {
      visibleForAdd: false,

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
  methods: {
    // 展示添加对话框
    showModalForAdd() {
      this.visibleForAdd = true
      this.form.name = ''
      this.form.desc = ''
      this.form.status = true
    },
    // 确认添加
    async addTag() {
      let param = {
        tagName: this.form.name,
        description: this.form.desc,
        status: this.form.status === true ? 1 : 0,
      }
      const res = await saveTag(param)
      if (res.code === 200) {
        this.$message.success(res.result, 1)
      } else {
        this.$message.error(res.result, 1)
      }
      this.visibleForAdd = false
      this.$parent.queryTagList(this.params) // 子组件调用父组件的方法
    },

    // 展示删除对话框并且处理删除
    showModalForDelete() {
      let _this = this
      let params = { ids: this.selectedIds.toString() }
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先勾选需要删除的数据！', 2)
      } else {
        this.$confirm({
          title: '确定删除所选中的标签吗?',
          content: '',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          async onOk() {
            const res = await removeTags(params)
            if (res.code === 200) {
              _this.$message.success(res.result, 1)
            } else {
              _this.$message.error(res.result, 1)
            }
            _this.$parent.queryTagList(_this.params)
          },
          onCancel() {},
        })
        _this.selectedIds.length = 0 // 数组置零
      }
    },

    onSubmit() {
      let _this = this
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          _this.addTag()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
  },
}
</script>

<style></style>