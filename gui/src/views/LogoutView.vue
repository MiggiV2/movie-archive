<template>
<div class="box">
  <h2>Sie konnte nicht erneut eingeloggt werden.</h2>
  <img src="https://cdn.pixabay.com/photo/2014/04/02/10/35/panda-303949_1280.png">
  <p>Sie werden ausgeloggt und weiter geleitet in {{timer.time}}.</p>
</div>
  <Footer/>
</template>

<script setup>
import Footer from "@/components/FooterComp.vue";
document.title = 'MovieArchive | Bitte warten...';

import {reactive} from "@vue/reactivity";
import { openLogout } from "@/tools/Auth";

var timer = reactive({
  time: 3
});

decreaseTimer();

function decreaseTimer() {
  setTimeout(() => {
    timer.time -= 1;
    if(timer.time > 0) {
      decreaseTimer();
    } else {
      openLogout();
      window.location = "/";
      }
  },1000);
}
</script>


<style scoped>
img {
  max-width: 40vh;
  margin-bottom: 2rem;
  margin-top: 1rem;
}
</style>