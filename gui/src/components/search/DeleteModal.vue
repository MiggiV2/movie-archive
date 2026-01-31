<template>
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModallLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Bist du sicher?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div v-if="!data.delete.hasResponed">
                        Du bist dabei den Film '{{ props.movie.name }}' zu entfernen!
                    </div>
                    <div v-else-if="data.delete.wasSuccessfull">
                        Film '{{ props.movie.name }}' wurde erfolglreich entfernt!
                    </div>
                    <div v-else>
                        Leider ist etwas schief gelaufen! Versuchen Sie es bitte später
                        erneut. Vilen Dank für das Verständniss.
                    </div>
                </div>
                <div class="modal-footer">
                    <button v-if="!data.delete.hasResponed" type="button" class="btn btn-secondary"
                        data-bs-target="#movieModal" data-bs-toggle="modal">
                        Abbrechen
                    </button>
                    <button v-if="!data.delete.hasResponed" type="button" class="btn btn-primary" @click="sendDelete()">
                        <div v-if="data.delete.isSending" class="spinner-border spinner-border-sm" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        Ja, sicher
                    </button>
                    <button v-else type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        Schließen
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
const { reactive } = require("vue");
import { deleteMovie } from "@/tools/api-wrapper/Movie";
// eslint-disable-next-line
const props = defineProps({
    movie: {},
});
var data = reactive({
    delete: {
        isSending: false,
        hasResponed: false,
        wasSuccessfull: false,
    },
});

function sendDelete() {
    data.delete.isSending = true;
    deleteMovie(props.movie)
        .then(() => {
            data.delete.wasSuccessfull = true;
        })
        .catch((e) => {
            console.error(e);
            data.delete.wasSuccessfull = false;
        })
        .finally(() => {
            data.delete.isSending = false;
            data.delete.hasResponed = true;
            setTimeout(() => {
                window.location.reload();
            }, 3000)
        });
}

</script>
