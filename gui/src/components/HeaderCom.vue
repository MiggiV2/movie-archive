<template>
  <nav class="header-content">
    <div class="row align-items-center">
      <div class="col-auto">
        <h2>
          <router-link to="/" class="link"><i class="bi bi-camera-reels"></i> MovieArchive
            <i class="bi bi-film"></i></router-link>
        </h2>
      </div>
      <!--desktop-menu-->
      <div class="col" />
      <div class="col-auto desktop">
        <h4 v-if="user.simpleName.length > 0 && user.isAdmin">
          <router-link to="/add"><i class="bi bi-plus-circle"></i> Neuer Film</router-link>
        </h4>
      </div>
      <div class="col-auto desktop">
        <h4 v-if="user.simpleName.length > 0">
          <router-link to="/search"><i class="bi bi-search"></i> Suchen</router-link>
        </h4>
      </div>
      <!--UserName/Login-->
      <div class="col-auto desktop">
        <h4 class="dropdown" v-if="user.simpleName.length > 0">
          <div class="dropdown">
            <a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown"
              aria-expanded="false">
              <i class="bi bi-person-circle"></i> {{ user.simpleName }}
            </a>

            <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
              <li>
                <a class="dropdown-item" @click="showUserModal()">Infos <i
                    class="bi bi-person-bounding-box icon-right"></i></a>
              </li>
              <li class="mt-2">
                <a class="dropdown-item" @click="startExport()">Export <i
                    class="bi bi-arrow-down-circle icon-right"></i></a>
              </li>
              <hr />
              <li>
                <a class="dropdown-item logout" @click="openLogout()">
                  Logout <i class="bi bi-box-arrow-right icon-right"></i>
                </a>
              </li>
            </ul>
          </div>
        </h4>
        <h4 v-else @click="openLogin()">
          <i class="bi bi-box-arrow-in-right"></i> Login
        </h4>
      </div>
      <!--mobile-menu-->
      <div class="col-auto mobile">
        <h2 v-if="user.simpleName.length > 0">
          <a data-bs-toggle="collapse" href="#mobileMenu" role="button" aria-expanded="false"
            aria-controls="mobileMenu">
            <i class="bi bi-list"></i>
          </a>
        </h2>
        <h4 v-else @click="openLogin()">
          <i class="bi bi-box-arrow-in-right"></i> Login
        </h4>
      </div>
    </div>
    <div class="collapse" id="mobileMenu">
      <div class="card card-body">
        <hr id="menu-hr" />
        <div class="row">
          <div class="col-6" @click="startExport()">
            <i class="bi bi-arrow-down-circle"></i> Export
          </div>
          <div class="col-6" @click="showUserModal()">
            {{ user.simpleName }} <i class="bi bi-person-circle"></i>
          </div>
          <div v-if="user.isAdmin" class="col-6">
            <router-link to="/add"><i class="bi bi-plus-circle"></i> Neuer Film</router-link>
          </div>
          <div class="col-6">
            <a @click="openLogout()">
              Logout <i class="bi bi-box-arrow-right"></i>
            </a>
          </div>
        </div>
      </div>
    </div>
  </nav>
  <div id="dummy" />
  <!-- toast -->
  <div>
    <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
      <div id="liveToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
        <div class="toast-header">
          <img src="/img/unlock.png" class="rounded me-2" alt="..." />
          <strong class="me-auto">{{ user.motto }}</strong>
          <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      </div>
    </div>
  </div>
  <UserModal />
</template>

<script setup>
import { getCookie, setCookieSeasson } from "@/tools/Cookies";
import { getUserName, getUsersSimpleName, isAdmin } from "@/tools/User";
import { checkTokenAndRun, openLogin, openLogout } from "@/tools/Auth";
import { reactive } from "@vue/reactivity";
import { Modal, Toast } from "bootstrap";
import { getRandomMotto } from "@/tools/Motto";
import UserModal from "@/components/UserModal.vue";
import { onMounted } from "vue";
import { getExportSession } from "@/tools/api-wrapper/UserMovie";
import { DownloadExport } from "@/tools/api-wrapper/PubMovie";

var user = reactive({
  simpleName: "",
  fullName: "",
  isAdmin: false,
  motto: "",
});

onMounted(() => {
  if (getCookie("refreshToken")) {
    checkTokenAndRun(() => {
      user.simpleName = getUsersSimpleName();
      user.fullName = getUserName();
      user.isAdmin = isAdmin();
      user.motto = getRandomMotto(user);
      if (getCookie("login-toast") != "showed") {
        showToast();
        console.log(getRandomMotto(user));
      }
    });
  }
});

function showToast() {
  setTimeout(() => {
    var toastLiveExample = document.getElementById("liveToast");
    var toast = new Toast(toastLiveExample);
    toast.show();
    setCookieSeasson("login-toast", "showed");
  }, 100);
}
function showUserModal() {
  var modalElement = document.getElementById("user-modal");
  var userModal = new Modal(modalElement);
  userModal.show();
}
function startExport() {
  getExportSession().then(DownloadExport);
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

nav {
  position: fixed;
  width: 100vw;
  top: 0px;
  z-index: 100;
}

.header-content {
  background-color: var(--primary-color);
}

#dummy {
  height: 55px;
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

#menu-hr {
  margin: 0 0 0.5rem;
}

#mobileMenu .row div {
  padding-top: 10px;
}

.icon-right {
  right: 1.2rem;
  position: absolute;
}

.dropdown-menu hr {
  margin: 8px 0;
}

@media (max-width: 768px) {
  .row {
    max-width: 98vw;
  }
}
</style>