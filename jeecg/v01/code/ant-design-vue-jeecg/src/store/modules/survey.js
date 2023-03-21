import { getUserList } from '@/api/evaluation'

const survey = {
  state: {
    surveyState: {}
  },
  mutations: {
    changeUserList(state, userList) {
      state.userList = userList
    }
  },
  actions: {
    // 获取项目用户列表
    async getUserListAction({ commit }, id) {
      const { result: res } = await getUserList(id)
      commit('changeUserList', res)
    }
  }
}

export default survey
