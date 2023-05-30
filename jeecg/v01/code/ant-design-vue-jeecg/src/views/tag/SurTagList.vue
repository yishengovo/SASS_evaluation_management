<template>
  <a-card>
    <!-- TagTable -->
    <sur-tag-form></sur-tag-form>
  </a-card>
</template>

<script>
import SurTagForm from './modules/SurTagForm.vue'

export default {
  name: 'SurTagListTest',
  components: {
    SurTagForm,
  },
  data() {
    return {}
  },
}
</script>
<style scoped></style>