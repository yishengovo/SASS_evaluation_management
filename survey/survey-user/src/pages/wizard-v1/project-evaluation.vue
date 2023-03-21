<!--
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-04 20:09:25
 * @LastEditors: August Rush 864011713@qq.com
 * @LastEditTime: 2023-01-28 19:21:43
 * @FilePath: \survey-user\src\pages\wizard-v1\project-evaluation.vue
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
-->
<script setup lang="ts">
import { LocationQueryValue } from 'vue-router'
import disableEle from '/@src/components/disableEle.vue'
import {
  addEvaluationPerson,
  deleteEavluation,
  deleteEavluationPerson,
  queryEavluation,
  queryEavluationPerson,
  setEavluation,
  updateEavluation,
  setWeight,
  getWeight,
} from '/@src/api/survey/360survey'
import { IEvaluationPerson } from '/@src/api/survey/type'
import { Notice } from '/@src/components/base/au-notice/Notice'

import { useWizard } from '/@src/stores/wizard'
import { WizardType } from '/@src/models/wizard'
import {
  deleteEvaluationProjectPerson,
  getEvaluationProjectPerson,
  getProject,
  uploadExcel,
} from '/@src/api/createProject/createProject'
import { IEvaluationPersonResult } from '/@src/api/createProject/type'

export interface EvaluationRelation {
  evaluator: string
  superior: string[]
  colleague: string[]
  subordinate: string[]
  isSelf: boolean
}

export interface EvaluationOptions {
  value: string
  label: string
  disabled: boolean
}
interface EvaluationData {
  id: string
  isSelf: boolean
  userId: string
  evaluator: string
  superior: string[]
  colleague: string[]
  subordinate: string[]
  actionSlots?: string
}

const wizard = useWizard()
const router = useRouter()

const isDisable = localStorage.getItem('isView') === 'view'
const projectId = ref<string | LocationQueryValue[] | null>(null)
const toLink = ref('/wizard-v1/project-evaluation')
const isLoaderActive = ref(false)
const projectType = ref<WizardType>()
const exampleExcelUrl = ref('')
projectType.value = wizard.data.type

const getSurevyInfo = async () => {
  isLoaderActive.value = true
  const { data: res } = await getProject(projectId.value as string)
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({ notice_type: 'error', message: '获取数据失败' })
  }
  exampleExcelUrl.value = res.result.url
  wizard.data.id = projectId.value
  wizard.data.name = res.result.project.projectName
  wizard.data.description = res.result.project.content
  wizard.data.type = res.result.project.type
  projectType.value = res.result.project.type
  isLoaderActive.value = false
}
if (router.currentRoute.value.query.id) {
  projectId.value = router.currentRoute.value.query.id
  await getSurevyInfo()
  toLink.value = `/wizard-v1/project-evaluation?id=${projectId.value}`
  console.log(projectType.value)
}
const nameListModal = ref(false)
const textarea = ref('')
const currentTab = ref('manageName')
const modalTitle = ref('名单管理')
const evaluationTitle = ref('添加评价关系')
// const pageTitle = ref('评价关系设置')
// 评价关系数据
const evaluationData = ref<EvaluationData[]>([])
window.onbeforeunload = function () {}

// 监听tab变化
watch(currentTab, (newTab: string) => {
  if (newTab === 'manageName') {
    modalTitle.value = '名单管理'
  } else {
    modalTitle.value = '批量添加名单'
  }
})
const columns = {
  evaluator: {
    label: '被评价者*',
  },
  superior: {
    label: '上级',
  },
  colleague: {
    label: '同级',
  },
  subordinate: {
    label: '下级',
  },
  isSelf: {
    label: '是否自评',
  },
  actionSlots: {
    label: '操作',
    align: 'end',
  },
}

const weightColumnsA = {
  ways: {
    label: '',
  },
  superior: {
    label: '上级',
  },
  colleague: {
    label: '同级',
  },
  subordinate: {
    label: '下级',
  },
  self: {
    label: '自评',
    align: 'center',
  },
  total: {
    label: '合计',
  },
}
const weightColumnsB = {
  ways: {
    label: '',
    grow: true,
  },
  superior: {
    label: '上级',
    align: 'center',
  },
  colleague: {
    label: '同级',
    align: 'center',
  },
  subordinate: {
    label: '下级',
    align: 'center',
  },
  self: {
    label: '自评',
    align: 'center',
  },
  total: {
    label: '合计',
    align: 'end',
  },
}
const weightDataA = [
  {
    ways: '分配方法',
    colleague: '平均分配各级权重',
    total: '100%',
  },
]

