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
        <select @change="setSortIDAndLoad($event)" class="form-select desktop" aria-label="Default select example"
          v-model="data.sortID">
          <option value="1">Alphapethisch \/</option>
          <option value="2">Alphapethisch /\</option>
          <option value="3">Jahr \/</option>
          <option value="4">Jahr /\</option>
          <option value="5" disabled>Relevanz</option>
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
        <option value="0">Keine Sortierung</option>
        <option value="1">Alphapethisch \/</option>
        <option value="2">Alphapethisch /\</option>
        <option value="3">Jahr \/</option>
        <option value="4">Jahr /\</option>
      </select>
    </div>
  </div>
  <!-- movies -->
  <div class="container">
    <div class="box box-movie" v-for="(movie, index) in data.movies" :key="index" @click="showMovie(movie)">
      <h2>{{ movie.name }}</h2>
      <p>Aus dem Jahre {{ movie.year }}</p>
    </div>
  </div>
  <!-- placeholder -->
  <div v-if="data.isLoading" class="container">
    <div v-for="index in 10" :key="index">
      <div class="box box-movie placeholder-glow">
        <h2><span class="placeholder col-4"></span></h2>
        <p><span class="placeholder col-2"></span> <span class="placeholder col-1"></span></p>
      </div>
      <div class="box box-movie placeholder-glow">
        <h2><span class="placeholder col-2"></span></h2>
        <p><span class="placeholder col-2"></span> <span class="placeholder col-1"></span></p>
      </div>
      <div class="box box-movie placeholder-glow">
        <h2><span class="placeholder col-3"></span></h2>
        <p><span class="placeholder col-2"></span> <span class="placeholder col-1"></span></p>
      </div>
    </div>
  </div>
  <!-- 404 -->
  <div v-else-if="data.movies.length == 0" class="container mt-5">
    <h2>Leider keine Filme mit diesem Namen gefunden :(</h2>
  </div>
  <!--Spacer-->
  <div class="my-5 py-5"></div>
  <!-- Modal-->
  <div class="modal fade" id="movieModal" tabindex="-1" aria-labelledby="movieModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
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
            <!--Data-->
            <div class="col">
              <h4>{{ data.currentMovie.name }}</h4>
              <!--Poster_Mobile-->
              <div class="mobile" v-if="data.currentMovie.omdbData != undefined">
                <img v-if="data.currentMovie.omdbData.Poster != undefined" :src="data.currentMovie.omdbData.Poster"
                  alt="Movie Poster">
              </div>
              <p>Zu finden in {{ data.currentMovie.block }}</p>
              <p>Aus dem Jahre {{ data.currentMovie.year }}</p>
              <p>Type: {{ data.currentMovie.type }}</p>
              <p class="wikis">
                <a class="btn btn-primary" v-if="allowIframe(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl">Mehr Details auf Wikipedia <i class="bi bi-globe"></i></a>
                <a class="btn btn-primary" v-else-if="isVideoBuster(data.currentMovie.wikiUrl)"
                  :href="data.currentMovie.wikiUrl">
                  VideoBuster Link
                </a>
              </p>
              <p class="youtube-search">
                <a class="btn btn-danger"
                  :href="'https://www.youtube.com/results?search_query=Trailer ' + data.currentMovie.name">
                  Trailer auf Youtube suchen <i class="bi bi-youtube"></i>
                </a>
              </p>
            </div>
            <!--Poster_Desktop-->
            <div class="col desktop" v-if="data.currentMovie.omdbData != undefined">
              <img v-if="data.currentMovie.omdbData.Poster != undefined" :src="data.currentMovie.omdbData.Poster"
                alt="Movie Poster">
              <div v-else>
                <p>Keine Poster gefunden...</p>
                <img src="/img/default-poster.png"
                  alt="Default Poster from https://pixabay.com/vectors/android-sci-fi-retro-poster-7479380/" width="300"
                  height="420">
              </div>
            </div>
          </div>
          <hr v-if="data.currentMovie.tags.length > 0">
          <!--Tags-->
          <div class="row justify-content-center">
            <div class="col-auto" v-for="tag in data.currentMovie.tags" :key="tag.name" @click="loadMoviesByTag(tag)"
              data-bs-dismiss="modal">
              <p class="tag-outline">#{{ tag.name }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div class="row">
            <div class="col">
              <button v-if="user.isAdmin" type="button" class="btn btn-outline-danger" data-bs-target="#deleteModal"
                data-bs-toggle="modal">
                <i class="bi bi-trash3"></i> <span class="desktop">Löschen</span>
              </button>
            </div>
            <div class="col-auto">
              <button v-if="user.isAdmin" @click="showUpdateModal()" type="button" class="btn btn-outline-primary"
                data-bs-dismiss="modal">
                <i class="bi bi-pencil"></i> <span class="desktop">Update</span>
              </button>
              <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                Schließen
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <UpdateModal :movie="data.currentMovie" />
  <DeleteModal :movie="data.currentMovie"/>
  <TagModal @tag-selected="loadMoviesByTag"/>
</template>

<script setup>
import { checkTokenAndRun } from "@/tools/Auth";
import { getPostUrl } from "@/tools/api-wrapper/omdbapi";
import {
  getSortedMovies,
  searchMovie,
  searchByTag,
  getTagsByMovie
} from "@/tools/api-wrapper/UserMovie";
import { reactive } from "@vue/reactivity";
import { Modal } from "bootstrap";
import { wikiWhiteList, videoBusterList } from "@/tools/SearchList";
import { getMoviePageCount } from "@/tools/api-wrapper/PubMovie";
import { isAdmin } from "@/tools/User";
import { getCookie } from "@/tools/Cookies";
import UpdateModal from "@/components/UpdateModal.vue";
import DeleteModal from "@/components/DeleteModal.vue";
import TagModal from "@/components/TagModal.vue";

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
    id: 0,
    tags: [],
    omdbData: {
      Poster: ""
    }
  },
  query: urlParams.has("query") ? urlParams.get("query") : "",
  isLoading: true,
  lastSearch: 0,
  currentPage: 0,
  maxPageCount: 0,
  sortID: "3",
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
      data.sortID = "3";
      data.currentPage = 0;
      checkTokenAndRun(() => {
        loadMovies();
      });
      console.log(data.sortID);
      console.log(typeof data.sortID)
    }
  } else {
    data.isLoading = true;
    data.sortID = "5";
    checkTokenAndRun(() => {
      sendSearch();
    });
  }
  data.lastSearch = new Date().getTime();
}

