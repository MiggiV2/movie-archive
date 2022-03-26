<template>
  <div class="header-content">
    <div class="row align-items-center">
      <div class="col-auto">
        <h2>
          <a href="/" class="link"
            ><i class="bi bi-camera-reels"></i> MovieArchive
            <i class="bi bi-film"></i
          ></a>
        </h2>
      </div>
      <div class="col" />
      <div class="col-auto desktop">
        <h4 v-if="user.name.length > 0">
          <a href="/search"><i class="bi bi-search"></i> Suchen</a>
        </h4>
      </div>
      <div class="col-auto desktop">
        <h4 class="logout" v-if="user.name.length > 0" @click="logout()">
          <i class="bi bi-box-arrow-right"></i> Logout
        </h4>
      </div>
      <div class="col-auto desktop">
        <h4 v-if="user.name.length > 0">
          <i class="bi bi-person-circle"></i> {{ user.name }}
        </h4>
        <h4 v-else @click="openLogin()">
          <i class="bi bi-box-arrow-in-right"></i> Login
        </h4>
      </div>
      <div class="col-auto mobile">
        <h2>
          <a
            data-bs-toggle="collapse"
            href="#mobileMenu"
            role="button"
            aria-expanded="false"
            aria-controls="mobileMenu"
          >
            <i class="bi bi-list"></i>
          </a>
        </h2>
      </div>
    </div>
    <div class="collapse" id="mobileMenu">
      <div class="card card-body">
        <hr />
        <div class="list-group">
          <a v-if="user.name.length > 0">
            <i class="bi bi-search"></i> Suchen
          </a>
          <a v-if="user.name.length > 0">
            {{ user.name }} <i class="bi bi-person-circle"></i>
          </a>
          <a v-else @click="login()">
            Login <i class="bi bi-box-arrow-in-right"></i>
          </a>
          <a class="logout" v-if="user.name.length > 0" @click="logout()">
            Logout <i class="bi bi-box-arrow-right"></i>
          </a>
        </div>
      </div>
    </div>
  </div>
  <!-- toast -->
  <div>
    <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
      <div
        id="liveToast"
        class="toast"
        role="alert"
        aria-live="assertive"
        aria-atomic="true"
      >
        <div class="toast-header">
          <img src="/img/unlock.png" class="rounded me-2" alt="..." />
          <strong class="me-auto">Erfolgreich eingeloggt!</strong>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="toast"
            aria-label="Close"
          ></button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getCookie, setCookieSeasson } from "@/tools/Cookies";
import {
  checkTokenAndRun,
  getUserName,
  isAdmin,
  openLogin,
  openLogout,
} from "@/tools/Auth";
import { reactive } from "@vue/reactivity";
import { Toast } from "bootstrap";

var user = reactive({
  name: "",
  isAdmin: false,
});

function login() {
  openLogin();
}

function logout() {
  openLogout();
}

if (getCookie("refreshToken")) {
  checkTokenAndRun(() => {
    user.name = getUserName();
    user.isAdmin = isAdmin();
    if (getCookie("login-toast") != "showed") {
      showToast();
    }
  });
}
function showToast() {
  setTimeout(() => {
    var toastLiveExample = document.getElementById("liveToast");
    var toast = new Toast(toastLiveExample);
    toast.show();
    setCookieSeasson("login-toast", "showed");
  }, 100);
}
</script>

<style scoped>
i {
  cursor: pointer;
}
.row {
  margin: auto;
  max-width: 92vw;
  padding-top: 10px;
}
.header-content {
  background-color: var(--primary-color);
  max-width: 100vw;
}
.link:hover {
  text-decoration: underline;
}
a {
  color: unset;
  text-decoration: unset;
}
div h4 {
  cursor: pointer;
}
.logout:hover {
  color: rgb(216, 19, 19);
}
.card .list-group {
  text-align: right;
  padding-right: 10px;
}
img {
  max-width: 40px;
  margin-right: 1rem !important;
}
@media (max-width: 768px) {
  .row {
    max-width: 98vw;
  }
}
</style>