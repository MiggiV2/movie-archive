<template>
  <div class="modal fade" id="updateModal" tabindex="-1" aria-labelledby="updateModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="updateModalLabel">
            Update {{ props.movie.name }}
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3 row">
            <label for="movie-name" class="col-sm-3 col-form-label">Film Name</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" id="movie-name" v-model="data.movie.name" />
            </div>
          </div>
          <div class="mb-3 row">
            <label for="movie-year" class="col-sm-3 col-form-label">Jahr des Films</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" id="movie-year" v-model="data.movie.year" />
            </div>
          </div>
          <div class="mb-3 row">
            <label for="movie-block" class="col-sm-3 col-form-label">Film Block</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" id="movie-block" v-model="data.movie.block" />
            </div>
          </div>
          <div class="mb-3 row">
            <label for="movie-wiki" class="col-sm-3 col-form-label">Wikipedia URL</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" id="movie-wiki" v-model="data.movie.wikiUrl" />
            </div>
          </div>
          <div class="mb-3 row">
            <label for="movie-originalname" class="col-sm-3 col-form-label">Originaltitel</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" id="movie-originalname" v-model="data.movie.originalName" />
            </div>
          </div>
          <strong v-if="data.failed && data.message != null">
            {{ data.message }}
          </strong>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="showMovieModal()">
            Abbrechen
          </button>
          <button type="button" class="btn btn-primary" @click="update()">
            <div v-if="!data.failed && data.message != null">
              <i class="bi bi-check-circle"></i> Gespeichert!
            </div>
            <div v-else><i class="bi bi-sd-card"></i> Speichern</div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const { reactive } = require("@vue/reactivity");
import { updateMovie } from "@/tools/api-wrapper/AdminMovie";
import { Modal } from "bootstrap";
import { onMounted } from "vue";
// eslint-disable-next-line
const props = defineProps({
  movie: {},
});
var data = reactive({
  movie: props.movie,
  failed: false,
  message: null,
});

onMounted(() => {
  setHandler();
});

function setHandler() {
  var myModal = document.getElementById("updateModal");
  // eslint-disable-next-line
  myModal.addEventListener("show.bs.modal", function (event) {
    data.movie = props.movie;
  });
}

async function update() {
  await updateMovie(data.movie)
    .then(() => {
      data.failed = false;
      data.message = "Film gespeicher!";
    })
    .catch((e) => {
      data.message = e;
      data.failed = true;
    });
}

function showMovieModal() {
  var movieEle = document.getElementById("movieModal");
  var movieModal = new Modal(movieEle);
  data.message = null;
  movieModal.show();
}
</script>