<template>
    <div class="modal fade" id="tagModal" tabindex="-1" aria-labelledby="tagModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="tagModalLabel">Suche einen Film mit folgendem Tag / Schlagwort</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" placeholder="Nach welchen Tag suchen Sie?"
                                aria-label="Nach welchen Tag suchen Sie?" aria-describedby="tag-button"
                                v-model="data.tagName">
                            <button class="btn btn-outline-dark" type="button" id="tag-button">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                        <p class="col-auto"
                            v-for="(tag, index) in data.tags.filter(tag => data.tagName == null || tag.name.toLowerCase().startsWith(data.tagName.toLowerCase()))"
                            :key="index" @click="sendSelectedTag(tag)" data-bs-dismiss="modal">
                            <span class="tag">{{ tag.name }}</span>
                        </p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Schließen</button>
                    <button type="button" class="btn btn-primary">Suchen</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
const { reactive } = require("@vue/reactivity");
import { getTags } from "@/tools/api-wrapper/UserMovie";
import { checkTokenAndRun } from "@/tools/Auth";
import { onMounted } from "vue";
// eslint-disable-next-line
const emit = defineEmits(['tagSelected'])

var data = reactive({
    tags: [],
    tagName: null,
});

onMounted(() => {
    checkTokenAndRun(() => {
        getTags().then(tags => {
            data.tags = tags;
        });
    })
})

function sendSelectedTag(tag) {
    emit('tagSelected', tag)
}
</script>
