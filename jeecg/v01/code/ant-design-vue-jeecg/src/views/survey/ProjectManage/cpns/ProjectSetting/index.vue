<template>
  <div>
    <div class="360Survey" v-if="projectType === '360度评估'">
      <!-- 360度测评问卷步骤 -->
      <a-steps class="360steps" :current="activeKey360">
        <a-step title="设置评价体系" />
        <a-step title="设置评价体系权重" />
        <a-step title="设计问卷" />
        <a-step title="指标设置" />
        <a-step title="问卷设置" />
        <a-step title="完成" />
      </a-steps>
      <div class="content">
        <EvaluationSystem
          v-if="activeKey360 === 0"
          @nextStep360="nextStep360"
          :projectId="projectId"
          :projectType="projectType"
        ></EvaluationSystem>
        <EvaluationWeight
          v-if="activeKey360 === 1"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :isUpAndDown="isUpAndDown"
          :projectId="projectId"
          :projectType="projectType"
        ></EvaluationWeight>
        <!-- <Design360Survey
          v-if="activeKey360 === 2"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></Design360Survey> -->
        <SurveySelect
          v-if="activeKey360 === 2"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :isUpAndDown="isUpAndDown"
          :projectId="projectId"
          :projectType="projectType"
        ></SurveySelect>
        <IndicatorSetting
          v-if="activeKey360 === 3"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></IndicatorSetting>
        <!-- <CompleteUserInfo
          v-if="activeKey360 === 4"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></CompleteUserInfo> -->
        <SurveySetting
          v-if="activeKey360 === 4"
          @nextStep360="nextStep360"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></SurveySetting>
        <DoneResult v-if="activeKey360 === 5" @prevStep="prevStep" @finish="finish"></DoneResult>
      </div>
    </div>
    <div class="survey" v-else>
      <a-steps class="steps" :current="activeKey">
        <a-step title="用户体系" />
        <a-step title="问卷设计" />
        <a-step title="指标设置" />
        <a-step title="问卷设置" />
        <a-step title="完成" />
      </a-steps>
      <div class="content">
        <UserSelect
          v-if="activeKey === 0"
          @nextStep="nextStep"
          :projectId="projectId"
          :projectType="projectType"
        ></UserSelect>
        <SurveySelect
          v-if="activeKey === 1"
          @nextStep="nextStep"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></SurveySelect>
        <IndicatorSetting
          v-if="activeKey === 2"
          @nextStep="nextStep"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></IndicatorSetting>
        <SurveySetting
          v-if="activeKey === 3"
          @nextStep="nextStep"
          @prevStep="prevStep"
          :projectId="projectId"
          :projectType="projectType"
        ></SurveySetting>
        <DoneResult v-if="activeKey === 4" @prevStep="prevStep" @finish="finish"></DoneResult>
      </div>
    </div>
  </div>
</template>
<script>
import SurveySelect from './SurveySelect.vue'
import UserSelect from './UserSelect.vue'
import DoneResult from './DoneResult.vue'
import EvaluationSystem from './EvaluationSystem.vue'
import EvaluationWeight from './EvaluationWeight.vue'
import Design360Survey from './Design360Survey.vue'
import CompleteUserInfo from './CompleteUserInfo.vue'
import IndicatorSetting from './IndicatorSetting.vue'
import SurveySetting from './SurveySetting.vue'

export default {
  props: {
    projectId: {
      type: String,
      default: '',
      required: true
    },
    projectType: {
      type: String,
      default: '',
      required: true
    }
  },
  components: {
    SurveySelect,
    UserSelect,
    DoneResult,
    EvaluationSystem,
    EvaluationWeight,
    Design360Survey,
    CompleteUserInfo,
    IndicatorSetting,
    SurveySetting
  },
  mounted() {},
  data() {
    return {
      isHidden: true,
      activeKey360: 0,
      activeKey: 0,
      isUpAndDown: false
    }
  },
  methods: {
    // handler
    nextStep() {
      if (this.activeKey < 4) {
        this.activeKey += 1
      }
    },
    prevStep(data) {
      this.isUpAndDown = data
      if (this.activeKey > 0 || this.activeKey360 > 0) {
        this.activeKey -= 1
        this.activeKey360 -= 1
      }
    },
    finish() {
      this.activeKey = 0
      this.activeKey360 = 0
    },
    // 360度测评问卷相关
    nextStep360(data) {
      if (data) {
        this.isUpAndDown = data
      } else {
        this.isUpAndDown = false
      }

      if (this.activeKey360 < 5) {
        this.activeKey360 += 1
      }
    }
  }
}
</script>
<style lang="less" scoped></style>
