<template>
  <div class="container text-center my-5 pb-5">
    <div>
      <h1>
        Willkommen im Filmarchiv <span class="desktop">von {{ owner }}</span>
      </h1>
      <div class="m-4">
        <img class="modern-shadow mb-4" src="/img/start_page.webp" />
      </div>
      <h4 id="counter">
        Aktuell sind {{ user.movies }} Filme in diesem Archiv verzeichnet!
      </h4>
      <h4 v-if="!user.login">
        Wenn Sie sich einloggen, können Sie alle Filme aufgelistet finden!
      </h4>
      <h4>Bereit deinen nächsten Film zu finden?</h4>
      <a type="button" class="btn btn-outline-light btn-lg" role="button" href="/search" v-if="user.login">
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

a.btn {
  margin-top: 2rem;
  width: 30rem;
}

img {
  max-height: 40rem;
  border-radius: 20px;
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