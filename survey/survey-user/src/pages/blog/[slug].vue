<script setup lang="ts">
import { useHead } from '@vueuse/head'
import { useApi } from '/@src/composable/useApi'

interface Article {
  id: string
  title: string
  slug: string
  content: string
  comments: string[]
}
const article = ref<Article>()
const loading = ref(false)
const api = useApi()
const router = useRouter()
const route = useRoute()

console.log(route.params)
watchEffect(async () => {
  const currentSlug = (route.params?.slug as string) ?? ''

  loading.value = true
  try {
    const { data } = await api.get<Article[]>(`/articles?slug=${currentSlug}`)

    if (!data?.length) {
      throw new Error('Artcile not found')
    }

    article.value = data[0]
  } catch (error) {
    router.replace({
      name: '/[...all]', // this will match the ./src/pages/[...all].vue route
      params: {
        all: 'article-not-found',
      },
      query: {
        original: router.currentRoute.value.fullPath,
      },
    })
  } finally {
    loading.value = false
  }
})

useHead({
  title: computed(() => article.value?.title ?? 'Loading article...'),
})
</script>

<template>
  <div v-if="loading">Loading article...</div>
  <div v-else class="blog-detail-wrapper">
    <!--
        Page content goes here

        You can see more complete pages content samples from
        files in /src/components/pages directory
      -->

    <h1>{{ article?.title }}</h1>
    <div>{{ article?.content }}</div>
  </div>
</template>

<style lang="" scoped></style>