const evaluationProjectColumn = {
  name: {
    label: '姓名',
  },
  phone: {
    label: '电话',
  },
  actionSlots: {
    label: '操作',
    align: 'end',
  },
}
const projectPerson = ref<IEvaluationPersonResult[]>([])
// 获取参与测评项目的评估人员
const getProjectPerson = async () => {
  const { data: res } = await getEvaluationProjectPerson(projectId.value as string)
  projectPerson.value = res.result.map((item: any) => {
    return {
      ...item,
      name: item.name ?? '匿名',
      phone: item.phone ?? '未知',
    }
  })
}

//获取评价关系数据
const getEvaluationData = async () => {
  evaluationOptionsSingle.value.forEach((option: EvaluationOptions) => {
    option.disabled = false
  })
  isLoaderActive.value = true
  evaluationData.value = []
  const { data: res } = await queryEavluation(projectId.value as string)
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '获取数据失败',
    })
  }

  res.result.forEach((item: any) => {
    evaluationOptionsSingle.value.forEach((option: EvaluationOptions) => {
      if (option.value === item.userId) {
        option.disabled = true
      }
    })
    evaluationData.value.push({
      id: item.id,
      evaluator: item.userName,
      isSelf: item.isSelf,
      userId: item.userId,
      superior:
        item.superiorList.filter(
          (evaluationItem: any) => evaluationItem.evaluatorLevel === 1
        ) ?? [],
      colleague:
        item.colleagueList.filter(
          (evaluationItem: any) => evaluationItem.evaluatorLevel === 2
        ) ?? [],
      subordinate:
        item.subordinateList.filter(
          (evaluationItem: any) => evaluationItem.evaluatorLevel === 3
        ) ?? [],
    })
  })

  isLoaderActive.value = false
}

// 处理编辑评价关系
const handleEdit = (row: any) => {
  handleEvaluationModalClose()
  currentEvaluationId.value = row.id
  const evaluatorIdArr = ref<any[]>([])
  evaluationTitle.value = '编辑评价关系'
  evaluationRelation.evaluator = row.userId
  evaluatorIdArr.value.push(row.userId)
  evaluationRelation.isSelf = row.isSelf
  row.colleague.forEach((item: any) => {
    evaluationRelation.colleague.push(item.evaluatorId)
    evaluatorIdArr.value.push(item.evaluatorId)
  })
  row.superior.forEach((item: any) => {
    evaluationRelation.superior.push(item.evaluatorId)
    evaluatorIdArr.value.push(item.evaluatorId)
  })
  row.subordinate.forEach((item: any) => {
    evaluationRelation.subordinate.push(item.evaluatorId)
    evaluatorIdArr.value.push(item.evaluatorId)
  })
  evaluationOptions.value.forEach((option: EvaluationOptions) => {
    option.disabled = !!evaluatorIdArr.value.find((item: string) => {
      return item == option.value ? true : false
    })
  })
  evaluationOptionsSingle.value.forEach((option: EvaluationOptions) => {
    option.disabled = !!evaluatorIdArr.value.find((item: string) => {
      return item == option.value ? true : false
    })
  })
  evaluationModal.value = true
}

