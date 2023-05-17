<template>
  <div class="table-operator">
    <a-form :layout="formLayout">
      <a-form-item label="标签标签">
        <a-input placeholder="输入标签名称查询" v-model="inputTagName" />
      </a-form-item>

      <a-form-item label="标签描述">
        <a-input placeholder="输入标签描述查询" v-model="inputTagDesc" />
      </a-form-item>

      <a-form-item label="标签描述">
        <a-select
          placeholder="选择标签状态"
          style="width: 200px"
          @change="handleChange"
          :allow-clear="true"
          v-model="selectedStatus"
        >
          <a-select-option value="1"> 启用 </a-select-option>
          <a-select-option value="0"> 禁用 </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" @click="queryList"> <a-icon type="search" />查询 </a-button>
        <a-button type="primary" @click="clearInput"> <a-icon type="reload" />重置 </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
export default {
  name: 'SurTagQuery',
  component: {},
  props: ['pageSize'],
  data() {
    return {
      formLayout: 'inline',
      inputTagName: '',
      inputTagDesc: '',
      selectedStatus: undefined,
    }
  },
  methods: {
    clearInput() {
      this.inputTagName = ''
      this.inputTagDesc = ''
      this.selectedStatus = undefined

      // console.log('clear:  this.inputTagName=======', this.inputTagName)
      // console.log('clear:  this.inputTagDesc=======', this.inputTagDesc)
      // console.log('clear:  this.selectedStatus=======', this.selectedStatus)

      this.queryList()
    },
    queryList() {
      let params = {
        pageNum: 1,
        pageSize: this.pageSize,
        tagName: this.inputTagName,
        description: this.inputTagDesc,
        status: this.selectedStatus,
      }

      this.$parent.queryTagList(params)
    },

    handleChange(value) {
      this.selectedStatus = value
      console.log(`selected ${value}`)
    },
  },
}
</script>


<style></style>