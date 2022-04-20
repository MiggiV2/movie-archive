<template>
    <Header />
    <AuditLogComp />
  <Footer />
</template>

<script setup>
import Header from "@/components/HeaderCom.vue";
import AuditLogComp from "@/components/AuditLogComp.vue";
import Footer from "@/components/FooterComp.vue";
document.title = 'MovieArchive | Auditlog';
</script>