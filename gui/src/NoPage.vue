<template>
  <Header />
  <div class="box">
    <h2>404 - Page not found!</h2>
  </div>
</template>

<script setup>
import Header from "./components/HeaderCom.vue";
document.title = "MovieArchive | 404";
</script>

<style scoped>
.box {
  text-align: center;
  max-width: 45rem;
  padding: 3rem;
  margin: 4rem auto;
}
</style>