/*
 * @Author: “Augsut-Rushme”
 * @Date: 2022-10-10 16:17:34
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-03 21:34:27
 * @FilePath: \survey-user\src\api\survey\360survey.ts
 * @Description:
 *
 * Copyright (c) 2022 by “Augsut-Rushme” 864011713@qq.com, All Rights Reserved.
 */
import { IDataType } from '../type'
import {
  IPersonList,
  IEvaluation,
  IEvaluationPerson,
  ISetEvaluationData,
  ISetWeight,
  evaluateInfo,
  IGetPhoneCode,
  IphoneLogin,
} from './type'
import { useApi } from '/@src/composable/useApi'

const api = useApi()

enum Survey360Api {
  AddEvaluationPerson = '/client/userProject/addPerson',
  QueryEvaluationPerson = '/client/userProject/queryPerson/',
  QueryEvaluation = '/client/userProject/queryEvaluation/',
  SetEvaluation = '/client/userProject/setLevel',
  UpdateEvaluation = '/client/userProject/updateEvaluation',
  DeleteEvaluationPerson = '/client/userProject/removePerson',
  DeleteEvaluation = 'client/userProject/deleteEvaluationRelationship/',
  SetWeight = '/client/userProject/setWeight',
  GetWeight = '/client/userProject/getWeight',
  Identity = '/client/userProject/judge',
  GetPhoneCode = '/client/userProject/getPhoneCode',
  PhoneLogin = '/client/userProject/judge',
}

/**
 * @description: 批量添加360度项目的评价人员
 * @param {IPersonList} data
 * @return {*}
 */
export const addEvaluationPerson = (data: IPersonList): Promise<IDataType> => {
  return api.post(Survey360Api.AddEvaluationPerson, data)
}

/**
 * @description: 查询360度项目的评价人员
 * @param {string} id
 * @return {*}
 */
export const queryEavluationPerson = (
  id: string
): Promise<IDataType<IEvaluationPerson[]>> => {
  return api.get(Survey360Api.QueryEvaluationPerson + id)
}

/**
 * @description: 查询360度项目的查询评价关系
 * @param {string} id
 * @return {*}
 */
export const queryEavluation = (id: string): Promise<IDataType<IEvaluation[]>> => {
  return api.get(Survey360Api.QueryEvaluation + id)
}

/**
 * @description: 设置360度项目的有上下级的评价关系
 * @param {ISetEvaluationData} data
 * @return {*}
 */
export const setEavluation = (data: ISetEvaluationData): Promise<IDataType> => {
  return api.post(Survey360Api.SetEvaluation, data)
}

/**
 * @description: 更新360度项目的有上下级的评价关系
 * @param {ISetEvaluationData} data
 * @return {*}
 */
export const updateEavluation = (data: ISetEvaluationData): Promise<IDataType> => {
  return api.post(Survey360Api.UpdateEvaluation, data)
}

/**
 * @description: 删除一些360度项目的评价人员
 * @param {IPersonList} data
 * @return {*}
 */
export const deleteEavluationPerson = (data: IPersonList): Promise<IDataType> => {
  return api.post(Survey360Api.DeleteEvaluationPerson, data)
}

/**
 * @description: 删除评价关系
 * @param {string} id
 * @return {*}
 */
export const deleteEavluation = (id: string): Promise<IDataType> => {
  return api.delete(Survey360Api.DeleteEvaluation + id)
}

/**
 * @description: 设置权重
 * @param {ISetWeight } data
 * @return {*}
 */
export const setWeight = (data: ISetWeight): Promise<IDataType> => {
  return api.post(Survey360Api.SetWeight, data)
}

/**
 * @description: 设置权重
 * @param {ISetWeight } data
 * @return {*}
 */
export const getWeight = (id: string): Promise<IDataType> => {
  return api.get(Survey360Api.GetWeight + '/' + id)
}

/**
 * @description: 认证身份
 * @param { string } phone
 * @param { string } projetcId
 * @return {*}
 */
export const identityPhone = (
  phone: string,
  projectId: string,
  code?: string
): Promise<IDataType<evaluateInfo>> => {
  return api.post(Survey360Api.Identity, { phone, projectId, code })
}

export const getSPhoneCode = (data: IGetPhoneCode): Promise<IDataType<any>> => {
  return api.post(Survey360Api.GetPhoneCode, data)
}
export const PhoneLogin = (data: IphoneLogin): Promise<IDataType<any>> => {
  return api.post(Survey360Api.PhoneLogin, data)
}
