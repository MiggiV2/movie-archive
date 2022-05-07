<template>
  <Header />
  <div class="box">
    <h2>404 - Page not found!</h2>
    <img
      src="/img/pixel-cells.png"
      alt=""
    />
  </div>
  <Footer />
</template>

<script setup>
import Header from "@/components/HeaderCom.vue";
import Footer from "@/components/FooterComp.vue";
document.title = "MovieArchive | 404";
</script>

<style scoped>
.box {
  text-align: center;
  width: 45rem;
  padding: 3rem;
  margin: 4rem auto;
}
img {
  max-width: 36rem;
  margin-top: 2rem;
}
</style>