// 处理删除评价关系
const delEvaluationModal = ref(false)
// 当前评价关系记录的id
const currentEvaluationId = ref('')
const handleDelete = (row: any) => {
  currentEvaluationId.value = row.id
  delEvaluationModal.value = true
}
const handleDelEvaluation = async () => {
  const { data: res } = await deleteEavluation(currentEvaluationId.value)
  if (res.code !== 200) {
    return Notice({
      notice_type: 'error',
      message: '删除失败，请稍后再试',
    })
  }
  Notice({
    notice_type: 'success',
    message: '删除成功',
  })
  if (projectType.value === '360度评估') {
    getEvaluationData()
  } else {
    getProjectPerson()
  }
  delEvaluationModal.value = false
}
// 处理评价关系弹窗关闭
const handleEvaluationModalClose = () => {
  evaluationRelation.colleague = []
  evaluationRelation.evaluator = ''
  evaluationRelation.subordinate = []
  evaluationRelation.superior = []
  evaluationRelation.isSelf = false
  evaluationModal.value = false
  for (let i = 0; i < evaluationOptions.value.length; i++) {
    if (evaluationOptions.value[i].value) {
      evaluationOptions.value[i].disabled = false
    }
  }
}
// 处理添加被评价
const evaluationModal = ref(false)
const evaluationRelation = reactive<EvaluationRelation>({
  evaluator: '',
  superior: [],
  colleague: [],
  subordinate: [],
  isSelf: false,
})
const addEvaluation = () => {
  getEvaluationData()
  evaluationTitle.value = '添加评价关系'
  evaluationModal.value = true
}
// 处理评价关系的选择
const handleEvaluationSelect = (value: string, optionType: string) => {
  if (optionType === 'single') {
    // w
  } else {
    for (let i = 0; i < evaluationOptions.value.length; i++) {
      if (evaluationOptions.value[i].value === value) {
        evaluationOptions.value[i].disabled = true
      }
    }
    for (let i = 0; i < evaluationOptionsSingle.value.length; i++) {
      if (evaluationOptionsSingle.value[i].value === value) {
        evaluationOptionsSingle.value[i].disabled = true
      }
    }
  }
}
// 评价关系的清除
// const handleEvaluationClear = (value: any) => {
//   console.log(value)
//   for (let i = 0; i < evaluationOptionsSingle.value.length; i++) {
//     if (evaluationOptionsSingle.value[i].disabled === true) {
//       evaluationOptionsSingle.value[i].disabled = false
//     }
//   }
// }
// 评价关系的单个取消选择
const handleEvaluationDeselect = (value: any) => {
  const oldValue = value

  for (let i = 0; i < evaluationOptions.value.length; i++) {
    if (evaluationOptions.value[i].value === value) {
      evaluationOptions.value[i].disabled = !evaluationOptions.value[i].disabled
    }
  }
  for (let i = 0; i < evaluationOptionsSingle.value.length; i++) {
    if (oldValue && evaluationOptionsSingle.value[i].value === oldValue) {
      evaluationOptionsSingle.value[i].disabled =
        !evaluationOptionsSingle.value[i].disabled
    }
  }

  getEvaluationData()
}
// 评价关系选择更新时
const handleEvaluationChange = () => {
  const oldValue = evaluationRelation.evaluator
  for (let i = 0; i < evaluationOptionsSingle.value.length; i++) {
    if (oldValue && evaluationOptionsSingle.value[i].value === oldValue) {
      evaluationOptionsSingle.value[i].disabled =
        !evaluationOptionsSingle.value[i].disabled
    }
  }
  for (let i = 0; i < evaluationOptions.value.length; i++) {
    if (oldValue && evaluationOptions.value[i].value === oldValue) {
      evaluationOptions.value[i].disabled = !evaluationOptions.value[i].disabled
    }
  }
}

// 处理保存评价关系
const handleSaveEvaluation = async () => {
  if (evaluationTitle.value === '添加评价关系') {
    if (evaluationRelation.evaluator.length === 0) {
      return Notice({
        notice_type: 'error',
        message: '请选择被评价者',
      })
    } else if (
      evaluationRelation.colleague.length === 0 &&
      evaluationRelation.subordinate.length === 0 &&
      evaluationRelation.superior.length === 0
    ) {
      return Notice({
        notice_type: 'error',
        message: '评价者不能全部为空，请检查',
      })
    }

    const { data: res } = await setEavluation({
      projectId: projectId.value as string,
      ...evaluationRelation,
    })

    if (res.code !== 200) {
      return Notice({
        notice_type: 'error',
        message: '添加失败，请稍后再试',
      })
    }
    Notice({
      notice_type: 'success',
      message: '添加成功',
    })
  } else {
    const { data: res } = await updateEavluation({
      id: currentEvaluationId.value,
      projectId: projectId.value as string,
      ...evaluationRelation,
    })
    if (res.code !== 200) {
      return Notice({
        notice_type: 'error',
        message: '编辑失败，请稍后再试',
      })
    }
    Notice({
      notice_type: 'success',
      message: '编辑成功',
    })
  }

  handleEvaluationModalClose()
  await getEvaluationPerson()
  if (projectType.value === '360度评估') {
    await getEvaluationData()
  }
  evaluationModal.value = false
}
// 评价人员名单
const evaluationPerson = ref<IEvaluationPerson[]>([])
const evaluationOptions = ref<EvaluationOptions[]>([])
const evaluationOptionsSingle = ref<EvaluationOptions[]>([])
// 获取评价人员
const getEvaluationPerson = async () => {
  // 先清空数据
  evaluationPerson.value = []
  evaluationOptions.value = []
  evaluationOptionsSingle.value = []
  isLoaderActive.value = true
  const { data: res } = await queryEavluationPerson(projectId.value as string)
  if (res.code !== 200) {
    isLoaderActive.value = false
    return Notice({
      notice_type: 'error',
      message: '获取数据失败',
    })
  }
  // 初始化值
  evaluationPerson.value = res.result
  evaluationPerson.value.forEach((item: IEvaluationPerson) => {
    evaluationOptions.value.push({
      value: item.id,
      label: item.name as string,
      disabled: false,
    })
    evaluationOptionsSingle.value.push({
      value: item.id,
      label: item.name as string,
      disabled: false,
    })
  })

  isLoaderActive.value = false
}
// 如果有项目id则获取数据
if (projectId.value && projectType.value === '360度评估') {
  getEvaluationPerson()
  getEvaluationData()
} else {
  getEvaluationPerson()
  getProjectPerson()
}

