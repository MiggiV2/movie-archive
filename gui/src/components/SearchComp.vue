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
        <span class="input-group-text desktop" id="basic-addon1"
          >Sortierung</span
        >
        <select
          @change="setSortIDAndLoad($event)"
          class="form-select desktop"
          aria-label="Default select example"
        >
          <option value="0" selected>Keine Sortierung</option>
          <option value="1">Alphapethisch \/</option>
          <option value="2">Alphapethisch /\</option>
          <option value="3">Jahr \/</option>
          <option value="4">Jahr /\</option>
        </select>
        <button
          class="btn btn-primary mobile"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#collapseMenu"
          aria-expanded="false"
          aria-controls="collapseMenu"
        >
          Sortieren <i class="bi bi-wrench"></i>
        </button>
      </div>
    </form>
  </div>
  <!--mobile-sort-content-->
  <div class="container collapse" id="collapseMenu">
    <div class="card card-body">
      <select
        @change="setSortIDAndLoad($event)"
        class="form-select"
        aria-label="Default select example"
      >
        <option value="0" selected>Keine Sortierung</option>
        <option value="1">Alphapethisch \/</option>
        <option value="2">Alphapethisch /\</option>
        <option value="3">Jahr \/</option>
        <option value="4">Jahr /\</option>
      </select>
    </div>
  </div>
  <!-- movies -->
  <div id="movies" class="container">
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
            <div class="col">
              <p>{{ data.currentMovie.name }}</p>
              <p>Zu finden in {{ data.currentMovie.block }}</p>
              <p>Aus dem Jahre {{ data.currentMovie.year }}</p>
              <p>Type: {{ data.currentMovie.type }}</p>
              <p class="wikis">
                <a
                  class="btn btn-primary"
                  v-if="allowIframe(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl"
                  >Seite auf Wikipedia öffnen</a
                >
                <a
                  class="btn btn-primary"
                  v-else-if="isVideoBuster(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl"
                >
                  VideoBuster Link
                </a>
              </p>
              <p class="youtube-search">
                <a
                  class="btn btn-primary"
                  :href="
                    'https://www.youtube.com/results?search_query=Trailer ' +
                    data.currentMovie.name
                  "
                  >Auf Youtube suchen <i class="bi bi-youtube"></i
                ></a>
              </p>
            </div>
            <div
              v-if="allowIframe(data.currentMovie.wikiUrl)"
              class="col-10 desktop"
            >
              <iframe
                :src="data.currentMovie.wikiUrl"
                title="Wikipedia"
              ></iframe>
            </div>
            <div v-else class="col-10">
              <h2>Leider kein Wikipedia Artikel verzeichnet!</h2>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button
            v-if="user.isAdmin"
            type="button"
            class="btn btn-danger"
            data-bs-target="#deleteModal"
            data-bs-toggle="modal"
          >
            <i class="bi bi-trash3"></i> Löschen
          </button>
          <button
            v-if="user.isAdmin"
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
import {
  getMovies,
  getSortedMovies,
  searchMovie,
} from "@/tools/api-wrapper/UserMovie";
import { reactive } from "@vue/reactivity";
import { Modal } from "bootstrap";
import { wikiWhiteList, videoBusterList } from "@/tools/SearchList";
import { getMoviePageCount } from "@/tools/api-wrapper/PubMovie";
import UpdateModal from "@/components/UpdateModal.vue";
import { deleteMovie } from "@/tools/api-wrapper/AdminMovie";
import { isAdmin } from "@/tools/User";
import { getCookie } from "@/tools/Cookies";

var urlParams = new URLSearchParams(window.location.search);

const user = reactive({
  isAdmin: false,
});

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
  query: urlParams.has("query") ? urlParams.get("query") : "",
  isLoading: true,
  lastSearch: 0,
  currentPage: 0,
  maxPageCount: 0,
  sortID: 0,
  delete: {
    isSending: false,
    hasResponed: false,
    wasSuccessfull: false,
  },
});

start();

function start() {
  if (!getCookie("accessToken")) {
    setTimeout(() => {
      start();
    }, 10);
  } else {
    load();
  }
}

window.onscroll = function () {
  var scrollPosition =
    document.documentElement.scrollTop || document.body.scrollTop;
  if (
    document.body.scrollHeight - scrollPosition < 1200 &&
    data.currentPage < data.maxPageCount &&
    !data.isLoading &&
    data.query.replace(/\s+/g, "").length == 0
  ) {
    if (window.location.pathname == "/search") {
      data.currentPage++;
      loadMovies();
    }
  }
};

function load() {
  user.isAdmin = isAdmin();
  checkTokenAndRun(() => {
    if (data.query.length == 0) {
      loadMovies();
    } else {
      startSearch();
    }
    getMoviePageCount().then((count) => {
      data.maxPageCount = count;
    });
  });
}

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
    if (urlParams.has("query")) {
      window.location = "/search";
    } else {
      data.currentPage = 0;
      checkTokenAndRun(() => {
        loadMovies();
      });
    }
  } else {
    data.isLoading = true;
    checkTokenAndRun(() => {
      sendSearch();
    });
  }
  data.lastSearch = new Date().getTime();
}

function loadMovies() {
  if (data.sortID == 0) {
    checkTokenAndRun(() => {
      loadUnsortedMovies();
    });
  } else {
    checkTokenAndRun(() => {
      loadSortedMovies();
    });
  }
}

function setSortIDAndLoad(event) {
  data.sortID = event.target.value;
  data.currentPage = 0;
  loadMovies();
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
      setTimeout(() => {
        window.location =
          data.query.length > 0 ? "/search?query=" + data.query : "/search";
      }, 2500);
    });
}

function loadUnsortedMovies() {
  data.isLoading = true;
  getMovies(data.currentPage)
    .then((movies) => {
      if (data.currentPage == 0) {
        data.movies = [];
      }
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

function loadSortedMovies() {
  data.isLoading = true;
  getSortedMovies(data.currentPage, data.sortID)
    .then((movies) => {
      if (data.currentPage == 0) {
        data.movies = [];
      }
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

function sendSearch() {
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
#movies {
  margin-bottom: 6rem;
}
iframe {
  width: 100%;
  height: 40rem;
}
.modal-xl {
  max-width: 97vw;
}
.col h2 {
  text-align: center;
}
div.col-10 h2 {
  text-align: center;
}
select.desktop {
  max-width: 11rem;
}
</style>