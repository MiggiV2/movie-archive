<template>
  <div class="container">
    <div class="box">
      <h1>
        Herzlich Willkommen im Filmarchiv
        <span class="desktop">von {{ owner }}</span>
      </h1>
      <img src="/img/film.jpg" alt="" class="desktop" />
      <img
        class="mobile"
        src="/img/cinema.jpg"
        alt=""
      />
      <h4 id="counter">
        Aktuell sind {{ user.movies }} Filme in diesem Archiv verzeichnet!
      </h4>
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
  </div>
</template>

<script setup>
import { Owner } from "@/main";
import { getCookie } from "@/tools/Cookies";
import { reactive } from "@vue/reactivity";
import { getMovieCount } from "@/tools/api-wrapper/PubMovie";

var owner = Owner;
var user = reactive({
  login: getCookie("refreshToken"),
  movies: 0,
});

getMovieCount().then((count) => {
  if (count != null) {
    if (count < 100) {
      user.movies = 0;
    } else {
      user.movies = count - 100;
    }
    countAnimation(count);
  }
});

function countAnimation(count) {
  if (user.movies < count) {
    setTimeout(() => {
      user.movies++;
      countAnimation(count);
    }, 800 / (count - user.movies));
  }
}
</script>

<style scoped>
h1 {
  margin: 1rem;
}
#counter {
  margin-bottom: 2rem;
}
a.btn {
  margin-top: 2rem;
}
img {
  max-width: 100%;
  margin-bottom: 2rem;
}
</style>