const handleNameList = async () => {
  await getEvaluationPerson()
  nameListModal.value = true
}

// 处理移除标签
const listPerson = ref<string[]>([])
const removeTag = (item: any, index: number) => {
  evaluationPerson.value.splice(index, 1)
}

// 处理保存名单管理
const handleSave = async () => {
  // 清空数据，避免缓存
  listPerson.value = []
  // 批量添加名单
  if (currentTab.value === 'addName') {
    const data = textarea.value.split('\n').filter((item) => item.length > 0)
    const flag = ref(false)
    if (data.length === 0)
      return Notice({ notice_type: 'error', message: '人员名单不能为空' })
    data.forEach((item: string) => {
      if (!item.includes('，')) {
        flag.value = true
        return
      }
    })
    if (flag.value) {
      return Notice({ notice_type: 'error', message: '格式有误，格式为姓名，号码' })
    }
    const { data: res } = await addEvaluationPerson({
      id: projectId.value as string,
      names: data,
    })
    if (res.code !== 200) {
      return Notice({
        notice_type: 'error',
        message: '添加失败，请稍后再试',
      })
    }
    Notice({
      notice_type: 'success',
      message: '添加成功',
    })
    getProjectPerson()
    getEvaluationPerson()
    currentTab.value = 'manageName'
    textarea.value = ''
  } else {
    for (let index = 0; index < evaluationPerson.value.length; index++) {
      if (evaluationPerson.value[index].name) {
        listPerson.value.push(evaluationPerson.value[index].name as string)
      }
    }
    if (projectType.value === '360度评估') {
      // 删除名单中的人
      const { data: res } = await deleteEavluationPerson({
        id: projectId.value as string,
        names: listPerson.value,
      })
      if (res.code !== 200) {
        return Notice({
          notice_type: 'error',
          message: '保存失败，请稍后再试',
        })
      }
      Notice({
        notice_type: 'success',
        message: '保存成功',
      })
      getEvaluationPerson()
      getEvaluationData()
    } else {
      Notice({ notice_type: 'default', message: '保存中，请稍后~' })
      const { data: res } = await deleteEvaluationProjectPerson({
        id: projectId.value as string,
        names: listPerson.value,
      })
      if (res.code !== 200) {
        return Notice({
          notice_type: 'error',
          message: '保存失败，请稍后再试',
        })
      }
      Notice({
        notice_type: 'success',
        message: '保存成功',
      })
      getEvaluationPerson()
      getProjectPerson()
    }
    console.log(projectType.value)
    nameListModal.value = false
  }
}
// 处理选项变化
const tabChange = (tab: string) => {
  currentTab.value = tab
}
// 权重相关
type weightDataBType = {
  ways: string
  superior: string
  colleague: string
  subordinate: string
  self: string
  total: string
  id: number
}
const weightDataB = ref<weightDataBType[]>([
  {
    ways: '默认情况',
    superior: '0',
    colleague: '0',
    subordinate: '0',
    self: '0',
    total: '100%',
    id: 0,
  },
])
const weightModal = ref(false)
const weightSelected = ref('a')
// let MaxId = 2
const handleSettingWeight = () => {
  weightModal.value = true
}
const saveWeightSetting = async () => {
  let canSubMit = true
  if (weightSelected.value === 'b') {
    weightDataB.value.forEach((item) => {
      console.log(item)
      if (
        Number(item.superior) +
          Number(item.colleague) +
          Number(item.subordinate) +
          Number(item.self) !==
        100
      ) {
        canSubMit = false
        return Notice({
          notice_type: 'error',
          message: `检测到权重之和不为100,请修改后保存！`,
        })
      }
    })
  }
  if (canSubMit) {
    weightModal.value = false
    if (weightSelected.value === 'b') {
      const res = await setWeight({
        id: projectId.value as string,
        type: 1,
        self: weightDataB.value[0].self,
        superior: weightDataB.value[0].superior,
        colleague: weightDataB.value[0].colleague,
        subordinate: weightDataB.value[0].subordinate,
      })
      if (res.data.code !== 200) {
        return Notice({
          notice_type: 'error',
          message: '保存失败，请稍后再试',
        })
      }
      Notice({
        notice_type: 'success',
        message: '保存成功',
      })
    } else {
      const res = await setWeight({
        id: projectId.value as string,
        type: 0,
        superior: '33.33',
        colleague: '33.33',
        subordinate: '33.33',
        self: '0',
      })
      if (res.data.code !== 200) {
        return Notice({
          notice_type: 'error',
          message: '保存失败，请稍后再试',
        })
      }
      Notice({
        notice_type: 'success',
        message: '保存成功',
      })
    }
  }
}

