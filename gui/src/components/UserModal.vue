<template>
  <div id="user-modal" class="modal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Nutzer-Infos</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <div class="col-auto">
              <p>Name: {{ data.user.preferred_username }}</p>
              <p>Gruppe: {{ data.user.group }}</p>
              <p>Verifizierte E-Mail: {{ data.user.email_verified }}</p>
              <router-link to="/audit-log" v-if="data.user.isAdmin">
                <p data-bs-dismiss="modal" aria-label="Close">Admin-Log <i class="bi bi-shield-check"></i></p>
              </router-link>
            </div>
            <div class="col"></div>
            <div class="col-auto">
              <img src="/img/avatar.png" alt="USER-IMAGE" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";

const { isAdmin, getPreferredUsername, isUserMailVerified } = require("@/tools/User");
const { reactive } = require("vue");

const data = reactive({
  user: {
    group: undefined,
  },
});

onMounted(() => {
  data.user.preferred_username = getPreferredUsername();
  data.user.group = isAdmin() ? "Admin" : "Nutzer";
  data.user.isAdmin = isAdmin();
  data.user.email_verified = isUserMailVerified() ? "Ja" : "Nein";
});
</script>

<style scoped>
img {
  max-width: 5rem;
  margin-right: 1rem;
}

a {
  text-decoration: unset;
}
</style>