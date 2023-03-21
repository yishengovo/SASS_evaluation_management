import { axios } from '@/utils/request'
const api = {
  userList: '/survey/surProject/userList/'
}

export function getUserList(id) {
  return axios({
    method: 'get',
    url: api.userList + id
  })
}
