<template>
  <footer>
    <div class="row align-items-center">
      <div class="col-auto">
        <span class="desktop">Entwickelt vom</span>
        <span class="mobile">Von</span>
         Michael Hainz
        <i class="bi bi-person-check-fill"></i>
      </div>
      <div class="col" />
      <div class="col-auto">
        <a href="https://github.com/MiggiV2">
          GitHub <i class="bi bi-github"></i>
        </a>
      </div>
      <div class="col-auto">
        <a href="https://gitea.familyhainz.de/Miggi">
          Gitea <i class="bi bi-git"></i>
        </a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
footer {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 40px;
  background-color: var(--primary-color);
}
.row {
  height: 100%;
  max-width: 95%;
  margin: auto;
}
a {
  color: unset;
  text-decoration: unset;
}
@media (max-width: 768px) {
  .row {
    max-width: 100vw;
  }
}
</style>