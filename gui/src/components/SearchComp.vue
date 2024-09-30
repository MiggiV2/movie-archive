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
  <div v-else-if="data.movies.length == 0" class="container mt-5 text-center">
    <h2>Leider keine Filme mit diesem Namen gefunden :(</h2>
  </div>
  <!--Spacer-->
  <div class="my-5 py-5"></div>
  <MovieModal @tag-selected="loadMoviesByTag" :movie="data.currentMovie" />
  <UpdateModal :movie="data.currentMovie" />
  <DeleteModal :movie="data.currentMovie" />
  <TagModal @tag-selected="loadMoviesByTag" />
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
import { getMoviePageCount } from "@/tools/api-wrapper/PubMovie";
import { isAdmin } from "@/tools/User";
import UpdateModal from "@/components/search/UpdateModal.vue";
import DeleteModal from "@/components/search/DeleteModal.vue";
import TagModal from "@/components/search/TagModal.vue";
import MovieModal from "@/components/search/MovieModal.vue";
import { onMounted } from "vue";

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

onMounted(() => {
    load();
});


function handleScroll() {
  const showAllMovies = window.location.pathname == "/search" 
  && data.query.replace(/\s+/g, "").length == 0;
  if(!showAllMovies) {
    return;
  }

  const pixelCountForTigger = window.innerHeight * 0.8;  
  // Check if the user has scrolled to the bottom of the page
  if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - pixelCountForTigger) {
    // If there are more pages to load
    if (data.currentPage < data.maxPageCount) {
      // Increment the current page and load more movies
      data.currentPage++;
      loadMovies();
    } else {
      console.log("No more pages to load.");
    }
  }
}

// Attach the scroll event listener to the window
window.addEventListener('scroll', handleScroll);

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

#search-input ::placeholder {
  color: white;
  opacity: 0.9;
}

select.desktop {
  max-width: 11rem;
}
</style>