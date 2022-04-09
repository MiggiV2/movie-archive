<template>
  <Header />
  <div class="box">
    <h2>404 - Page not found!</h2>
    <img
      src="/img/pixel-cells.png"
      alt=""
    />
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
img {
  max-width: 36rem;
  margin-top: 2rem;
}
</style>