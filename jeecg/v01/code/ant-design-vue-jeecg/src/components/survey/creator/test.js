const questionsObj = {}
questions.forEach(question => {
  if (question.names.length > 0) {
    question.names.forEach(item => {
      questionsObj[item] = question
    })
  } else {
    questionsObj[question.name] = question
  }
})
const resObj = {}
res.forEach(item => {
  resObj[item.content] = item
})
console.log(questionsObj)
console.log(resObj)
const questionsArr = Object.keys(questionsObj)
const resArr = Object.keys(resObj)
//  !resArr.includes(item)
const questionsDiff = questionsArr.filter(question => {
  return !resArr.includes(question)
})
const resDiff = resArr.filter(item => !questionsArr.includes(item))
// 如果只改变了选项
if (questionsDiff.length == 0 || resDiff.length == 0) {
  for (const key in questionsObj) {
    const choices = questionsObj[key].choices
    const choice = resObj[key].choice
    console.log(choices)
    console.log(choice)
    // 比较choices和choice的不同,如果不同则将keypush到questionsDiff中
    if (choices.length !== choice.length) {
      // 比较长度
      if (!questionsDiff.includes(key)) {
        questionsDiff.push(key)
      }
    } else {
      // 比较内容
      for (let i = 0; i < choices.length; i++) {
        if (choices[i] !== choice[i].choice && !questionsDiff.includes(key)) {
          questions.forEach(item => {
            if (item.type === 'matrix') {
              questionsDiff.push(key)
            }
          })
          break
        } else if (choices[i] !== choice[i].choice && !questionsDiff.includes(key)) {
          questionsDiff.push(key)
        }
      }
    }
  }
}

console.log(questionsDiff)
console.log(resDiff)
if (questionsDiff.length > 0 && resDiff.length > 0) {
  // 更新数据库
  const questionsUpdate = []
  questionsDiff.forEach((itemQuestion, i) => {
    questionsUpdate.push({
      id: resObj[resDiff[i]].id,
      questionId: resObj[resDiff[i]].questionId,
      content: questionsObj[itemQuestion].name,
      type: questionsObj[itemQuestion].type,
      choices: questionsObj[itemQuestion].choices,
      names: questionsObj[itemQuestion].names
    })
  })
  console.log(questionsUpdate)
} else {
  const questionsUpdate = []
  questionsDiff.forEach((itemQuestion, i) => {
    questionsUpdate.push({
      id: resObj[questionsDiff[i]].id,
      questionId: resObj[questionsDiff[i]].questionId,
      content: questionsObj[itemQuestion].name,
      type: questionsObj[itemQuestion].type,
      choices: questionsObj[itemQuestion].choices,
      names: questionsObj[itemQuestion].names
    })
  })
  console.log(questionsUpdate)
}
