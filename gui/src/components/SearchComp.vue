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
      <h2>Leider keine Filme gefunden!</h2>
    </div>
  </div>
  <!-- Modal - not working yet -->
  <div
    class="modal fade"
    id="exampleModal"
    tabindex="-1"
    aria-labelledby="exampleModalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">Modal title</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">...</div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            Close
          </button>
          <button type="button" class="btn btn-primary">Save changes</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { checkTokenAndRun } from "@/tools/Auth";
import { getMovies, searchMovie } from "@/tools/UserMovie";
import { reactive } from "@vue/reactivity";

const data = reactive({
  movies: [],
  query: "",
  isLoading: true,
  lastSearch: 0,
});

checkTokenAndRun(() => {
  loadMovies();
});

function search() {
  setTimeout(() => {
    if (data.lastSearch + 500 < new Date().getTime()) {
      startSearch();
    }
  }, 500);
}

function startSearch() {
  if (data.query.replace(/\s+/g, "").length == 0) {
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
  getMovies(0)
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
</style>