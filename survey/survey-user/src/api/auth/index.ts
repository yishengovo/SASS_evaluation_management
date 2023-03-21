/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-13 20:12:24
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-07 21:21:54
 * @Description: file content
 */
import { useApi } from '../../composable/useApi'
import { IDataType } from '../type'
import {
  IRegister,
  IGetCode,
  ILogin,
  IPhoneLogin,
  IGetEmailCode,
  IEmailLogin,
  IBindDomain,
  IEmailBind,
  IEmailChange,
  IEmailChangeCode,
  IPhoneChange,
  IPhoneChangeCode,
  IResetPassword,
  IResetCompany,
  IResetPwdByPhone,
  IResetPwdByEmail,
  IWxPhoneBind,
  IWxGetPhoneCode,
  IWxLogin,
} from './type'
const api = useApi()

enum authApi {
  Login = '/sys/login',
  Register = '/sys/user/register',
  GetCode = '/sys/sms',
  GetImageCode = '/sys/randomImage',
  PhoneLogin = '/sys/phoneLogin',
  GetUserINFO = '/sys/user/getUserInfoByToken',
  GetEmailCode = '/sys/getEmailCode',
  EmailLogin = '/sys/emailLogin',
  GetBindEmailCode = '/sys/user/getEmailCode',
  BindEmail = '/sys/user/bindEmail',
  BindDomain = '/sys/tenant/bindRealName',
  IsDomainExist = '/sys/tenant/existRealmName',
  DomainUserLogin = '/sys/tenantLogin',
  CancleDomianBind = '/sys/tenant/cancelBindRealmName',
  EmailChange = '/sys/user/RebindEmail',
  EmailChangeCode = '/sys/user/getRebindEmailCode',
  PhoneChange = '/sys/rebindPhone',
  PhoneChangeCode = '/sys/getRebindPhoneCode',
  ResetPassword = '/sys/user/updatePassword',
  ResetCompanyName = '/sys/tenant/updateCompanyName',
  ResetPwdByPhone = '/sys/user/passwordChange',
  ResetPwdByEmail = '/sys/user/passwordChangeByEmail',
  WxPhoneBind = '/sys/thirdLogin/bindingThirdPhone',
  WxGetPhoneCode = '/sys/smsWechatOpen',
  WxLogin = '/sys/thirdLogin/getLoginUser',
  WxBind = '/sys/thirdLogin/bindWechatOpen',
  WxDeBind = '/sys/thirdLogin/cancelBindWechatOpen',
}
export const login = (data: ILogin): Promise<IDataType<any>> => {
  return api.post(authApi.Login, data)
}

export const register = (data: IRegister): Promise<IDataType<any>> => {
  return api.post(authApi.Register, data)
}

export const getCode = (data: IGetCode): Promise<IDataType<any>> => {
  return api.post(authApi.GetCode, data)
}
export const getEmailCode = (data: IGetEmailCode): Promise<IDataType<any>> => {
  return api.post(authApi.GetEmailCode, data)
}
export const getImageCode = (data: string): Promise<IDataType<string>> => {
  return api.get(authApi.GetImageCode + '/' + data)
}

export const phoneLogin = (data: IPhoneLogin): Promise<IDataType<any>> => {
  return api.post(authApi.PhoneLogin, data)
}
export const emailLogin = (data: IEmailLogin): Promise<IDataType<any>> => {
  return api.post(authApi.EmailLogin, data)
}

export const getUserInfo = (): Promise<IDataType<any>> => {
  return api.get(authApi.GetUserINFO)
}

export const bindDomain = (data: IBindDomain): Promise<IDataType<any>> => {
  return api.post(authApi.BindDomain, data)
}

export const isDomainExist = (domain: any): Promise<IDataType<any>> => {
  return api.get(authApi.IsDomainExist, {
    params: {
      realmName: domain,
    },
  })
}

export const domainUserLogin = (data: ILogin): Promise<IDataType<any>> => {
  return api.post(authApi.DomainUserLogin, data)
}

export const cancleDomianBind = (): Promise<IDataType<string>> => {
  return api.put(authApi.CancleDomianBind)
}

export const getBindEmailCode = (data: string): Promise<IDataType<string>> => {
  return api.post(authApi.GetBindEmailCode, {
    email: data,
  })
}

export const bindEmail = (data: IEmailBind): Promise<IDataType<any>> => {
  return api.post(authApi.BindEmail, data)
}

export const emailBindChange = (data: IEmailChange): Promise<IDataType<any>> => {
  return api.post(authApi.EmailChange, data)
}

export const emailBindChangeCode = (data: IEmailChangeCode): Promise<IDataType<any>> => {
  return api.post(authApi.EmailChangeCode, data)
}

export const phoneBindChange = (data: IPhoneChange): Promise<IDataType<any>> => {
  return api.post(authApi.PhoneChange, data)
}

export const phoneBindChangeCode = (data: IPhoneChangeCode): Promise<IDataType<any>> => {
  return api.post(authApi.PhoneChangeCode, data)
}

export const resetPassword = (data: IResetPassword): Promise<IDataType<any>> => {
  return api.put(authApi.ResetPassword, data)
}

export const resetCompanyName = (data: IResetCompany): Promise<IDataType<any>> => {
  return api.put(authApi.ResetCompanyName, data)
}
export const resetPwdByPhone = (params: IResetPwdByPhone): Promise<IDataType<any>> => {
  return api.get(authApi.ResetPwdByPhone, {
    params,
  })
}
export const resetPwdByEmail = (params: IResetPwdByEmail): Promise<IDataType<any>> => {
  return api.get(authApi.ResetPwdByEmail, {
    params,
  })
}

export const wxPhoneBindApi = (data: IWxPhoneBind): Promise<IDataType<any>> => {
  return api.post(authApi.WxPhoneBind, data)
}

export const wxGetPhoneCodeApi = (data: IWxGetPhoneCode): Promise<IDataType<any>> => {
  return api.post(authApi.WxGetPhoneCode, data)
}

export const wxLoginApi = (params: IWxLogin): Promise<IDataType<any>> => {
  return api.get(
    authApi.WxLogin + '/' + params.token + '/' + params.thirdType + '/' + params.loginEnd
  )
}
export const wxBindApi = (data: string): Promise<IDataType<any>> => {
  return api.post(authApi.WxBind, {
    uuid: data,
  })
}

export const wxDeBind = (): Promise<IDataType<any>> => {
  return api.delete(authApi.WxDeBind)
}

// export const initDomainApi = (): Promise<IDataType<any>> => {
//   return api.get(authApi.InitDomain)
// }
