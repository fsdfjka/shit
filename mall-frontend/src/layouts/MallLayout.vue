<script setup lang="ts">
/* 商城前台布局：纸面底，导航栏在页面内（如 Home）；此处只做容器 */
</script>

<template>
  <div class="mall-layout">
    <main class="mall-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.mall-layout {
  min-height: 100vh;
  background: var(--md-color-bg);
}
</style>
