<script setup lang="ts">
import { useWizard } from '/@src/stores/wizard'
// import { tools } from '/@src/data/wizard'

const wizard = useWizard()
const router = useRouter()
wizard.setStep({
  number: 6,
  canNavigate: true,
  previousStepFn: async () => {
    router.push({
      name: '/wizard-v1/project-team',
    })
  },
  validateStepFn: async () => {
    router.push({
      name: '/wizard-v1/project-review',
    })
  },
})

const currentControl = ref(0)
const controls = ['基础设置', '答题限制', '提交限制']
const changeControl = (index: number) => {
  const progressEle = document.querySelector('.control-progress') as HTMLDivElement
  const transformPx = index * 60
  progressEle.style.transform = `translate3d(0, ${transformPx}px, 0)`
  console.log(progressEle)
  currentControl.value = index
}
</script>

<template>
  <div id="wizard-step-5" class="inner-wrapper is-active">
    <div class="step-content">
      <div class="left-progress">
        <div class="left-text">
          <template v-for="(item, index) in controls" :key="item">
            <div
              :class="currentControl === index ? 'active' : ''"
              @click="changeControl(index)"
            >
              {{ item }}
            </div>
          </template>
        </div>
        <div class="progress-container">
          <div class="control-progress"></div>
        </div>
      </div>
      <div class="right-content">
        <VCard class="card">
          <div class="card-title">基础设置</div>
          <div class="item">
            <div>时间控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
          <div class="item">
            <div>密码控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
        </VCard>
        <VCard class="card">
          <div class="card-title">答题设置</div>
          <div class="item">
            <div>设备控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
          <div class="item">
            <div>IP地址控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
          <div class="item">
            <div>微信控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
        </VCard>
        <VCard class="card">
          <div class="card-title">答题设置</div>
          <div class="item">
            <div>设备控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
          <div class="item">
            <div>IP地址控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
          <div class="item">
            <div>微信控制</div>
            <VControl subcontrol>
              <VSwitchBlock color="primary" />
            </VControl>
          </div>
        </VCard>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.step-content {
  display: flex;
  width: 800px;
  margin: 0 auto;
  .left-progress {
    display: flex;
    align-items: center;
    height: 180px;
    margin-right: 5rem;
    .left-text {
      margin-right: 20px;
      margin-top: 10px;
      .active {
        color: var(--primary);
      }
      div {
        cursor: pointer;
        margin-bottom: 30px;
      }
    }
    .progress-container {
      height: 180px;
      width: 6px;
      border-radius: 3px;
      background-color: #dfdfdf;
      .control-progress {
        width: 6px;
        border-radius: 3px;
        height: 60px;
        background-color: #6ac29b;
        transform: translate3d(0, 0, 0);
        transition: all 0.3s ease;
      }
    }
  }
  .right-content {
    flex: 1;
    .card {
      margin-bottom: 20px;
      padding-bottom: 40px;
      .card-title {
        font-weight: 700;
        font-size: 16px;
        padding: 15px 0;
        border-bottom: 1px solid var(--border);
      }
      .item {
        display: flex;
        justify-content: space-between;
        padding: 15px 0;
        border-bottom: 1px solid var(--border);
      }
    }
  }
}
</style>
