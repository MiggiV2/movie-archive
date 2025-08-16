<template>
  <div class="container text-center my-5 pb-5">
    <div>
      <h1 class="mb-5">
        Herzlich Willkommen im Filmarchiv<span class="desktop"> von {{ owner }}</span>!
      </h1>
      <div class="my-4">
        <img class="modern-shadow mb-2" src="/img/start_page.webp" />
      </div>
      <div class="container-secondary py-3 mb-2">
        <p id="counter">
          Aktuell sind {{ user.movies }} Filme <span class="desktop">in diesem Archiv </span>verzeichnet!
        </p>
        <p v-if="!user.login">
          Wenn Sie sich einloggen, können Sie alle Filme aufgelistet finden!
        </p>
        <p>Bereit das Archive zu durchstöbern?</p>
      </div>
      <a type="button" class="btn btn-outline-secondary btn-lg" role="button" href="/search" v-if="user.login">
        <i class="bi bi-search"></i> Suchen
      </a>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "@vue/reactivity";
import { getMovieCount } from "@/tools/api-wrapper/PubMovie";
import { getAuthManager } from "@/tools/AuthManager";

var owner = localStorage.getItem("platformOwner");
const mgr = getAuthManager();
var user = reactive({
  login: false,
  movies: 0,
});

updateOwner();

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

mgr?.getUser().then((userData) => {
  if (userData) {
    user.login = true;
  } else {
    user.login = false;
  }
}).catch(err => {
  console.error("Error fetching user data:", err);
  user.login = false;
});

function countAnimation(count) {
  if (user.movies < count) {
    setTimeout(() => {
      user.movies++;
      countAnimation(count);
    }, 800 / (count - user.movies));
  }
}

function updateOwner() {
  owner = localStorage.getItem("platformOwner");
  if (!owner) {
    // wait
    setTimeout(() => updateOwner(), 100);
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

p {
  font-size: 1.1rem;
}

#counter {
  margin-bottom: 1rem;
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

@media (max-width: 768px) {
  img {
    max-height: unset;
    max-width: 100%;
  }

  a.btn {
    width: 100%;
  }
}
</style>