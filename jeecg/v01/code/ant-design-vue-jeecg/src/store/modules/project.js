import { getCallBackList } from '@/api/project'

const project = {
  state: {
    callback: '',
    isPublished: false
  },
  mutations: {
    changeCallBackList(state, callbackList) {
      state.callback = callbackList
      state.isPublished = callbackList.project.isPublish || false
    }
  },
  actions: {
    // 获取项目回调列表
    async getCallBackListAction({ commit }, params) {
      const { result: res } = await getCallBackList(params)
      commit('changeCallBackList', res)
    }
  }
}

export default project
