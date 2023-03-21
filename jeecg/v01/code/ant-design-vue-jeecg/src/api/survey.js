/*
 * @Author: August-Rushme
 * @Date: 2022-08-25 17:40:40
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2022-11-08 13:30:34
 * @FilePath: \ant-design-vue-jeecg\src\api\survey.js
 * @Description:
 *
 * Copyright (c) 2022 by August-Rushme 864011713zqy@gmail.com, All Rights Reserved.
 */
import { axios } from '@/utils/request'
const api = {
  getSurveyState: '/survey/surProject/publish',
  setQid: '/survey/survey/setQuestionPid',
  queryTenant: '/survey/survey/tenant'
}

export function getUserList(data) {
  return axios({
    method: 'post',
    url: api.getSurveyState,
    data
  })
}

export function setQuestId(data) {
  return axios({
    method: 'post',
    url: api.setQid,
    data
  })
}

export function getTenant() {
  return axios({
    method: 'get',
    url: api.queryTenant
  })
}
