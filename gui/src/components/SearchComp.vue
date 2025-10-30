<template>
  <!-- search -->
  <div id="search-input" class="container mb-5">
    <form @submit="search" onsubmit="return false">
      <div class="input-group mb-3">
        <input type="text" class="form-control" placeholder="Welchen Film suche Sie?"
          aria-label="Welchen Film suche Sie?" aria-describedby="button-addon2" v-model="data.query" @input="search" />
        <!-- Disabled for now
        <button class="btn btn-outline-light" data-bs-toggle="modal" data-bs-target="#tagModal">
          Tags
          <i class="bi bi-tag desktop"></i>
        </button>
        -->
        <select @change="setSortIDAndLoad($event)" class="form-select desktop" aria-label="Default select example"
          v-model="data.sortID">
          <option value="1">Alphapethisch \/</option>
          <option value="2">Alphapethisch /\</option>
          <option value="3">Jahr \/</option>
          <option value="4">Jahr /\</option>
          <option value="5" disabled>Relevanz</option>
        </select>
        <button class="btn btn-outline mobile" type="button" data-bs-toggle="collapse"
          data-bs-target="#collapseMenu" aria-expanded="false" aria-controls="collapseMenu">
          Sortieren
        </button>
      </div>
    </form>
  </div>
  <!--mobile-sort-content-->
  <div class="container collapse" id="collapseMenu">
      <select @change="setSortIDAndLoad($event)" class="form-select" aria-label="Default select example">
        <option value="1">Alphapethisch \/</option>
        <option value="2">Alphapethisch /\</option>
        <option value="3">Jahr \/</option>
        <option value="4">Jahr /\</option>
      </select>
  </div>
  <!-- placeholder -->
  <div v-if="data.isLoading" class="container">
    <div class="row">
      <div v-for="index in 9" :key="index" class="col-xl-4 text-center my-3 placeholder-glow">
        <h2><span class="placeholder col-6"></span></h2>
        <span style="height: 490px;" class="placeholder col-10"></span>
        <p class="mt-2">Aus dem Jahre <span class="placeholder col-2"></span></p>
      </div>
    </div>
  </div>
  <!-- 404 -->
  <div v-else-if="data.movies.length == 0" class="container mt-5 text-center">
    <h2>Leider keine Filme mit diesem Namen gefunden :(</h2>
  </div>
  <!-- movies -->
  <div class="container">
    <div class="row">
      <div class="col-xl-4 text-center my-3 movie" v-for="(movie, index) in data.movies" :key="index"
        @click="showMovie(movie)">
        <h2>{{ movie.title }}</h2>
        <img :src="getImage(movie)" alt="Poster" class="modern-shadow">
        <p class="mt-2">Aus dem Jahre {{ movie.year }}</p>
      </div>
    </div>
  </div>
  <!--Spacer-->
  <div class="my-5 py-5"></div>
  <MovieModal @tag-selected="loadMoviesByTag" :movie="data.currentMovie" />
  <UpdateModal :movie="data.currentMovie" />
  <DeleteModal :movie="data.currentMovie" />
  <TagModal @tag-selected="loadMoviesByTag" ref="tagModalRef" />
</template>

<script setup>
import {
  getSortedMovies,
  searchMovie,
  searchByTag,
  getMovie
} from "@/tools/api-wrapper/UserMovie";
import { reactive, ref } from "vue";
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
  isLoading: false,
  lastSearch: 0,
  currentPage: 0,
  maxPageCount: 0,
  sortID: "3",
});
const tagModalRef = ref(null);

onMounted(() => {
  load();
});

async function handleScroll() {
  const showAllMovies = window.location.pathname == "/search"
    && data.query.replace(/\s+/g, "").length == 0;
  if (!showAllMovies) {
    return;
  }

  const pixelCountForTigger = window.innerHeight * 0.8;
  // Check if the user has scrolled to the bottom of the page
  const needToLoadNextPage = (window.innerHeight + window.scrollY) >= document.body.offsetHeight - pixelCountForTigger && !data.isLoading;
  const hasNextPage = data.currentPage < data.maxPageCount;
  if (needToLoadNextPage && hasNextPage) {
    data.currentPage++;
    // console.log("requesting data...")
    await loadSortedMovies();
    // console.log("request done!")
  }
}

// Attach the scroll event listener to the window
window.addEventListener('scroll', handleScroll);

async function load() {
  user.isAdmin = isAdmin();
  if (data.query.length == 0) {
    await loadSortedMovies();
  } else {
    await startSearch();
  }
  getMoviePageCount().then((count) => {
    data.maxPageCount = count;
  });
}

function getImage(movie) {
  if (movie.image == null) {
    return "/img/default-poster.webp";
  }
  return movie.image.replace('@._V1_.jpg', '@._V1_QL75_UX380_CR0,0,380,562_.jpg');
}

// called by vue.js input / form
function search() {
  setTimeout(() => {
    if (data.lastSearch + 500 < new Date().getTime()) {
      // proably okay not to use await here
      startSearch();
    }
  }, 500);
}

async function startSearch() {
  if (data.query.replace(/\s+/g, "").length == 0) {
    if (urlParams.has("query")) {
      window.location = "/search";
    } else {
      data.sortID = "3";
      data.currentPage = 0;
      await loadSortedMovies();
      console.log(data.sortID);
      console.log(typeof data.sortID)
    }
  } else {
    data.isLoading = true;
    data.sortID = "5";
    await sendSearch();
  }
  data.lastSearch = new Date().getTime();
}

async function setSortIDAndLoad(event) {
  data.sortID = event.target.value;
  data.currentPage = 0;
  let isRelevanz = data.sortID == 5 //Default in search  
  if (!isRelevanz) {
    await loadSortedMovies();
  }
}

async function showMovie(movie) {
  getMovie(movie.id).then((m) => {
    data.currentMovie = m;
    var modalElement = document.getElementById("movieModal");
    var modal = new Modal(modalElement);
    modal.show();
  }).catch((e) => {
    console.error(e);
    alert("Something went wrong! " + e);
  });
}

async function loadSortedMovies() {
  data.isLoading = true;
  await getSortedMovies(data.currentPage, data.sortID)
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

async function sendSearch() {
  let looksLikeTag = data.query.split("/").length > 2;
  if (!looksLikeTag) {
    await searchMovie(data.query.replaceAll(" ", "+"))
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

async function loadMoviesByTag(tag) {
  await searchByTag(tag.id).then(movies => {
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
</script>

<style scoped>
#search-input {
  margin-top: 3rem;
  max-width: 70rem;
}

#search-input input {
  background: unset;
  color: var(--secondary-800);
}

#search-input select {
  color: var(--secondary-800);;
  background: transparent;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

#search-input ::placeholder {
  color: var(--secondary-800);
  opacity: 0.9;
}

select.desktop {
  max-width: 11rem;
}

img {
  height: 490px;
  max-width: 80%;
  border-radius: 20px;
}

.movie {
  cursor: pointer;
  transition: transform 0.2s;
}

/** Molbile */
@media (max-width: 1400px) {
  img {
    height: unset;
    min-height: 300px;
    max-height: 400px;
  }
}
</style>