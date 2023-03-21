/*
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-10-13 20:12:30
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-11-24 15:12:27
 * @Description: file content
 */
export interface IRegister {
  phone: string
  smscode: string
  password: string
}

export interface IGetCode {
  mobile: string
  smsmode: number
}
export interface IGetEmailCode {
  email: string
}
export interface ILogin {
  username: string
  password: string
  captcha: string
  checkKey: number
  loginEnd: number
  realmName?: string
}
export interface IEmailLogin {
  email: string
  emailCode: string
  loginEnd: number
}

export interface IPhoneLogin {
  mobile: string
  captcha: string
  loginEnd: number
}

export interface IBindDomain {
  id?: string
  realmName: string
}
export interface IEmailBind {
  email: string
  emailCode: string
}
export interface IEmailChange {
  email: string
  emailCode: string
  rebindFlag: number
}
export interface IEmailChangeCode {
  email: string
  rebindFlag: number
}
export interface IPhoneChange {
  phone: string
  phoneCode: string
  rebindFlag: number
}
export interface IPhoneChangeCode {
  mobile: string
  rebindFlag: number
}
export interface IResetPassword {
  username: string
  oldpassword: string
  password: string
  confirmpassword: string
}

export interface IResetCompany {
  companyName: string
}

export interface IResetPwdByPhone {
  username: string
  password: string
  smscode: string
  phone: string
}
export interface IResetPwdByEmail {
  username: string
  password: string
  emialcode: string
  email: string
}
export interface IWxPhoneBind {
  mobile: string
  thirdUserUuid: string
  captcha: string
}

export interface IWxGetPhoneCode {
  mobile: string
  smsmode: string
}
export interface IWxLogin {
  token: string
  thirdType: string
  loginEnd: number
}
