<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-11-07 18:13:23
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-12-01 19:37:36
 * @Description: file content
-->
<script setup lang="ts">
import { useVFieldContext } from '/@src/composable/useVFieldContext'

export interface VInputEmits {
  (event: 'update:modelValue', value?: any): void
}
export interface VInputProps {
  raw?: boolean
  modelValue?: any
  trueValue?: boolean
  falseValue?: boolean
}

const vFieldContext = reactive(
  useVFieldContext({
    create: false,
    help: 'VInput',
  })
)
const emits = defineEmits<VInputEmits>()
const props = withDefaults(defineProps<VInputProps>(), {
  modelValue: '',
  trueValue: true,
  falseValue: false,
})
const value = ref(vFieldContext.field?.value ?? props.modelValue)

watch(value, () => {
  emits('update:modelValue', value.value)
})
watch(
  () => props.modelValue,
  () => {
    value.value = props.modelValue
  }
)
const classes = computed(() => {
  if (props.raw) return []

  return ['input']
})
</script>

<template>
  <input
    :id="vFieldContext.id"
    v-model="value"
    :class="classes"
    :name="vFieldContext.id"
    :true-value="props.trueValue"
    :false-value="props.falseValue"
    @change="vFieldContext.field?.handleChange"
    @blur="vFieldContext.field?.handleBlur"
  />
</template>
