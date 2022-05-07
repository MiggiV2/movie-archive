<template>
  <Header />
  <Content />
  <Footer />
</template>

<script setup>
import Header from "@/components/HeaderCom.vue";
import Content from "@/components/SearchComp.vue";
import Footer from "@/components/FooterComp.vue";
document.title = 'MovieArchive | Suche';
</script>