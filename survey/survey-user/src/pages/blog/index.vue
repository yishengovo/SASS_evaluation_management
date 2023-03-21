<script setup lang="ts">
import { useHead } from '@vueuse/head'
import { useApi } from '/@src/composable/useApi'
interface Article {
  id: string
  title: string
  slug: string
}
const api = useApi()
const articles = ref<Article[]>([])
async function fetchArticles() {
  try {
    const { data } = await api.get<Article[]>('/articles')
    articles.value = data
  } catch (error) {
    console.error(error)
  }
}
watchEffect(fetchArticles)
useHead({
  title: 'my blog',
})
</script>
<template>
  <div class="blog-list-wrapper">
    <!-- This is a simple page example -->
    <h1>My blog posts:</h1>
    <ul>
      <li v-for="article in articles" :key="article.id">
        <!-- Here we are linking to the article detail page with a dynamic "slug" parameter -->
        <RouterLink
          :to="{
            name: '/blog/[slug]',
            params: {
              slug: article.slug,
            },
          }"
        >
          {{ article.title }}
        </RouterLink>
      </li>
    </ul>
  </div>
</template>

<style lang="scss" scoped>
.blog-list-wrapper {
  // Here we can add custom styles for the blog page
  // They will be only applied to this component
}
</style>
