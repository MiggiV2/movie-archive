<template>
  <!-- search -->
  <div id="search-input" class="container">
    <form @submit="search" onsubmit="return false">
      <div class="input-group mb-3">
        <input type="text" class="form-control" placeholder="Welchen Film suche Sie?"
          aria-label="Welchen Film suche Sie?" aria-describedby="button-addon2" v-model="data.query" @input="search" />
        <button class="btn btn-outline-light" data-bs-toggle="modal" data-bs-target="#tagModal">
          Tags
          <i class="bi bi-tag desktop"></i>
        </button>
        <select @change="setSortIDAndLoad($event)" class="form-select desktop" aria-label="Default select example">
          <option value="0" selected>Keine Sortierung</option>
          <option value="1">Alphapethisch \/</option>
          <option value="2">Alphapethisch /\</option>
          <option value="3">Jahr \/</option>
          <option value="4">Jahr /\</option>
          <option value="5">Relevanz</option>
        </select>
        <button class="btn btn-outline-light mobile" type="button" data-bs-toggle="collapse"
          data-bs-target="#collapseMenu" aria-expanded="false" aria-controls="collapseMenu">
          Sortieren
        </button>
      </div>
    </form>
  </div>
  <!--mobile-sort-content-->
  <div class="container collapse" id="collapseMenu">
    <div class="card card-body">
      <select @change="setSortIDAndLoad($event)" class="form-select" aria-label="Default select example">
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
    <div class="box box-movie" v-for="(movie, index) in data.movies" :key="index" @click="showMovie(movie)">
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
    <h2>Leider keine Filme mit diesem Namen gefunden :(</h2>
  </div>
  <!-- Modal-->
  <div class="modal fade" id="movieModal" tabindex="-1" aria-labelledby="movieModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="movieModalLabel">
            Film - {{ data.currentMovie.name }}
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
                <a class="btn btn-outline-primary" v-if="allowIframe(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl">Seite auf Wikipedia öffnen</a>
                <a class="btn btn-outline-primary" v-else-if="isVideoBuster(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl">
                  VideoBuster Link
                </a>
              </p>
              <p class="youtube-search">
                <a class="btn btn-danger" :href="'https://www.youtube.com/results?search_query=Trailer ' +
      data.currentMovie.name
      ">Auf Youtube suchen <i class="bi bi-youtube"></i></a>
              </p>
              <p>Tags:</p>
              <div class="row">
                <div class="col-auto tag-wrapper" v-for="tag in data.currentMovie.tags" :key="tag.name" @click="loadMoviesByTag(tag)" data-bs-dismiss="modal">
                  <p class="tag">{{ tag.name }}</p>
                </div>
              </div>
            </div>
            <div v-if="allowIframe(data.currentMovie.wikiUrl)" class="col-10 desktop">
              <iframe :src="data.currentMovie.wikiUrl" title="Wikipedia"></iframe>
            </div>
            <div v-else class="col-10">
              <h2>Leider kein Wikipedia Artikel verzeichnet!</h2>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button v-if="user.isAdmin" type="button" class="btn btn-danger" data-bs-target="#deleteModal"
            data-bs-toggle="modal">
            <i class="bi bi-trash3"></i> Löschen
          </button>
          <button v-if="user.isAdmin" @click="showUpdateModal()" type="button" class="btn btn-primary"
            data-bs-dismiss="modal">
            <i class="bi bi-pencil"></i> Update
          </button>
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
            Schließen
          </button>
        </div>
      </div>
    </div>
  </div>
  <UpdateModal :movie="data.currentMovie" />
  <!-- Delete-Confirm Modal -->
  <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModallLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteModalLabel">Bist du sicher?</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
          <button v-if="!data.delete.hasResponed" type="button" class="btn btn-secondary" data-bs-target="#movieModal"
            data-bs-toggle="modal">
            Abbrechen
          </button>
          <button v-if="!data.delete.hasResponed" type="button" class="btn btn-primary" @click="sendDelete()">
            <div v-if="data.delete.isSending" class="spinner-border spinner-border-sm" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            Ja, sicher
          </button>
          <button v-else type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            Schließen
          </button>
        </div>
      </div>
    </div>
  </div>
  <!-- Tag-Modal -->
  <div class="modal fade" id="tagModal" tabindex="-1" aria-labelledby="tagModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="tagModalLabel">Suche einen Film mit folgendem Tag / Schlagwort</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <div class="input-group mb-3">
              <input type="text" class="form-control" placeholder="Nach welchen Tag suchen Sie?"
                aria-label="Nach welchen Tag suchen Sie?" aria-describedby="tag-button" v-model="data.tagName">
              <button class="btn btn-outline-dark" type="button" id="tag-button">
                <i class="bi bi-search"></i>
              </button>
            </div>
            <p class="col-auto"
              v-for="(tag, index) in data.tags.filter(tag => data.tagName == null || tag.name.toLowerCase().startsWith(data.tagName.toLowerCase()))"
              :key="index" @click="loadMoviesByTag(tag)" data-bs-dismiss="modal">
              <span class="tag">{{ tag.name }}</span>
            </p>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Schließen</button>
          <button type="button" class="btn btn-primary">Suchen</button>
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
  getTags,
  searchByTag,
  getTagsByMovie
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
  tags: [],
  tagName: null,
  currentMovie: {
    name: "...",
    year: 0,
    uuid: "...",
    block: "...",
    wikiUrl: "...",
    type: "...",
    id: 0,
    tags: []
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
    getTags().then(tags => {
      data.tags = tags;
    })
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
  let isRelevanz = data.sortID == 5 //Default in search
  if (!isRelevanz) {
    loadMovies();
  }
}

function showMovie(movie) {
  data.currentMovie = movie;
  loadTagsForMovie();
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
  let looksLikeTag = data.query.split("/").length > 2;
  if (!looksLikeTag) {
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
}

function loadMoviesByTag(tag) {
  searchByTag(tag.id).then(movies => {
    data.movies = movies;
    // Setting query -> no slide sync
    if (movies.length == 1) {
      data.query = movies[0].name;
    } else if (movies.length > 1) {
      data.query = "";
      for (let i = 0; i < movies.length; i++) {
        data.query += movies[i].name
        if (i != movies.length - 1) {
          data.query += " / ";
        }
      }
    }
    else {
      data.query = "Tag=" + tag.name;
    }
  }).catch((e) => {
    console.error(e);
    alert("Something went wrong! " + e);
  })
    .finally(() => {
      data.isLoading = false;
    });
}

function loadTagsForMovie() {
  data.currentMovie.tags = [{ name: "no", id: 0 }];
  getTagsByMovie(data.currentMovie.id).then(tags => {
    data.currentMovie.tags = tags;
  })
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

#search-input select {
  color: white;
  background: transparent;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
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

div.container>h2 {
  text-align: center;
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

#search-input ::placeholder {
  color: white;
  opacity: 0.9;
}

.modal-body>.row>.col-4 {
  padding: 5px;
}

.tag-wrapper {
  padding: 5px;
}

.tag {
  color: white;
  background-color: rgb(0, 102, 255);
  padding: 7px;
  border-radius: 5px;
  cursor: pointer;
}
</style>