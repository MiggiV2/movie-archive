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
      <h2>Hacking Versuch im Gange!</h2>
      <p>Schließen Sie sofort alle anderen Browser Tabs und löschen Sie ihre Cookies!</p>
  </div>
</template>

<script setup>
const { login } = require("@/tools/Auth");
const { getCookie } = require("@/tools/Cookies");
const { reactive }=require("@vue/reactivity");

const urlParams = new URLSearchParams(window.location.search);
const code = urlParams.get("code");
var showWarning = reactive({
    status: false
});

if (code == null || code.length < 10) {
  alert("Failed to login! PS: Need code param");
} else {
  if (getCookie("state") != urlParams.get("state")) {
    titleWarning();
    showWarning.status = true;
  } else {
    login(code);    
  }
}

function titleWarning() {
  setTimeout(() => {
    document.title = "Achtung!!!";
  }, 500);
  setTimeout(() => {
    document.title = "Hacking Versuch!";
    titleWarning();
  }, 1000);
}
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