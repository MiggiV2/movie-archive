<template>
  <div class="box">
    <h1>Herzlich Willkommen im Filmarchiv von {{ owner }}</h1>
  </div>
  <div class="box">
    <h4>Aktuell sind {{ user.movies }} in dem Archiv verzeichnet!</h4>
  </div>
  <div class="box">
    <h4 v-if="!user.login">
      Wenn Sie sich einloggen, können Sie alle Filme aufgelistet finden!
    </h4>
    <h4>Die Filme können sortiert und gefiltert werden.</h4>
    <h4>Auch können die Einträge durchsucht werden!</h4>
    <a
      type="button"
      class="btn btn-outline-light btn-lg"
      role="button"
      href="/search"
      v-if="user.login"
      >Suchen <i class="bi bi-search"></i>
    </a>
  </div>
</template>

<script setup>
import { Owner } from "@/main";
import { getCookie } from "@/tools/Cookies";
import { reactive } from "@vue/reactivity";
import { getMovieCount } from "@/tools/PubMovie";

var owner = Owner;
var user = reactive({
  login: getCookie("refreshToken"),
  movies: 0,
});

getMovieCount().then((count) => {
  if (count != null) {
    user.movies = count - 12;
    countAnimation(count);
  }
});

function countAnimation(count) {
  if (user.movies < count) {
    setTimeout(() => {
      user.movies += 3;
      countAnimation(count);
    }, 200);
  }
}
</script>