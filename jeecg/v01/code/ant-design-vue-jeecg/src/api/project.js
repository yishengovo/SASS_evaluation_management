import { axios } from '@/utils/request'
const api = {
  queryById: '/survey/surProject/queryById'
}


export function getCallBackList(params) {
  return axios({
    method: 'get',
    url: api.queryById,
    params
  })
}


export function getProjectList(data) {
  return axios({
    method: 'post',
    url: '/survey/surProject/search',
    data
  })
}