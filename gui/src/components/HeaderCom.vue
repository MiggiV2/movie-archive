<template>
  <nav class="header-content">
    <div class="row align-items-center">
      <div class="col-auto">
        <h2>
          <router-link to="/" class="link">MovieArchive </router-link>
        </h2>
      </div>
      <!--desktop-menu-->
      <div class="col" />
      <div class="col-auto desktop">
        <h4 v-if="user.simpleName != null">
          <router-link to="/search"><i class="bi bi-search"></i> Suchen</router-link>
        </h4>
      </div>
      <div class="col-auto desktop">
        <h4 v-if="user.simpleName != null && user.isAdmin">
          <router-link to="/add"><i class="bi bi-plus-lg"></i> Hinzufügen</router-link>
        </h4>
      </div>
      <!--UserName/Login-->
      <div class="col-auto desktop">
        <h4 class="dropdown" v-if="user.simpleName != null">
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
        <h2 v-if="user.simpleName != null">
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
            <router-link to="/add"><i class="bi bi-plus"></i> Neuer Film</router-link>
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
import { isAdmin } from "@/tools/User";
import { reactive } from "vue";
import { Modal } from "bootstrap";
import { getRandomMotto } from "@/tools/Motto";
import UserModal from "@/components/UserModal.vue";
import { onMounted } from "vue";
import { getExportSession } from "@/tools/api-wrapper/UserMovie";
import { DownloadExport } from "@/tools/api-wrapper/PubMovie";
import { getAuthManager, login } from "@/tools/AuthManager";

const mgr = getAuthManager();
var user = reactive({
  simpleName: null,
  fullName: null,
  isAdmin: false,
  motto: "",
});

onMounted(() => {
  mgr?.getUser().then((userData) => {
    if (userData) {
      user.simpleName = userData.profile.name;
      user.fullName = userData.profile.name;
      user.isAdmin = isAdmin();
      user.motto = getRandomMotto(user);
    }
  }).catch((err) => {
    console.error("Error fetching user data:", err);
  });
});

function showUserModal() {
  var modalElement = document.getElementById("user-modal");
  var userModal = new Modal(modalElement);
  userModal.show();
}
function startExport() {
  getExportSession().then(DownloadExport);
}
function openLogin() {
  login();
}
function openLogout() {
  mgr?.removeUser().then(() => {
    console.log("User logged out successfully.");
    localStorage.removeItem("is_admin");
    user.simpleName = null;
    user.fullName = null;
    user.isAdmin = false;
    user.motto = "";
  });
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
  color: var(--background-color);
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