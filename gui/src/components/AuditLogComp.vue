<template>
  <!-- log -->
  <div class="container">
    <h2 class="box">Admin Audit Log</h2>
    <div class="box" v-for="(item, index) in data.log" :key="index">
      <p>{{ item.userName }}: {{ item.message }}</p>
      <small>{{ item.date.substring(0, 19) }}</small>
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
const {
  getAuditLog,
  getAuditLogPageCount,
} = require("@/tools/api-wrapper/AdminMovie");
const { checkTokenAndRun } = require("@/tools/Auth");
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
    if (window.location.pathname == "/audit-log") {
      data.page ++;
      loadLog();
    }
  }
};

checkTokenAndRun(() => {
  loadLog();
  getAuditLogPageCount().then((count) => {
    data.maxPage = count;
  });
});

function loadLog() {
  checkTokenAndRun(() => {
    getAuditLog(data.page)
      .then((log) => {
        data.log = data.log.concat(log);
      })
      .finally(() => {
        data.isLoading = false;
      });
  });
}
</script>

<style scoped>
.box {
  text-align: left;
}
h2.box {
  text-align: center;
}
.container {
  margin-bottom: 10rem;
}
</style>