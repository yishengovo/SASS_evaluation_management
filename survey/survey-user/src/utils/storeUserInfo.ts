import { useUserSession } from '/@src/stores/userSession'
import { useViewWrapper } from '/@src/stores/viewWrapper'
import { setCookie } from '/@src/utils/cookie'
export interface UserInfo {
  id: string
  username: string
  realname: string
  avatar?: any
  birthday?: any
  sex?: any
  email?: any
  phone: string
  orgCode?: any
  orgCodeTxt?: any
  status: number
  delFlag: number
  workNo: string
  post?: any
  telephone?: any
  createBy?: any
  createTime: string
  updateBy: string
  updateTime: string
  activitiSync: number
  userIdentity?: any
  departIds: string
  relTenantIds: string
  clientId?: any
}

export interface OlFormBizType {
  value: string
  text: string
  title: string
  label: string
}

export interface MsgType {
  value: string
  text: string
  title: string
  label: string
}

export interface EoaPlanStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface PositionRank {
  value: string
  text: string
  title: string
  label: string
}

export interface RuleCondition {
  value: string
  text: string
  title: string
  label: string
}

export interface CeshiOnline {
  value: string
  text: string
  title: string
  label: string
}

export interface OnlineGraphDataType {
  value: string
  text: string
  title: string
  label: string
}

export interface DatabaseType {
  value: string
  text: string
  title: string
  label: string
}

export interface OnlineGraphDisplayTemplate {
  value: string
  text: string
  title: string
  label: string
}

export interface LogType {
  value: string
  text: string
  title: string
  label: string
}

export interface UserType {
  value: string
  text: string
  title: string
  label: string
}

export interface Yn {
  value: string
  text: string
  title: string
  label: string
}

export interface SendStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface EoaCmsMenuType {
  value: string
  text: string
  title: string
  label: string
}

export interface TenantStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface BpmProcessType {
  value: string
  text: string
  title: string
  label: string
}

export interface FormPermsType {
  value: string
  text: string
  title: string
  label: string
}

export interface ValidStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface UrgentLevel {
  value: string
  text: string
  title: string
  label: string
}

export interface DelFlag {
  value: string
  text: string
  title: string
  label: string
}

export interface UserStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface OperateType {
  value: string
  text: string
  title: string
  label: string
}

export interface QuartzStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface CgformTableType {
  value: string
  text: string
  title: string
  label: string
}

export interface IsOpen {
  value: string
  text: string
  title: string
  label: string
}

export interface MenuType {
  value: string
  text: string
  title: string
  label: string
}

export interface Sex {
  value: string
  text: string
  title: string
  label: string
}

export interface MsgCategory {
  value: string
  text: string
  title: string
  label: string
}

export interface OrgCategory {
  value: string
  text: string
  title: string
  label: string
}

export interface PermsType {
  value: string
  text: string
  title: string
  label: string
}

export interface GlobalPermsType {
  value: string
  text: string
  title: string
  label: string
}

export interface Priority {
  value: string
  text: string
  title: string
  label: string
}

export interface DictItemStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface OnlineGraphType {
  value: string
  text: string
  title: string
  label: string
}

export interface ActivitiSync {
  value: string
  text: string
  title: string
  label: string
}

export interface MsgSendStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface MsgType2 {
  value: string
  text: string
  title: string
  label: string
}

export interface BpmStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface EoaPlanType {
  value: string
  text: string
  title: string
  label: string
}

export interface DepartStatu {
  value: string
  text: string
  title: string
  label: string
}

export interface Status {
  value: string
  text: string
  title: string
  label: string
}

export interface SysAllDictItems {
  ol_form_biz_type: OlFormBizType[]
  msgType: MsgType[]
  eoa_plan_status: EoaPlanStatu[]
  position_rank: PositionRank[]
  rule_conditions: RuleCondition[]
  ceshi_online: CeshiOnline[]
  online_graph_data_type: OnlineGraphDataType[]
  database_type: DatabaseType[]
  online_graph_display_template: OnlineGraphDisplayTemplate[]
  log_type: LogType[]
  user_type: UserType[]
  yn: Yn[]
  send_status: SendStatu[]
  eoa_cms_menu_type: EoaCmsMenuType[]
  tenant_status: TenantStatu[]
  bpm_process_type: BpmProcessType[]
  form_perms_type: FormPermsType[]
  valid_status: ValidStatu[]
  urgent_level: UrgentLevel[]
  del_flag: DelFlag[]
  user_status: UserStatu[]
  operate_type: OperateType[]
  quartz_status: QuartzStatu[]
  cgform_table_type: CgformTableType[]
  is_open: IsOpen[]
  menu_type: MenuType[]
  sex: Sex[]
  msg_category: MsgCategory[]
  org_category: OrgCategory[]
  perms_type: PermsType[]
  global_perms_type: GlobalPermsType[]
  priority: Priority[]
  dict_item_status: DictItemStatu[]
  online_graph_type: OnlineGraphType[]
  activiti_sync: ActivitiSync[]
  msgSendStatus: MsgSendStatu[]
  msg_type: MsgType2[]
  bpm_status: BpmStatu[]
  eoa_plan_type: EoaPlanType[]
  depart_status: DepartStatu[]
  status: Status[]
}

export interface TenantList {
  id: number
  name: string
  createBy?: any
  createTime: string
  beginDate?: any
  endDate?: any
  status: number
  realmName: string
}

export interface RootObject {
  multi_depart: number
  userInfo: UserInfo
  sysAllDictItems: SysAllDictItems
  tenantList: TenantList[]
  departs: any[]
  token: string
}
const userSession = useUserSession()
const viewWrapper = useViewWrapper()
export const storeUserInfo = async (data: RootObject) => {
  userSession.setToken(JSON.stringify(data.token))
  userSession.setTenantId(data.tenantList[0].id + '')
  userSession.setUser({
    ...data.userInfo,
    name: data.tenantList[0].name,
    domain: data.tenantList[0].realmName,
  })
  viewWrapper.setPageTitle(data.tenantList[0].name)
  setCookie('hrtools_token', data.token, 10)
  setCookie('hrtools_tanantId', data.tenantList[0].id + '', 10)
}
