import { getUserList } from '@/api/evaluation'

const evaluation = {
  state: {
    userList: [],
    projectState: false
  },
  mutations: {
    changeUserList(state, res) {
      state.userList = res
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

export default evaluation
