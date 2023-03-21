<script setup lang="ts">
import { useWizard } from '/@src/stores/wizard'
import type { WizardType } from '/@src/models/wizard'

const wizard = useWizard()
const router = useRouter()
wizard.setStep({
  number: 1,
})

const validateStep = (relatedTo: WizardType) => {
  console.log(relatedTo)
  wizard.data.type = relatedTo

  router.push({
    name: '/wizard-v1/project-info',
  })
}
</script>

<template>
  <div id="wizard-step-0" class="inner-wrapper is-active">
    <div class="step-content">
      <div class="step-title">
        <h2 class="dark-inverted">选择项目类型</h2>
      </div>

      <div class="wizard-types">
        <div class="columns">
          <div class="column is-4">
            <div class="wizard-card">
              <img src="/@src/assets/illustrations/wizard/type-1.svg" alt="" />
              <h3 class="dark-inverted">调查项目</h3>
              <p style="height: 3em !important; line-height: 3em !important">
                调查问卷类型项目方便开展调研 .
              </p>
              <div class="button-wrap">
                <VButton
                  color="primary"
                  class="type-select-button"
                  rounded
                  elevated
                  bold
                  @click="validateStep('调查')"
                >
                  创建
                </VButton>
              </div>
              <div class="learn-more-link">
                <a href="#" class="dark-inverted-hover">或者了解更多</a>
              </div>
            </div>
          </div>
          <div class="column is-4">
            <div class="wizard-card">
              <img src="/@src/assets/illustrations/wizard/type-2.svg" alt="" />
              <h3 class="dark-inverted">测评项目</h3>
              <p>测评类项目包括人才测评、职业测评等.</p>
              <div class="button-wrap">
                <VButton
                  color="primary"
                  class="type-select-button"
                  rounded
                  elevated
                  bold
                  @click="validateStep('测评')"
                >
                  创建
                </VButton>
              </div>
              <div class="learn-more-link">
                <a href="#" class="dark-inverted-hover">或者了解更多</a>
              </div>
            </div>
          </div>
          <div class="column is-4">
            <div class="wizard-card">
              <img src="/@src/assets/illustrations/wizard/type-3.svg" alt="" />
              <h3 class="dark-inverted">考核项目</h3>
              <p>考核项目包括360人才考核, 绩效考核等.</p>
              <div class="button-wrap">
                <VButton
                  color="primary"
                  class="type-select-button"
                  rounded
                  elevated
                  bold
                  @click="validateStep('360度评估')"
                >
                  创建
                </VButton>
              </div>
              <div class="learn-more-link">
                <a href="#" class="dark-inverted-hover">或者了解更多</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped></style>
