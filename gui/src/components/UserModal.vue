<template>
  <div id="user-modal" class="modal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Nutzer-Infos</h5>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
            aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <div class="col-auto">
              <p>Name: {{ data.user.preferred_username }}</p>
              <p>Gruppe: {{ data.user.group }}</p>
              <p>Verifizierte E-Mail: {{ data.user.email_verified }}</p>
            </div>
            <div class="col"></div>
            <div class="col-auto">
              <img
                src="/img/avatar.png"
                alt="USER-IMAGE"
              />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const { getUser, isAdmin } = require("@/tools/User");
const { reactive } = require("@vue/reactivity");

const data = reactive({
  user: {
    group: undefined,
  },
});
data.user = getUser();
data.user.group = isAdmin() ? "Admin" : "Nutzer";
data.user.email_verified = data.user.email_verified ? "Ja" : "Nein";
</script>

<style scoped>
img {
  max-width: 5rem;
  margin-right: 1rem;
}
</style>