let questions = []

// 处理题目的逻辑
const handleQuestion = question => {
  const questionObj = {
    name: '',
    title: '',
    names: [],
    choices: [],
    type: ''
  }
  questionObj.name = question.name
  questionObj.type = question.type
  // 如果有题目文本
  if (question.title) {
    questionObj.title = question.title['zh-cn']
  }
  // 如果chicos数组里面是对象，则获取value
  if (
    question.choices &&
    question.choices.length > 0 &&
    question.type !== 'imagepicker' &&
    question.type !== 'matrixdropdown'
  ) {
    let choices = []
    question.choices.forEach(item => {
      if (typeof item === 'object') {
        // 替换json中的value
        item.value = item.text['zh-cn']
        choices.push(item.text['zh-cn'])
      } else if (item === '' || item === null) {
        choices = []
      } else {
        choices.push(item)
      }
    })
    questionObj.choices = choices
  } else if (question.type === 'text') {
    if (typeof question.title === 'object') {
      questionObj.name = question.title['zh-cn']
    } else {
      questionObj.name = question.name
    }
    // 评分
  } else if (question.rateValues && question.rateValues.length > 0) {
    question.rateValues.forEach(item => {
      if (typeof item === 'object') {
        item.value = item.text['zh-cn']
        questionObj.choices.push(item.text['zh-cn'])
      } else {
        questionObj.choices.push(item)
      }
    })

    // 矩阵问题
  } else if (question.type === 'matrix') {
    // 如果有题目文本
    if (question.title) {
      questionObj.title = question.title['zh-cn']
    }
    question.columns.forEach(item => {
      if (typeof item === 'object') {
        item.value = item.text['zh-cn']
        questionObj.choices.push(item.text['zh-cn'])
      } else {
        questionObj.choices.push(item)
      }
    })
    questionObj.type = question.type
    question.rows.forEach(item => {
      if (typeof item === 'object') {
        item.value = item.text['zh-cn']
        questionObj.names.push(item.text['zh-cn'])
      } else {
        questionObj.names.push(item)
      }
    })
  } else if (question.type === 'matrixdropdown') {
    // 如果有题目文本
    if (question.title) {
      questionObj.title = question.title['zh-cn']
    }
    question.columns.forEach(item => {
      if (typeof item === 'object' && item.title) {
        questionObj.choices.push(item.title['zh-cn'])
      } else {
        questionObj.choices.push(item.name)
      }
    })
    questionObj.type = question.type
    question.rows.forEach(item => {
      if (typeof item === 'object') {
        item.value = item.text['zh-cn']
        questionObj.names.push(item.text['zh-cn'])
      } else {
        questionObj.names.push(item)
      }
    })
  } else if (question.type === 'multipletext') {
    question.items.forEach(item => {
      if (typeof item === 'object' && item.title) {
        item.name = item.title['zh-cn']
        questionObj.choices.push(item.title['zh-cn'])
      } else {
        questionObj.choices.push(item.name)
      }
    })
  }
  questions.push(questionObj)
}

// 如果是面板问题则递归
const recursivePanel = question => {
  if (question.elements) {
    for (let j = 0; j < question.elements.length; j++) {
      if (question.elements[j].type === 'panel') {
        recursivePanel(question.elements[j])
      } else {
        const panelQuestion = question.elements[j]
        handleQuestion(panelQuestion)
      }
    }
  }
}
export const analysisQuestion = newSurveyJson => {
  questions = []
  // 循环pages获取name和choices
  for (let i = 0; i < newSurveyJson.pages.length; i++) {
    for (const question of newSurveyJson.pages[i].elements) {
      // 如果是面板，则遍历面板里面的题目
      if (question.type === 'panel') {
        try {
          recursivePanel(question)
        } catch (err) {
          console.log(err)
          questions = []
        }
      } else {
        try {
          handleQuestion(question)
        } catch (err) {
          console.log(err)
          questions = []
        }
      }
    }
  }
  return [questions, newSurveyJson]
}
