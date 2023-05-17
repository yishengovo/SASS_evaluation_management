import { axios } from '@/utils/request'

const api = {
  getTag: '/client/surTag/query',
  saveTag: '/client/surTag/add',
  modifyTag: '/client/surTag/edit',
  removeTag: '/client/surTag/delete',
  removeTags: '/client/surTag/deleteBatch',
  exportXls: '/client/surTag/exportXls',
}

// 分页查询or模糊查询
export function getTag(data) {
  return axios({
    method: 'post',
    url: api.getTag,
    data
  })
}

// 新增
export function saveTag(data) {
  return axios({
    method: 'post',
    url: api.saveTag,
    data
  })
}

// 编辑
export function modifyTag(data) {
  return axios({
    method: 'post',
    url: api.modifyTag,
    data
  })
}

// 单个删除
export function removeTag(params) {
  return axios({
    method: 'delete',
    url: api.removeTag,
    params
  })
}

// 批量删除
export function removeTags(params) {
  return axios({
    method: 'delete',
    url: api.removeTags,
    params
  })
}


// 导出Xls
// export function exportXls(params) {
//   return axios({
//     method: 'get',
//     url: api.exportXls,
//     params
//   })
// }

// // 查询路径参数
// export function getTenant(param) {
//   return axios({
//     method: 'get',
//     url: api.getTenant + param
//   })
// }

// // put
// export function saveTag2(data) {
//   return axios({
//     method: 'put',
//     url: api.saveTag2,
//     data
//   })
// }

// // 路径参数
// export function getTenant2(param) {
//   return axios({
//     method: 'delete',
//     url: api.getTenant2 + param
//   })
// }