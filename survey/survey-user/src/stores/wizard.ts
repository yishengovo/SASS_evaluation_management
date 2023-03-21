/*
 * @Author: August-Rushme
 * @Date: 2022-09-24 20:41:01
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-02 23:34:51
 * @FilePath: \survey-user\src\stores\wizard.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import { acceptHMRUpdate, defineStore } from 'pinia'
import { reactive, ref, shallowRef, computed } from 'vue'

/**
 * Using typescript types allow better developer experience
 * with autocompletion and compiler error prechecking
 */
import type { WizardData } from '/@src/models/wizard'
import sleep from '/@src/utils/sleep'

interface WizardStepOptions {
  number: number
  canNavigate?: boolean
  previousStepFn?: () => Promise<void>
  validateStepFn?: () => Promise<void>
}

export const useWizard = defineStore('wizard', () => {
  const step = ref(1)
  const loading = ref(false)
  const canNavigate = ref(false)
  const previousStepFn = shallowRef<WizardStepOptions['previousStepFn'] | null>()
  const validateStepFn = shallowRef<WizardStepOptions['validateStepFn'] | null>()
  const data = reactive<WizardData>({
    name: '',
    description: '',
    type: undefined,
    isPublish: undefined,
    id: null,
    jsonPreview: '',
  })

  const stepTitle = computed(() => {
    if (data.type === '调查') {
      switch (step.value) {
        case 2:
          return '填写项目信息'
        case 3:
          return '设计问卷'
        case 4:
          return '问卷设置'
        case 1:
        default:
          return '选择项目类型'
      }
    } else {
      switch (step.value) {
        case 2:
          return '填写项目信息'
        case 3:
          return '设计问卷'
        case 4:
          return '评价关系'
        case 5:
          return '问卷设置'
        case 1:
        default:
          return '选择项目类型'
      }
    }
  })

  function setLoading(value: boolean) {
    loading.value = value
  }
  function setStep(options?: WizardStepOptions) {
    step.value = options?.number || 1
    canNavigate.value = options?.canNavigate ?? false
    previousStepFn.value = options?.previousStepFn ?? null
    validateStepFn.value = options?.validateStepFn ?? null
  }

  async function save() {
    loading.value = true

    // simulate saving data
    await sleep(2000)

    loading.value = false
  }

  function reset() {
    data.name = ''
    data.description = ''
    data.type = undefined
    data.isPublish = undefined
    data.id = null
    data.jsonPreview = ''
  }

  return {
    canNavigate,
    previousStepFn,
    validateStepFn,
    step,
    loading,
    stepTitle,
    data,
    setLoading,
    setStep,
    save,
    reset,
  } as const
})

/**
 * Pinia supports Hot Module replacement so you can edit your stores and
 * interact with them directly in your app without reloading the page.
 *
 * @see https://pinia.esm.dev/cookbook/hot-module-replacement.html
 * @see https://vitejs.dev/guide/api-hmr.html
 */
if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useWizard, import.meta.hot))
}