// 判断后缀
const checkFile = (file: File) => {
  const suffix = file.name.substring(file.name.lastIndexOf('.'))

  if ('.xls' != suffix && '.xlsx' != suffix) {
    Notice({ notice_type: 'error', message: '选择Excel格式的文件导入！' })
    return false
  }
  return true
}
// 文件上传
const fileName = ref()
const handleFileUpload = async (event: any) => {
  const file = event.target.files[0] as File
  if (!checkFile(file)) {
    return
  }
  fileName.value = file.name
  Notice({ notice_type: 'default', message: '上传中，请稍后~' })
  const { data: res } = await uploadExcel(projectId.value as string, file)
  if (res.code !== 200) {
    return Notice({ notice_type: 'error', message: '上传失败，请稍后再试' })
  }

  Notice({ notice_type: 'success', message: '上传成功' })
  await getEvaluationPerson()
  await getProjectPerson()
  currentTab.value = 'manageName'
}
// const onAddFile = (error: any, fileInfo: any) => {
//   if (error) {
//     console.error(error)
//     return
//   }

//   const _file = fileInfo.file as File
//   if (_file) {
//     checkFile(_file)
//   }
// }

// const onRemoveFile = (error: any, fileInfo: any) => {
//   if (error) {
//     console.error(error)
//     return
//   }
// }

// 处理下载示列excel
const handleDownloadExample = () => {
  const aEl = document.createElement('a')
  aEl.href = exampleExcelUrl.value
  aEl.click()
  document.body.removeChild(aEl)
  console.log('下载')
}
watch(
  () => [
    weightDataB.value[0].self,
    weightDataB.value[0].superior,
    weightDataB.value[0].colleague,
    weightDataB.value[0].subordinate,
  ],
  () => {
    weightDataB.value[0].total =
      Number(weightDataB.value[0].self) +
      Number(weightDataB.value[0].superior) +
      Number(weightDataB.value[0].colleague) +
      Number(weightDataB.value[0].subordinate) +
      '%'
  }
)
// const addWeightRule = (row: weightDataBType) => {
//   const index = weightDataB.value.findIndex((item) => {
//     return item.id === row.id
//   })
//   MaxId = MaxId + 1
//   weightDataB.value.splice(index + 1, 0, {
//     ways: '情况' + (index + 1),
//     superior: 40,
//     colleague: 30,
//     subordinate: 30,
//     total: '100%',
//     id: MaxId,
//   })
// }
// const deleteWeightRule = (row: weightDataBType) => {
//   const index = weightDataB.value.findIndex((item) => {
//     return item.id === row.id
//   })
//   weightDataB.value.splice(index, 1)
// }
wizard.setStep({
  number: 4,
  canNavigate: true,
  previousStepFn: async () => {
    if (projectId.value) {
      router.push(`/wizard-v1/project-details?id=${projectId.value}`)
    } else {
      router.push({
        name: '/wizard-v1/project-details',
      })
    }
  },
  validateStepFn: async () => {
    if (projectId.value) {
      router.push(`/wizard-v1/project-setting?id=${projectId.value}`)
    } else {
      router.push(`/wizard-v1/project-setting`)
    }
  },
})

