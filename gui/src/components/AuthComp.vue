<template>
  <div v-if="!showWarning.status">
    <h2>Bitte warten...</h2>
    <div class="text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
  <div v-else>
    <h2>Login fehlgeschlagen</h2>
  </div>
</template>

<script setup>
import { getAuthManager } from "@/tools/AuthManager";
import { onMounted } from "vue";
import { useRouter } from 'vue-router'

const router = useRouter();
const { reactive } = require("vue");

var showWarning = reactive({
  status: false
});

onMounted(() => {
  const mgr = getAuthManager();
  mgr?.signinCallback().then((user) => {
    console.log("Sign-in callback successful", user.profile.name);

    const adminGroup = localStorage.getItem("adminRole");
    localStorage.setItem("is_admin", user.profile.groups.includes(adminGroup));

    router.push("/");
  }).catch(err => {
    console.error("Error in sign-in callback:", err);
    showWarning.status = true;
  });
});
</script>

<style scoped>
h2 {
  margin-top: 2rem;
  text-align: center;
}

p {
  text-align: center;
}
</style>
