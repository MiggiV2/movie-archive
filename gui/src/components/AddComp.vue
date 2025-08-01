<template>
  <div class="container">
    <form id="add-form" class="box needs-validation" onsubmit="return false" @submit="save()" novalidate>
      <h2>Einen neuen Film hinzufügen</h2>
      <div class="mb-3 row">
        <label for="staticEmail" class="col-sm-2 col-form-label">Name</label>
        <div class="col-sm-9">
          <input type="text" class="form-control" id="staticEmail" placeholder="Film Titel" required
            v-model="movie.name" />
          <div class="invalid-feedback">Bitte gibt einen Film Namen ein!</div>
        </div>
      </div>
      <div class="mb-3 row">
        <label for="inputPassword" class="col-sm-2 col-form-label">WikipediaURL</label>
        <div class="col-sm-9">
          <input type="text" class="form-control" id="inputPassword" placeholder="https://de.wikipedia.org/wiki/..."
            v-model="movie.wikiUrl" />
          <div class="invalid-feedback">
            Bitte gibt einen Wikipedia Link ein!
          </div>
        </div>
      </div>
      <div class="mb-3 row">
        <label for="inputPassword" class="col-sm-2 col-form-label">Block</label>
        <div class="col-sm-9">
          <input type="number" class="form-control" id="inputPassword" placeholder="1" required v-model="movie.block" />
          <div class="invalid-feedback">
            Bitte gibt eine gültige Nummber ein!
          </div>
        </div>
      </div>
      <div class="mb-3 row">
        <label for="inputPassword" class="col-sm-2 col-form-label">Jahr</label>
        <div class="col-sm-9 has-validation">
          <input type="number" class="form-control" id="inputPassword" placeholder="2020" required
            v-model="movie.year" />
          <div class="invalid-feedback">
            Bitte gibt eine gültige Jahreszahl ein!
          </div>
        </div>
      </div>
      <div class="mb-3 row">
        <label for="inputPassword" class="col-sm-2 col-form-label">Type</label>
        <div class="col-sm-9">
          <select class="form-select" required v-model="movie.type">
            <option selected disabled value="">Wähle einen Type</option>
            <option value="BD">BluRay Disc</option>
            <option value="4k-BD">BluRay Disc 4k</option>
            <option value="DVD">DVD</option>
          </select>
          <div class="invalid-feedback">Bitte wähle eine Kategorie</div>
        </div>
      </div>
      <button class="btn btn-success" type="submit">
        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="status.isSending"></span>
        Speichern
      </button>
    </form>
    <!-- toast -->
    <div>
      <div class="position-fixed top-0 end-0 p-3" style="z-index: 101">
        <div id="errorToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
          <div class="toast-header">
            <img src="/img/false.png" class="rounded me-2" alt="Error" v-if="status.failed" />
            <img src="/img/tick.png" class="rounded me-2" alt="Done" v-else />
            <strong class="me-auto">{{ status.message }}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "@vue/reactivity";
import { setCustomValidation } from "@/tools/BS5Helper";
import { addMovie } from "@/tools/api-wrapper/AdminMovie";
import { Toast } from "bootstrap";
import { onMounted } from "vue";

const movie = reactive({
  name: undefined,
  year: undefined,
  block: undefined,
  wikiUrl: undefined,
  type: "",
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
      status.message = "Film '" + addedMovie.name + "' gespeichert!";
      status.failed = false;
      resetMovie();
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

function resetMovie() {
  movie.name = undefined;
  movie.year = undefined;
  movie.block = undefined;
  movie.wikiUrl = undefined;
  movie.type = "";
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
.box h2 {
  margin: 1rem auto 3rem;
}

#save-button {
  margin: 2rem auto 1.5rem;
}

img {
  max-width: 40px;
  margin-right: 1rem !important;
}
</style>