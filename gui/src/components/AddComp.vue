<template>
  <form
    id="add-form"
    class="box needs-validation"
    onsubmit="return false"
    @submit="setCustomValidity('add-form')"
    novalidate
  >
    <h2>Einen neuen Film hinzufügen</h2>
    <div class="mb-3 row">
      <label for="staticEmail" class="col-sm-2 col-form-label">Name</label>
      <div class="col-sm-9">
        <input
          type="text"
          class="form-control"
          id="staticEmail"
          placeholder="Film Titel"
          required
          v-model="movie.name"
        />
        <div class="invalid-feedback">Bitte gibt einen Film Namen ein!</div>
      </div>
    </div>
    <div class="mb-3 row">
      <label for="inputPassword" class="col-sm-2 col-form-label"
        >WikipediaURL</label
      >
      <div class="col-sm-9">
        <input
          type="text"
          class="form-control"
          id="inputPassword"
          placeholder="https://de.wikipedia.org/wiki/..."
          required
          v-model="movie.wikiUrl"
        />
        <div class="invalid-feedback">Bitte gibt einen Wikipedia Link ein!</div>
      </div>
    </div>
    <div class="mb-3 row">
      <label for="inputPassword" class="col-sm-2 col-form-label">Block</label>
      <div class="col-sm-9">
        <input
          type="number"
          class="form-control"
          id="inputPassword"
          placeholder="1"
          required
          v-model="movie.block"
        />
        <div class="invalid-feedback">Bitte gibt eine gültige Nummber ein!</div>
      </div>
    </div>
    <div class="mb-3 row">
      <label for="inputPassword" class="col-sm-2 col-form-label">Jahr</label>
      <div class="col-sm-9 has-validation">
        <input
          type="number"
          class="form-control"
          id="inputPassword"
          placeholder="2020"
          required
          v-model="movie.year"
        />
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
          <option value="BD">BlueRay Disc</option>
          <option value="4k-BD">BlueRay Disc 4k</option>
          <option value="DVD">DVD</option>
        </select>
        <div class="invalid-feedback">Bitte wähle eine Kategorie</div>
      </div>
    </div>
    <button
      id="save-button"
      type="submit"
      class="btn btn-success"
      @click="save()"
    >
      Speichern
    </button>
  </form>
</template>

<script setup>
const { reactive } = require("@vue/reactivity");

const movie = reactive({
  name: "",
  year: "",
  block: "",
  wikiUrl: "",
  type: "",
});

function setCustomValidity(elementID) {
  var form = document.getElementById(elementID);
  if (!form.classList.contains("was-validated")) {
    form.classList.add("was-validated");
  }
}

function save() {
  var list = document.getElementsByClassName("invalid-feedback");
  for (let i = 0; i < list.length; i++) {
    const element = list[i];
    console.log(element.getAttribute('display'))
  }
}
</script>

<style scoped>
.box h2 {
  margin: 1rem auto 3rem;
}
#save-button {
  margin: 2rem auto 1.5rem;
}
</style>