function loadMovies() {
  checkTokenAndRun(() => {
    loadSortedMovies();
  });
}

function setSortIDAndLoad(event) {
  data.sortID = event.target.value;
  data.currentPage = 0;
  let isRelevanz = data.sortID == 5 //Default in search  
  if (!isRelevanz) {
    checkTokenAndRun(() => {
      loadMovies();
    });
  }
}

function showMovie(movie) {
  data.currentMovie = movie;
  loadTagsForMovie();
  loadPoster(movie);
  var modalElement = document.getElementById("movieModal");
  var modal = new Modal(modalElement);
  modal.show();
}

function showUpdateModal() {
  var modalElement = document.getElementById("updateModal");
  var modal = new Modal(modalElement);
  modal.show();
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

function loadPoster(movie) {
  let cacheKey = movie.year + "-" + movie.name;
  if (localStorage.getItem(cacheKey)) {
    data.currentMovie.omdbData = JSON.parse(localStorage.getItem(cacheKey));
  }
  else {
    getPostUrl(movie).then(omdbData => {
      data.currentMovie.omdbData = omdbData;
      if (omdbData.Response != "False") {
        localStorage.setItem(cacheKey, JSON.stringify(omdbData));
      }
    });
  }
}

function loadMoviesByTag(tag) {
  checkTokenAndRun(() => {
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
  })
}

function loadTagsForMovie() {
  data.currentMovie.tags = [{ name: "no", id: 0 }];
  checkTokenAndRun(() => {
    getTagsByMovie(data.currentMovie.id).then(tags => {
      data.currentMovie.tags = tags;
    })
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

#search-input select {
  color: white;
  background: transparent;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
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

#movieModal .modal-body {
  text-align: center;
  font-size: 1.2rem;
}

#movieModal .modal-body h4 {
  margin-top: 0.8rem;
  margin-bottom: 1.2rem;
}

#movieModal .modal-body>hr {
  margin-top: 3rem;
}

#movieModal .row.justify-content-center {
  margin-top: 1.5rem;
}

#movieModal .modal-body .col-auto {
  padding-left: 5px;
  padding-right: 5px;
}

#movieModal p.tag-outline {
  color: rgb(0, 102, 255);
  margin-bottom: 5px;
  font-size: 0.9rem;
  cursor: pointer;
}

.wikis {
  margin-top: 11rem;
}

.modal-footer>.row {
  width: 100%;
}

.modal-footer button {
  margin-left: 1rem;
}

.tag {
  color: white;
  background-color: rgb(0, 102, 255);
  padding: 7px;
  border-radius: 5px;
  cursor: pointer;
}

/** Molbile */
@media (max-width: 768px) {
  .wikis {
    margin-top: 2rem;
  }

  .mobile > img {
    margin-bottom: 2rem;
  }
}
</style>