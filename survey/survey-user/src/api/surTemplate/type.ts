export interface IGetTemplate {
  pageNum: number
  pageSize: number
  name?: string | undefined
  type: '测评' | '调查' | '360度评估' | '我的'
}
export interface Record {
  id: string
  fid: string
  surName: string
  type: string
  surContent: string
  isShare?: any
  filterModel?: any
  createTime: string
  completed?: any
  surStatus?: any
  collectionAmount?: any
  amounted?: any
  targetUser?: any
  qualityControl?: any
  empUid?: any
  userUid?: any
  surResult?: any
  jsonContent?: any
  jsonPreview: string
  jsonPublish?: any
  publishTime?: any
  surStartDate?: any
  surEndDate?: any
  isUnlocked?: any
  isOnline?: any
  voteType?: any
  orgUid?: any
  tenantUid?: any
  enableDate?: any
  disableDate?: any
  dr?: any
  ts?: any
  createBy?: any
  createName?: any
  createDate: string
  updateBy?: any
  updateName?: any
  updateDate: string
  isPublic: boolean
  isUse: boolean
  tenantId: string
  status?: any
  tenantIdList?: any
  credit?: number
}

export interface ITemplateResult {
  records: Record[]
  total: number
  size: number
  current: number
  orders: any[]
  optimizeCountSql: boolean
  searchCount: boolean
  countId?: any
  maxLimit?: any
  pages: number
}
