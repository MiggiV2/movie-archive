<template>
  <div class="container text-center my-5 pb-5">
    <div>
      <h1 class="mb-5">
        Herzlich Willkommen im Filmarchiv <span class="desktop">von {{ owner }}</span>!
      </h1>
      <div class="m-4">
        <img class="modern-shadow mb-2" src="/img/start_page.webp" />
      </div>
      <div class="container-secondary py-4 mb-2">
        <h4 id="counter">
          Aktuell sind {{ user.movies }} Filme in diesem Archiv verzeichnet!
        </h4>
        <h4 v-if="!user.login">
          Wenn Sie sich einloggen, können Sie alle Filme aufgelistet finden!
        </h4>
        <h4>Bereit das Archive zu durchstöbern?</h4>
      </div>
      <a type="button" class="btn btn-primary btn-lg" role="button" href="/search" v-if="user.login">
        <i class="bi bi-search"></i> Suchen
      </a>
    </div>
  </div>
</template>

<script setup>
import { getCookie } from "@/tools/Cookies";
import { reactive } from "@vue/reactivity";
import { getMovieCount } from "@/tools/api-wrapper/PubMovie";

var owner = process.env.VUE_APP_OWNER;
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
.container {
  cursor: default;
}

h1 {
  margin: 1rem;
}

#counter {
  margin-bottom: 2rem;
}

.container-secondary {
  max-width: 40rem;
}

a.btn {
  margin-top: 2rem;
  width: 40rem;
}

img {
  max-width: 40rem;
  border-radius: var(--bs-border-radius-lg);
}

@media (max-width: 1200px) {
  img {
    max-height: unset;
    max-width: 100%;
  }

  a.btn {
    width: 100%;
  }
}
</style>