onMounted(async () => {
  const { data: res } = await getWeight(projectId.value as string)
  if (res.result) {
    if (res.result.type === 0) {
      weightSelected.value = 'a'
    } else {
      weightSelected.value = 'b'
      weightDataB.value[0].colleague = res.result.colleagueWeight
      weightDataB.value[0].subordinate = res.result.subordinateWeight
      weightDataB.value[0].superior = res.result.superiorWeight
      weightDataB.value[0].self = res.result.selfWeight
    }
  }
})
</script>

<template>
  <div id="wizard-step-3" class="inner-wrapper is-active" data-step-title="Preview">
    <div class="step-content">
      <VCard>
        <div class="evaluation">
          <div class="header">
            <div class="evalution-title">
              {{ projectType === '360度评估' ? '评价关系设置' : '参与评估人员设置' }}
            </div>
            <div class="btn-group">
              <VButton class="nameBtn" @click="handleNameList">名单管理</VButton>
              <VButton v-if="projectType === '360度评估'" @click="handleSettingWeight"
                >层级权重</VButton
              >
            </div>
          </div>
          <disableEle :disable="isDisable">
            <VLoader size="large" :active="isLoaderActive" translucent>
              <div class="content">
                <VFlexTable
                  v-if="projectType === '360度评估'"
                  :data="evaluationData"
                  :columns="columns"
                  compact
                  rounded
                  reactive
                >
                  <template #body-cell="{ row, column }">
                    <div v-if="column.key === 'evaluator'">
                      {{ row.evaluator }}
                    </div>
                    <div v-if="column.key === 'superior'">
                      <template v-for="item in row.superior" :key="item.id">
                        {{ item.evaluatorName }} &nbsp;
                      </template>
                    </div>
                    <div v-if="column.key === 'colleague'">
                      <template v-for="item in row.colleague" :key="item.id">
                        {{ item.evaluatorName }} &nbsp;
                      </template>
                    </div>
                    <div v-if="column.key === 'subordinate'">
                      <template v-for="item in row.subordinate" :key="item.id">
                        {{ item.evaluatorName }} &nbsp;
                      </template>
                    </div>
                    <div v-if="column.key === 'isSelf'">
                      {{ row.isSelf === true ? '是' : '否' }}
                    </div>
                    <div v-if="column.key === 'actionSlots'">
                      <VButton
                        color="primary"
                        style="margin-right: 8px"
                        @click="handleEdit(row)"
                      >
                        编辑
                      </VButton>
                      <VButton color="danger" @click="handleDelete(row)"> 删除 </VButton>
                    </div>
                  </template>
                </VFlexTable>
                <VFlexTable
                  v-else
                  :data="projectPerson"
                  :columns="evaluationProjectColumn"
                  compact
                  rounded
                  reactive
                >
                  <template #body-cell="{ column }">
                    <div v-if="column.key === 'actionSlots'">
                      <VButton
                        color="primary"
                        style="margin-right: 8px"
                        @click="handleNameList"
                      >
                        编辑
                      </VButton>
                    </div>
                  </template>
                </VFlexTable>
              </div>
            </VLoader>
            <div v-if="projectType === '360度评估'" class="footer-btn">
              <VButton color="primary" @click="addEvaluation">添加被评价者</VButton>
            </div>
          </disableEle>
        </div>
      </VCard>
      <VModal
        :open="nameListModal"
        size="large"
        actions="right"
        :title="modalTitle"
        @close="nameListModal = false"
      >
        <template #content>
          <disableEle :disable="isDisable">
            <VTabs
              :selected="currentTab"
              :tabs="[
                {
                  label: '名单管理',
                  value: 'manageName',
                  to: toLink,
                },
                {
                  label: '批量添加名单',
                  value: 'addName',
                  to: toLink,
                },
              ]"
              @update:selected="tabChange"
            >
              <template #tab="{ activeValue }">
                <div v-if="activeValue === 'manageName'">
                  <div class="manageContent">
                    <VField v-if="evaluationPerson.length > 0" grouped multiline>
                      <template v-for="(item, index) in evaluationPerson" :key="item.id">
                        <VControl>
                          <VTags addons>
                            <VTag tag :label="item.name ?? '匿名'" />
                            <VTag tag remove @click="removeTag(item, index)" />
                          </VTags>
                        </VControl>
                      </template>
                    </VField>
                    <div v-else style="color: #c2c2c2; font-size: 14px">
                      请添加参与评估人员
                    </div>
                  </div>
                  <span style="color: #aaa; font-size: 12px">
                    注：删除名单将会同时删除数据</span
                  >
                </div>

                <div v-if="activeValue === 'addName'">
                  <VField grouped>
                    <VControl>
                      <div class="file has-name">
                        <label class="file-label">
                          <input
                            class="file-input"
                            type="file"
                            name="resume"
                            @change.stop="handleFileUpload"
                          />
                          <span class="file-cta">
                            <span class="file-icon">
                              <i class="fas fa-cloud-upload-alt"></i>
                            </span>
                            <span class="file-label"> Excel导入</span>
                          </span>
                          <span class="file-name light-text">
                            {{ fileName }}
                          </span>
                        </label>
                      </div>
                    </VControl>
                    <VControl>
                      <VButton
                        color="primary"
                        style="height: 35px"
                        @click="handleDownloadExample"
                        >下载示列Excel</VButton
                      >
                    </VControl>
                  </VField>
                  <!-- <VField>
                    <VControl>
                      <VFilePond
                        class="profile-filepond"
                        name="profile_filepond"
                        :chunk-retry-delays="[500, 1000, 3000]"
                        label-idle="<i class='lnil lnil-cloud-upload'></i>"
                        :accepted-file-types="['image/png', 'image/jpeg', 'image/gif']"
                        :image-preview-height="140"
                        :image-resize-target-width="140"
                        :image-resize-target-height="140"
                        image-crop-aspect-ratio="1:1"
                        style-panel-layout="compact circle"
                        style-load-indicator-position="center bottom"
                        style-progress-indicator-position="right bottom"
                        style-button-remove-item-position="left bottom"
                        style-button-process-item-position="right bottom"
                        @addfile="onAddFile"
                        @removefile="onRemoveFile"
                      />
                    </VControl>
                  </VField> -->
                  <VField>
                    <VControl>
                      <VTextarea
                        v-model="textarea"
                        class="textarea is-info-focus"
                        rows="10"
                        placeholder="请添加所有参与评估人员名单(按照姓名，手机号格式)，一行一个，重名会自动合并"
                      ></VTextarea>
                    </VControl>
                  </VField>
                </div>
              </template>
            </VTabs>
          </disableEle>
        </template>
        <template #action>
          <VButton v-if="!isDisable" color="primary" raised @click="handleSave"
            >保存</VButton
          >
        </template>
        <template #cancel>
          <VButton raised @click="nameListModal = false">取消</VButton>
        </template>
      </VModal>
      <!-- 添加评价关系 -->
      <VModal
        :open="evaluationModal"
        size="large"
        actions="right"
        :title="evaluationTitle"
        @close="handleEvaluationModalClose"
      >
        <template #content>
          <disableEle :disable="isDisable">
            <VField v-slot="{ id }">
              <VLabel>被评者：</VLabel>
              <VControl>
                <Multiselect
                  v-model="evaluationRelation.evaluator"
                  :attrs="{ id }"
                  :options="evaluationOptionsSingle"
                  placeholder="请选择"
                  :can-clear="false"
                  @change="handleEvaluationChange"
                  @select="handleEvaluationSelect"
                />
              </VControl>
            </VField>
            <VField v-slot="{ id }">
              <VLabel>上级：</VLabel>
              <VControl>
                <Multiselect
                  v-model="evaluationRelation.superior"
                  :attrs="{ id }"
                  mode="tags"
                  :options="evaluationOptions"
                  placeholder="请选择"
                  :can-clear="false"
                  @select="handleEvaluationSelect"
                  @deselect="handleEvaluationDeselect"
                />
              </VControl>
            </VField>
            <VField v-slot="{ id }">
              <VLabel>同级：</VLabel>
              <VControl>
                <Multiselect
                  v-model="evaluationRelation.colleague"
                  :attrs="{ id }"
                  mode="tags"
                  :options="evaluationOptions"
                  placeholder="请选择"
                  :can-clear="false"
                  @select="handleEvaluationSelect"
                  @deselect="handleEvaluationDeselect"
                />
              </VControl>
            </VField>
            <VField v-slot="{ id }">
              <VLabel>下级：</VLabel>
              <VControl>
                <Multiselect
                  v-model="evaluationRelation.subordinate"
                  :attrs="{ id }"
                  mode="tags"
                  :options="evaluationOptions"
                  placeholder="请选择"
                  :can-clear="false"
                  @select="handleEvaluationSelect"
                  @deselect="handleEvaluationDeselect"
                />
              </VControl>
            </VField>
            <VField v-slot="{ id }">
              <VLabel>自评：</VLabel>
              <VControl>
                <Multiselect
                  v-model="evaluationRelation.isSelf"
                  :attrs="{ id }"
                  :options="[
                    { value: true, label: '是' },
                    { value: false, label: '否' },
                  ]"
                  placeholder="请选择"
                  :can-clear="false"
                />
              </VControl>
            </VField>
          </disableEle>
        </template>
        <template #action>
          <VButton v-if="!isDisable" color="primary" raised @click="handleSaveEvaluation"
            >保存</VButton
          >
        </template>
        <template #cancel>
          <VButton raised @click="handleEvaluationModalClose">取消</VButton>
        </template>
      </VModal>
      <!-- 删除评价关系 -->
      <VModal
        :open="delEvaluationModal"
        actions="right"
        title="提示"
        @close="delEvaluationModal = false"
      >
        <template #content>
          <VPlaceholderSection title="您确认要删除该评价关系吗？" />
        </template>
        <template #action>
          <VButton color="primary" raised @click="handleDelEvaluation">确定</VButton>
        </template>
        <template #cancel>
          <VButton raised @click="delEvaluationModal = false">取消</VButton>
        </template>
      </VModal>

      <VModal
        :open="weightModal"
        actions="right"
        title="设置权重"
        size="large"
        @close="weightModal = false"
      >
        <template #content>
          <disableEle :disable="isDisable">
            <VControl>
              <VRadio
                v-model="weightSelected"
                value="a"
                label="平均权重分配"
                name="outlined_radio"
                color="primary"
              />

              <VRadio
                v-model="weightSelected"
                value="b"
                label="自定义权重分配"
                name="outlined_radio"
                color="primary"
              />
            </VControl>
            <div class="weight-table">
              <template v-if="weightSelected === 'a'">
                <VFlexTable rounded :data="weightDataA" :columns="weightColumnsA">
                </VFlexTable>
              </template>
              <template v-else>
                <div>
                  <div style="margin-bottom: 10px">
                    请根据实际情况把所有情况列全,如果未设置的情况系统会使用第一条,权重和必须为100%
                  </div>
                  <VFlexTable
                    rounded
                    :data="weightDataB"
                    :columns="weightColumnsB"
                    reactive
                  >
                    <template #body-cell="{ row, column }">
                      <template
                        v-if="
                          column.key === 'superior' ||
                          column.key === 'colleague' ||
                          column.key === 'subordinate' ||
                          column.key === 'self'
                        "
                      >
                        <div class="is-flex">
                          <VField>
                            <VControl>
                              <VInput
                                v-model="row[column.key]"
                                type="text"
                                placeholder="请输入比例"
                              />
                            </VControl>
                          </VField>
                          <div
                            style="padding-left: 5px; padding-top: 5px; font-size: 18px"
                          >
                            %
                          </div>
                        </div>
                      </template>
                      <!-- <template v-if="column.key === 'actionSlots'">
                      <i
                        class="lnir lnir-circle-plus"
                        aria-hidden="true"
                        style="color: var(--primary); font-size: 20px; cursor: pointer"
                        @click="addWeightRule(row)"
                      ></i>
                      <i
                        v-if="row.id !== 0"
                        class="lnir lnir-circle-minus"
                        aria-hidden="true"
                        style="
                          color: var(--danger);
                          font-size: 20px;
                          cursor: pointer;
                          padding-left: 8px;
                        "
                        @click="deleteWeightRule(row)"
                      ></i>
                    </template> -->
                    </template>
                  </VFlexTable>
                </div>
              </template>
              <div
                v-if="weightDataB[0].total !== '100%'"
                style="margin-top: 15px; color: red"
              >
                检测到权重之和不为100%
              </div>
            </div>
          </disableEle>
        </template>
        <template #action>
          <VButton v-if="!isDisable" color="primary" raised @click="saveWeightSetting"
            >保存</VButton
          >
        </template>
      </VModal>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.step-content {
  width: calc(50vw + 10rem);
  margin: 0 auto;
  .evaluation {
    .header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20px;
      .btn-group {
        .nameBtn {
          margin-right: 8px;
        }
      }
    }
    .footer-btn {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
.manageContent {
  height: auto;
  border: 1px solid #dbdbdb;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 5px;
}
.weight-table {
  padding: 15px;
  :deep(.light-text) {
    color: #000;
  }
}
</style>
