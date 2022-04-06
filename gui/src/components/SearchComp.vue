<template>
  <!-- search -->
  <div id="search-input" class="container">
    <form @submit="search" onsubmit="return false">
      <div class="input-group mb-3">
        <input
          type="text"
          class="form-control"
          placeholder="Welchen Film suche Sie?"
          aria-label="Welchen Film suche Sie?"
          aria-describedby="button-addon2"
          v-model="data.query"
          @input="search"
        />
        <button class="btn btn-primary" type="button" id="button-addon2">
          <i class="bi bi-search"></i> Suchen
        </button>
      </div>
    </form>
  </div>
  <!-- movies -->
  <div class="container">
    <div
      class="box box-movie"
      v-for="(movie, index) in data.movies"
      :key="index"
      @click="showMovie(movie)"
    >
      <h2>{{ movie.name }}</h2>
      <p>Aus dem Jahre {{ movie.year }}</p>
    </div>
  </div>
  <!-- spinner -->
  <div v-if="data.isLoading" class="d-flex justify-content-center">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
  <!-- 404 -->
  <div v-else-if="data.movies.length == 0" class="container">
    <div class="box">
      <h2>Leider keine Filme mit diesem Namen gefunden!</h2>
    </div>
  </div>
  <!-- Modal-->
  <div
    class="modal fade"
    id="movieModal"
    tabindex="-1"
    aria-labelledby="movieModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="movieModalLabel">
            Film - {{ data.currentMovie.name }}
          </h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <!-- Modal-Body -->
          <div class="row">
            <div class="col-2">
              <p>{{ data.currentMovie.name }}</p>
              <p>Zu finden in {{ data.currentMovie.block }}</p>
              <p>Aus dem Jahre {{ data.currentMovie.year }}</p>
              <p>Type: {{ data.currentMovie.type }}</p>
              <a
                v-if="allowIframe(data.currentMovie.wikiUrl)"
                :href="data.currentMovie.wikiUrl"
                >Wikipedia Link</a
              >
              <a
                v-else-if="isVideoBuster(data.currentMovie.wikiUrl)"
                :href="data.currentMovie.wikiUrl"
              >
                VideoBuster Link
              </a>
            </div>
            <div v-if="allowIframe(data.currentMovie.wikiUrl)" class="col">
              <iframe
                :src="data.currentMovie.wikiUrl"
                title="Wikipedia"
              ></iframe>
            </div>
            <div v-else class="col">
              <h2>Leider kein Wikipedia Artikel verzeichnet!</h2>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-danger"
            data-bs-target="#deleteModal"
            data-bs-toggle="modal"
          >
            <i class="bi bi-trash3"></i> Löschen
          </button>
          <button
            @click="showUpdateModal()"
            type="button"
            class="btn btn-primary"
            data-bs-dismiss="modal"
          >
            <i class="bi bi-pencil"></i> Update
          </button>
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            Schließen
          </button>
        </div>
      </div>
    </div>
  </div>
  <UpdateModal :movie="data.currentMovie" />
  <!-- Delete-Confirm Modal -->
  <div
    class="modal fade"
    id="deleteModal"
    tabindex="-1"
    aria-labelledby="deleteModallLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteModalLabel">Bist du sicher?</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <div v-if="!data.delete.hasResponed">
            Du bist dabei den Film '{{ data.currentMovie.name }}' zu entfernen!
          </div>
          <div v-else-if="data.delete.wasSuccessfull">
            Film '{{ data.currentMovie.name }}' wurde erfolglreich entfernt!
          </div>
          <div v-else>
            Leider ist etwas schief gelaufen! Versuchen Sie es bitte später
            erneut. Vilen Dank für das Verständniss.
          </div>
        </div>
        <div class="modal-footer">
          <button
            v-if="!data.delete.hasResponed"
            type="button"
            class="btn btn-secondary"
            data-bs-target="#movieModal"
            data-bs-toggle="modal"
          >
            Abbrechen
          </button>
          <button
            v-if="!data.delete.hasResponed"
            type="button"
            class="btn btn-primary"
            @click="sendDelete()"
          >
            <div
              v-if="data.delete.isSending"
              class="spinner-border spinner-border-sm"
              role="status"
            >
              <span class="visually-hidden">Loading...</span>
            </div>
            Ja, sicher
          </button>
          <button
            v-else
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            Schließen
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { checkTokenAndRun } from "@/tools/Auth";
import { getMovies, searchMovie } from "@/tools/UserMovie";
import { reactive } from "@vue/reactivity";
import { Modal } from "bootstrap";
import { wikiWhiteList, videoBusterList } from "@/tools/SearchList";
import { getMoviePageCount } from "@/tools/PubMovie";
import UpdateModal from "@/components/UpdateModal.vue";
import { deleteMovie } from "@/tools/AdminMovie";

