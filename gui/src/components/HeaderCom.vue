<template>
  <div class="header-content">
    <div class="row align-items-center">
      <div class="col-auto">
        <h2>
          <a href="/">Mini-Diary</a>
        </h2>
      </div>
      <div class="col" />
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
            {{ user.name }} <i class="bi bi-person-circle"></i>
          </a>
          <a v-else @click="openLogin()">
            Login <i class="bi bi-box-arrow-in-right"></i>
          </a>
          <a class="logout" v-if="user.name.length > 0" @click="logout()">
            Logout <i class="bi bi-box-arrow-right"></i>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { setCookie, getCookie } from "../tools/Cookies";
import { checkToken } from "../tools/Auth";

var user = reactive({
  name:
    getCookie("access_token") && localStorage.getItem("username")
      ? localStorage.getItem("username")
      : "",
});
var authURL =
  "http://localhost:8180/realms/quarkus/protocol/openid-connect/auth" +
  "?client_id=gui" +
  "&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fauth" +
  "&state=351fd66e-93ac-4feb-b36a-43f81e1bcbfd" +
  "&response_type=code" +
  "&scope=openid";

var logoutURL =
  "http://localhost:8180/realms/quarkus/protocol/openid-connect/logout" +
  "?redirect_uri=http%3A%2F%2Flocalhost:3000";

function openLogin() {
  localStorage.setItem("redirect", window.location.pathname);
  window.location = authURL;
}

function logout() {
  localStorage.removeItem("username");
  localStorage.removeItem("isAdmin");
  setCookie("access_token", "", -1);
  setCookie("refresh_token", "", -1);
  window.location = logoutURL;
}

checkToken();
</script>

<style scoped>
i {
  cursor: pointer;
}
.row {
  margin: auto;
  max-width: 85vw;
  padding-top: 10px;
}
.header-content {
  background-color: var(--primary-color);
  max-width: 100vw;
}
a {
  color: unset;
  text-decoration: unset;
}
a:hover {
  color: unset;
  text-decoration: underline;
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
@media (max-width: 768px) {
  .row {
    max-width: 98vw;
  }
}
</style>