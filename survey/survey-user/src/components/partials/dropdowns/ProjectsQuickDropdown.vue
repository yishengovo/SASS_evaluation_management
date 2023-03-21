<!--
 * @Author: zhuzhijun m,./1234
 * @Date: 2022-09-23 11:21:25
 * @LastEditors: zhuzhijun
 * @LastEditTime: 2022-10-20 20:14:25
 * @Description: file content
-->
<script setup lang="ts">
import { useUserSession } from '/@src/stores/userSession'
const userSession = useUserSession()
const name = ref(userSession.user?.name.slice(0, 1))
</script>

<template>
  <VAvatar
    size="small"
    color="h-green"
    :initials="name"
    tabindex="0"
    style="margin-right: 10px"
  />
</template>
