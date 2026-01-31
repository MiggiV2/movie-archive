<template>
  <div class="container">
    <div class="bg-white p-5 modern-shadow mt-5 rounded">
      <h2 class="mb-4 text-center">Einen neuen Film hinzufügen</h2>
      <form id="add-form" class="needs-validation" onsubmit="return false" @submit="save()" novalidate>
        <div class="mb-3 row">
          <label for="inputName" class="col-sm-2 col-form-label"><i class="bi bi-film me-2"></i>Name</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="inputName" placeholder="Film Titel" required
              v-model="movie.title" />
            <div class="invalid-feedback">Bitte gibt einen Film Namen ein!</div>
          </div>
        </div>
        <div class="mb-3 row">
          <label for="inputYear" class="col-sm-2 col-form-label"><i class="bi bi-calendar me-2"></i>Jahr</label>
          <div class="col-sm-10 has-validation">
            <input type="number" class="form-control" id="inputYear" placeholder="2020" required
              v-model="movie.year" />
            <div class="invalid-feedback">
              Bitte gibt eine gültige Jahreszahl ein!
            </div>
          </div>
        </div>      
        <div class="mb-3 row">
          <label for="inputBlock" class="col-sm-2 col-form-label"><i class="bi bi-box me-2"></i>Block</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="inputBlock" placeholder="A1" required v-model="movie.block" />
            <div class="invalid-feedback">
              Bitte gibt eine gültige Nummber ein!
            </div>
          </div>
        </div>
        <div class="mb-3 row">
          <label for="inputType" class="col-sm-2 col-form-label"><i class="bi bi-disc me-2"></i>Type</label>
          <div class="col-sm-10">
            <select class="form-select" id="inputType" required v-model="movie.type">
              <option selected value="BD">BluRay Disc</option>
              <option value="4k-BD">BluRay Disc 4k</option>
              <option value="DVD">DVD</option>
            </select>
            <div class="invalid-feedback">Bitte wähle eine Kategorie</div>
          </div>
        </div>
        <div class="mb-3 row">
          <label for="inputWiki" class="col-sm-2 col-form-label"><i class="bi bi-link me-2"></i>Wiki URL <span class="text-muted small">(Optional)</span></label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="inputWiki" placeholder="https://de.wikipedia.org/wiki/..."
              v-model="movie.wikiUrl" />
          </div>
        </div>      
        <div class="mb-3 row">
          <label for="inputImdb" class="col-sm-2 col-form-label"><i class="bi bi-camera-reels me-2"></i>IMDB ID <span class="text-muted small">(Optional)</span></label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="inputImdb" placeholder="tt0000000" v-model="movie.imdbId" />
          </div>
        </div>      
        <button class="btn btn-primary w-100 mt-3" id="save-button" type="submit">
          <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="status.isSending"></span>
          Speichern
        </button>
      </form>
    </div>
    <div v-if="savedMovie.image" class="text-center mb-5 mt-5">
      <h2>{{ savedMovie.title }}</h2>
      <img class="modern-shadow" :src="savedMovie.image" alt="" />
      <p class="mt-2">{{ savedMovie.year }}</p>
    </div>    
    <!-- toast -->
    <div>
      <div class="position-fixed top-0 end-0 p-3" style="z-index: 101">
        <div id="errorToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
          <div class="toast-header">
            <img src="/img/false.png" class="rounded me-2 small" alt="Error" v-if="status.failed" />
            <img src="/img/tick.png" class="rounded me-2 small" alt="Done" v-else />
            <strong class="me-auto">{{ status.message }}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { setCustomValidation } from "@/tools/BS5Helper";
import { addMovie } from "@/tools/api-wrapper/Movie";
import { Toast } from "bootstrap";
import { onMounted } from "vue";

const movie = reactive({
  title: undefined,
  year: undefined,
  block: undefined,
  wikiUrl: undefined,
  type: "BD",
  image: undefined,
  imdbId: undefined
});

const savedMovie = reactive({
  title: undefined,
  year: undefined,
  image: undefined,
  imdbId: undefined
});

const status = reactive({
  sending: false,
  failed: false,
  message: undefined,
});

onMounted(() => {
  setCustomValidation();
});

async function save() {
  var form = document.getElementById("add-form");
  if (form.checkValidity()) {
    status.sending = true;
    console.log("Sending...");
    await sendSave();
  }
}

async function sendSave() {
  await addMovie(movie)
    .then((addedMovie) => {
      status.message = "Film '" + addedMovie.title + "' gespeichert!";
      status.failed = false;
      if (addedMovie.externalId) {
        savedMovie.imdbId = addedMovie.externalId;
        savedMovie.image = addedMovie.image;
        savedMovie.title = addedMovie.title;
        savedMovie.year = addedMovie.year;
      }
      setTimeout(() => {
        resetMovie();
      }, 3000);      
    })
    .catch((e) => {
      console.log(e);
      status.message = e;
      status.failed = true;
    })
    .finally(() => {
      status.sending = false;
      showToast();
    });
}

/* eslint-disable */
function resetMovie() {
  movie.title = undefined;
  movie.year = undefined;
  movie.block = undefined;
  movie.wikiUrl = undefined;
  movie.type = "BD";
  movie.imdbId = undefined;
  var form = document.getElementById("add-form");
  form.classList.remove("was-validated");
}

function showToast() {
  var toastError = document.getElementById("errorToast");
  var toast = new Toast(toastError);
  toast.show();
}
</script>

<style scoped>
img.modern-shadow {
  height: 500px;
  border-radius: 20px;
}

img.small {
  max-width: 40px;
  margin-right: 1rem !important;
}
</style>
