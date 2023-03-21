/*
 * @Author: August-Rushme
 * @Date: 2023-03-13 09:11:10
 * @LastEditors: August-Rushme 864011713zqy@gmail.com
 * @LastEditTime: 2023-03-13 09:20:26
 * @FilePath: \ant-design-vue-jeecg\src\api\dashborad.js
 * @Description:
 *
 * Copyright (c) 2023 by ${git_name_email}, All Rights Reserved.
 */
import { axios } from '@/utils/request'
const api = {
  dashBoradData: '/survey/surProject/dashBord',
}

export function getDashBoradData() {
  return axios({
    method: 'get',
    url: api.dashBoradData,
  })
}
