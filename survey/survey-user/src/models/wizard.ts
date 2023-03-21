/*
 * @Author: August-Rushme
 * @Date: 2022-09-21 10:59:52
 * @LastEditors: “Augsut-Rushme” 864011713@qq.com
 * @LastEditTime: 2022-10-08 19:31:48
 * @FilePath: \survey-user\src\models\wizard.ts
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
/**
 * Theses types are used by the Wizard form
 *
 * @see /src/pages/wizard-v1.vue
 * @see /src/components/partials/wizard
 */

import { LocationQueryValue } from 'vue-router'

export type WizardType = '调查' | '测评' | '360度评估' | undefined
export type WizardBudget = '< 5K' | '< 30K' | '< 100K' | '100K+'
export type WizardTeammateRole = 'reader' | 'collaborator' | 'manager' | 'owner'

export interface WizardTimeFrame {
  start: Date
  end: Date
}
export interface WizardTeammate {
  name: string
  picture: string
  role: WizardTeammateRole
}
export interface WizardCustomer {
  name: string
  logo: string
  location: string
}
export interface WizardTool {
  name: string
  logo: string
  description: string
}

export interface WizardAttachement {
  name: string
  size: number
  type: string
  dataURL?: string
  upload: {
    uuid: string
    url?: string
  }
}
export interface WizardData {
  name: string
  description: string
  type: WizardType
  id?: string | null | LocationQueryValue[]
  isPublish: boolean | undefined
  jsonPreview?: string
}