const data = reactive({
  movies: [],
  currentMovie: {
    name: "...",
    year: 0,
    uuid: "...",
    block: "...",
    wikiUrl: "...",
    type: "...",
  },
  query: "",
  isLoading: true,
  lastSearch: 0,
  currentPage: 0,
  maxPageCount: 0,
  delete: {
    isSending: false,
    hasResponed: false,
    wasSuccessfull: false,
  },
});

checkTokenAndRun(() => {
  loadMovies();
  getMoviePageCount().then((count) => {
    data.maxPageCount = count;
  });
});

window.onscroll = function () {
  var scrollPosition =
    document.documentElement.scrollTop || document.body.scrollTop;
  if (
    document.body.scrollHeight - scrollPosition < 1200 &&
    data.currentPage < data.maxPageCount &&
    !data.isLoading &&
    data.query.replace(/\s+/g, "").length == 0
  ) {
    data.currentPage++;
    loadMovies();
  }
};

function search() {
  setTimeout(() => {
    if (data.lastSearch + 500 < new Date().getTime()) {
      startSearch();
    }
  }, 500);
}

function allowIframe(url) {
  return startURLWith(url, wikiWhiteList);
}

function isVideoBuster(url) {
  return startURLWith(url, videoBusterList);
}

function startURLWith(url, domainArray) {
  for (let index = 0; index < domainArray.length; index++) {
    const element = domainArray[index];
    if (url.startsWith(element)) {
      return true;
    }
  }
  return false;
}

function startSearch() {
  if (data.query.replace(/\s+/g, "").length == 0) {
    data.currentPage = 0;
    data.movies = [];
    loadMovies();
  } else {
    data.isLoading = true;
    searchMovie(data.query.replaceAll(" ", "+"))
      .then((movies) => {
        data.movies = movies;
      })
      .catch((e) => {
        console.error(e);
        alert("Something went wrong! " + e);
      })
      .finally(() => {
        data.isLoading = false;
      });
  }
  data.lastSearch = new Date().getTime();
}

function loadMovies() {
  data.isLoading = true;
  getMovies(data.currentPage)
    .then((movies) => {
      data.movies = data.movies.concat(movies);
    })
    .catch((e) => {
      console.error(e);
      alert("Something went wrong! " + e);
    })
    .finally(() => {
      data.isLoading = false;
    });
}

function showMovie(movie) {
  data.currentMovie = movie;
  var modalElement = document.getElementById("movieModal");
  var modal = new Modal(modalElement);
  modal.show();
}

function showUpdateModal() {
  var modalElement = document.getElementById("updateModal");
  var modal = new Modal(modalElement);
  modal.show();
}

function sendDelete() {
  data.delete.isSending = true;
  deleteMovie(data.currentMovie)
    .then(() => {
      data.delete.wasSuccessfull = true;
    })
    .catch((e) => {
      console.error(e);
      data.delete.wasSuccessfull = false;
    })
    .finally(() => {
      data.delete.isSending = false;
      data.delete.hasResponed = true;
    });
}
</script>

<style scoped>
#search-input {
  margin-top: 3rem;
  max-width: 70rem;
}
#search-input input {
  background: unset;
  color: white;
}
iframe {
  width: 100%;
  height: 40rem;
}
.modal-xl {
  max-width: 1500px;
}
.col h2 {
  text-align: center;
}
@media (max-width: 1500px) {
  .modal-xl {
    max-width: 97vw;
  }
}
</style>