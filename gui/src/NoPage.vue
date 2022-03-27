<template>
  <Header />
  <div>
    <h2>404 - Page not found!</h2>
  </div>
</template>

<script setup>
import Header from "./components/HeaderCom.vue";
document.title = 'MovieArchive | 404';
</script>

<style scoped>
h2 {
  text-align: center;
  max-width: 30rem;
  margin: 4rem auto;
}
</style>