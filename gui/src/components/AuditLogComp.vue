<template>
  <!-- log -->
  <div class="container box">
    <h2>Admin Audit Log</h2>
    <div class="log" v-for="(item, index) in data.log" :key="index">
      <p>{{ item.userName }}</p>
      <p>{{ item.message }}</p>
      <small>{{ item.date.substring(0, 19) }}</small>
    </div>
    <div class="log" v-if="!data.isLoading && data.log.length == 0">
      <p>No logs yet...</p>
    </div>
  </div>
  <!-- spinner -->
  <div v-if="data.isLoading" class="d-flex justify-content-center">
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

const {
  getAuditLog,
  getAuditLogPageCount,
} = require("@/tools/api-wrapper/AdminMovie");
const { reactive } = require("@vue/reactivity");

const data = reactive({
  log: [],
  isLoading: true,
  page: 0,
  maxPage: 0,
});

window.onscroll = function () {
  var scrollPosition =
    document.documentElement.scrollTop || document.body.scrollTop;
  if (
    document.body.scrollHeight - scrollPosition < 1200 &&
    data.page < data.maxPage &&
    !data.isLoading
  ) {
    if (window.location.pathname === "/audit-log") {
      data.page++;
      loadLog();
    }
  }
};

onMounted(() => {
  loadLog();
});

async function loadLog() {
  await getAuditLog(data.page)
    .then((log) => {
      data.log = data.log.concat(log);
    })
    .finally(() => {
      data.isLoading = false;
    });
  await getAuditLogPageCount()
    .then((count) => {
      data.maxPage = count;
    });
}
</script>

<style scoped>
.log {
  margin-top: 3rem;
  margin-bottom: 1rem;
}

.log p,
.log small {
  margin: 5px;
}

.container {
  margin-bottom: 6rem;
}
</style>