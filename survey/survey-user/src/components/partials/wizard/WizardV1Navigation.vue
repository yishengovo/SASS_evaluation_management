<script setup lang="ts">
// import { useDropdown } from '/@src/composable/useDropdown'
import { useWizard } from '/@src/stores/wizard'
import { keRequest } from '/@src/utils/keRequest'
import { deleteProject } from '/@src/api/projectList'
import { WizardData } from '/@src/models/wizard'

// import { onceImageErrored } from '/@src/utils/via-placeholder'
const router = useRouter()
const wizard = useWizard()
let queryId = router.currentRoute.value.query.id
const projectType = ref(wizard.data.type)
watch(wizard.data, (newValue: WizardData) => {
  projectType.value = newValue.type
  console.log(newValue.type)
})

// const dropdownElement = ref<HTMLElement>()
// const dropdown = useDropdown(dropdownElement)
const exitModel = ref(false)

const exitCreat = () => {
  exitModel.value = true
}
const confirmExit = async () => {
  queryId = router.currentRoute.value.query.id
  exitModel.value = false
  const isEdit = localStorage.getItem('isEdit')
  const isView = localStorage.getItem('isView')
  if (isEdit !== 'true') {
    if (queryId && isView !== 'view') {
      await keRequest(async () => {
        const res = await deleteProject(queryId as any)
        return [res]
      }, '退出创建')
    }
  }
  localStorage.removeItem('isView')
  wizard.reset()
  router.push('/home/all-project')
}
</script>

<template>
  <nav class="wizard-navigation">
    <RouterLink to="/" class="wizard-brand">
      <AnimatedLogo width="38px" height="38px" />
    </RouterLink>

    <div class="navbar-item is-wizard-title">
      <span class="title-wrap">
        步骤 {{ wizard.step }}: <span>{{ wizard.stepTitle }}</span>
      </span>
    </div>

    <VDropdown class="wizard-dropdown">
      <template #button="{ toggle }">
        <div
          tabindex="0"
          class="is-trigger"
          @click="toggle"
          @keydown.space.prevent="toggle"
        >
          <i aria-hidden="true" class="iconify" data-icon="feather:chevron-down"></i>
        </div>
      </template>
      <template #content="{ close }">
        <!-- <div v-if="projectType === '调查'">
          <RouterLink
            :class="[
              (wizard.step < 1 && 'is-disabled') ||
                (queryId && 'is-disabled') ||
                (wizard.step >= 3 && 'is-disabled'),
            ]"
            class="dropdown-item kill-drop"
            tabindex="0"
            to="/wizard-v1"
            @click.passive="close"
          >
            步骤 1: 选择项目类型
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 2 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-info?id=' + queryId"
            @click.passive="close"
          >
            步骤 2: 填写项目信息
          </RouterLink>

          <RouterLink
            :class="[wizard.step < 3 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-details?id=' + queryId"
            @click.passive="close"
          >
            步骤 3: 设计问卷
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 4 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-files?id=' + queryId"
            @click.passive="close"
          >
            步骤 4: 问卷设置
          </RouterLink>
        </div> -->
        <div>
          <RouterLink
            :class="[
              (wizard.step < 1 && 'is-disabled') ||
                (queryId && 'is-disabled') ||
                (wizard.step >= 3 && 'is-disabled'),
            ]"
            class="dropdown-item kill-drop"
            tabindex="0"
            to="/wizard-v1"
            @click.passive="close"
          >
            步骤 1: 选择项目类型
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 2 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-info?id=' + queryId"
            @click.passive="close"
          >
            步骤 2: 填写项目信息
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 3 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-details?id=' + queryId"
            @click.passive="close"
          >
            步骤 3: 设计问卷
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 4 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-evaluation?id=' + queryId"
            @click.passive="close"
          >
            步骤 4: 评价关系设置
          </RouterLink>
          <RouterLink
            :class="[wizard.step < 5 && 'is-disabled']"
            class="dropdown-item kill-drop"
            tabindex="0"
            :to="'/wizard-v1/project-files?id=' + queryId"
            @click.passive="close"
          >
            步骤 5: 问卷设置
          </RouterLink>
        </div>
      </template>
    </VDropdown>

    <!-- <div class="navbar-item is-dark-mode">
      <div class="navbar-icon">
        <label class="dark-mode">
          <input
            data-cy="dark-mode-toggle"
            type="checkbox"
            :checked="!darkmode.isDark"
            @change="darkmode.onChange"
          />
          <span></span>
        </label>
      </div>
    </div> -->

    <!-- <div ref="dropdownElement2" class="dropdown is-right dropdown-trigger user-dropdown">
      <div
        tabindex="0"
        class="is-trigger"
        aria-haspopup="true"
        @click="dropdown.toggle"
        @keydown.space.prevent="dropdown.toggle"
      >
        <div class="profile-avatar">
          <img
            class="avatar"
            src="/images/avatars/svg/vuero-1.svg"
            alt=""
            @error.once="onceImageErrored(150)"
          />
        </div>
        <i aria-hidden="true" class="iconify" data-icon="feather:chevron-down"></i>
      </div>
      <div class="dropdown-menu" role="menu">
        <div class="dropdown-content">
          <div class="dropdown-item">
            <p class="is-size-7 dark-inverted">Erik Kovalsky</p>
          </div>
          <a href="#" class="dropdown-item">
            <i aria-hidden="true" class="iconify" data-icon="feather:user"></i>
            <span>Profile</span>
          </a>
          <a class="dropdown-item">
            <i aria-hidden="true" class="iconify" data-icon="feather:edit-2"></i>
            <span>Edit Profile</span>
          </a>
          <a class="dropdown-item">
            <i aria-hidden="true" class="iconify" data-icon="feather:box"></i>
            <span>Projects</span>
          </a>
          <a class="dropdown-item">
            <i aria-hidden="true" class="iconify" data-icon="feather:settings"></i>
            <span>Settings</span>
          </a>
          <hr class="dropdown-divider" />
          <a href="#" class="dropdown-item">
            <i aria-hidden="true" class="iconify" data-icon="feather:log-out"></i>
            <span>Sign Out</span>
          </a>
        </div>
      </div>
    </div> -->
    <div class="exit" @click="exitCreat">
      <div style="cursor: pointer">
        <i class="lnir lnir-cross-circle" aria-hidden="true"></i>
      </div>
    </div>
    <VModal :open="exitModel" actions="center" title="提示" @close="exitModel = false">
      <template #content>
        <VPlaceholderSection title="退出将不保存此次创建的问卷信息，您确定要退出吗?" />
      </template>
      <template #action>
        <VButton color="primary" raised @click="confirmExit()"> 确定</VButton>
      </template>
    </VModal>
  </nav>
