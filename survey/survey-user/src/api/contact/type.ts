export interface IEditGroup {
  name: string
  id: string
}

// result
export interface IGroupResult {
  id: string
  name: string
  createTime: string
  tenantId: number
  count: number
}
export interface SurContact {
  id: string
  name: string
  phone: string
  email: string
  address?: any
  groupIds: string
  sex?: any
  birthday?: any
  company?: any
  position?: any
  remarks?: any
  fixedPhone?: any
  source: string
  recentContact?: any
  createTime: string
  updateTime: string
  tenantId: number
  groupNameList: string[]
}

export interface SurContactFieldInformationList {
  id: string
  contactId: string
  fieldId: string
  information: string
  createTime: string
  tenantId: number
  fieldName: string
}

export interface SurGroupContactList {
  id: string
  groupId: string
  contactId: string
  createTime: string
  tenantId: number
}

export interface contactResult {
  surContact: SurContact
  surContactFieldInformationList: SurContactFieldInformationList[]
  surGroupContactList: SurGroupContactList[]
}

export interface IGetContact {
  groupId?: string
  seachName?: string
}