</template>

<style lang="scss" scoped>
.wizard-navigation {
  position: fixed;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  height: 60px;
  background: var(--white);
  padding: 0 20px;
  transition: all 0.3s; // transition-all test
  z-index: 99;

  .wizard-brand {
    img {
      display: block;
      height: 34px;
      margin: 0 auto;
    }
  }

  .navbar-item {
    &.is-wizard-title {
      margin-left: 15px;
      border-left: 1px solid var(--muted-grey-light-15);
      padding-bottom: 6px;
      padding-top: 6px;
      font-family: var(--font);

      .title-wrap {
        position: relative;
        display: block;
        font-family: var(--font-alt);
        font-size: 1.2rem;
        font-weight: 600;

        span {
          font-weight: 400;
        }
      }
    }
  }

  .exit {
    position: absolute;
    right: 15px;
    font-size: 30px;
  }
  .wizard-dropdown {
    cursor: pointer;

    .is-trigger {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 30px;
      height: 30px;

      svg {
        height: 18px;
        width: 18px;
        color: var(--light-text);
      }
    }

    .dropdown-menu,
    :deep(.dropdown-menu) {
      border: 1px solid var(--fade-grey-dark-3);
      box-shadow: var(--light-box-shadow);
      border-radius: 8px;
      padding-top: 0;
      overflow: hidden;

      .dropdown-content {
        .dropdown-item {
          font-family: var(--font);
        }
      }
    }
  }

  .is-dark-mode {
    margin-left: auto;
    background: transparent !important;

    .navbar-icon {
      height: 38px;
      width: 38px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: var(--radius-rounded);
      border: 1px solid var(--fade-grey-dark-3);
      box-shadow: var(--light-box-shadow);
      background: var(--white);
      transition: all 0.3s; // transition-all test

      .dark-mode {
        transform: scale(0.6);
      }
    }
  }

  .user-dropdown {
    .is-trigger {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      cursor: pointer;

      .profile-avatar {
        position: relative;

        .avatar {
          display: block;
          width: 38px;
          height: 38px;
          border-radius: var(--radius-rounded);
        }

        .badge {
          position: absolute;
          right: -8px;
          bottom: 0;
          width: 20px;
          height: 20px;
          border: 2px solid var(--white);
          border-radius: var(--radius-rounded);
        }
      }

      svg {
        margin-left: 3px;
        width: 18px;
        height: 18px;
        color: var(--light-text);
        transition: all 0.3s; // transition-all test
      }
    }

    .dropdown-menu {
      top: 52px;
      border: 1px solid var(--fade-grey-dark-3);
      box-shadow: var(--light-box-shadow);
      border-radius: 8px;
      padding-top: 0;
      width: 180px;
      overflow: hidden;

      .dropdown-item {
        display: flex;
        align-items: center;
        font-family: var(--font);
        font-size: 0.9rem;
        padding: 8px 12px;
        color: var(--light-text);

        p {
          font-family: var(--font-alt);
          font-weight: 600;
          font-size: 0.95rem;
          color: var(--dark-text);
        }

        svg {
          margin-right: 8px;
          height: 16px;
          width: 16px;
          color: var(--light-text);
        }
      }
    }
  }
}
</style>
