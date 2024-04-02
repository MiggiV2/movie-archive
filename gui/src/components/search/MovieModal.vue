<template>
    <div class="modal fade" id="movieModal" tabindex="-1" aria-labelledby="movieModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="movieModalLabel">
                        Film - Details
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- Modal-Body -->
                    <div class="row">
                        <!--Data-->
                        <div class="col">
                            <h4>{{ props.movie.name }}</h4>
                            <!--Poster_Mobile-->
                            <div class="mobile" v-if="props.movie.omdbData != undefined">
                                <img v-if="props.movie.omdbData.Poster != undefined" :src="props.movie.omdbData.Poster"
                                    alt="Movie Poster">
                            </div>
                            <p>Zu finden in {{ props.movie.block }}</p>
                            <p>Aus dem Jahre {{ props.movie.year }}</p>
                            <p>Type: {{ props.movie.type }}</p>
                            <p class="wikis">
                                <a class="btn btn-primary" v-if="allowIframe(props.movie.wikiUrl)"
                                    :href="props.movie.wikiUrl">Mehr Details auf Wikipedia <i
                                        class="bi bi-globe"></i></a>
                                <a class="btn btn-primary" v-else-if="isVideoBuster(props.movie.wikiUrl)"
                                    :href="props.movie.wikiUrl">
                                    VideoBuster Link
                                </a>
                            </p>
                            <p class="youtube-search">
                                <a class="btn btn-danger"
                                    :href="'https://www.youtube.com/results?search_query=Trailer ' + props.movie.name">
                                    Trailer auf Youtube suchen <i class="bi bi-youtube"></i>
                                </a>
                            </p>
                        </div>
                        <!--Poster_Desktop-->
                        <div class="col desktop" v-if="props.movie.omdbData != undefined">
                            <img v-if="props.movie.omdbData.Poster != undefined" :src="props.movie.omdbData.Poster"
                                alt="Movie Poster">
                            <div v-else>
                                <p>Keine Poster gefunden...</p>
                                <img src="/img/default-poster.png"
                                    alt="Default Poster from https://pixabay.com/vectors/android-sci-fi-retro-poster-7479380/"
                                    width="300" height="420">
                            </div>
                        </div>
                    </div>
                    <hr v-if="props.movie.tags.length > 0">
                    <!--Tags-->
                    <div class="row justify-content-center">
                        <div class="col-auto" v-for="tag in props.movie.tags" :key="tag.name"
                            @click="loadMoviesByTag(tag)" data-bs-dismiss="modal">
                            <p class="tag-outline">#{{ tag.name }}</p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="row">
                        <div class="col">
                            <button v-if="userIsAdmin" type="button" class="btn btn-outline-danger"
                                data-bs-target="#deleteModal" data-bs-toggle="modal">
                                <i class="bi bi-trash3"></i> <span class="desktop">Löschen</span>
                            </button>
                        </div>
                        <div class="col-auto">
                            <button v-if="userIsAdmin" @click="showUpdateModal()" type="button"
                                class="btn btn-outline-primary" data-bs-dismiss="modal">
                                <i class="bi bi-pencil"></i> <span class="desktop">Update</span>
                            </button>
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                                Schließen
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { isAdmin } from '@/tools/User';
import { wikiWhiteList, videoBusterList } from "@/tools/SearchList";
import { Modal } from "bootstrap";

// eslint-disable-next-line
const props = defineProps({
    movie: {}
});

const userIsAdmin = isAdmin();

function allowIframe(url) {
    return startURLWith(url, wikiWhiteList);
}

function isVideoBuster(url) {
    return startURLWith(url, videoBusterList);
}

function showUpdateModal() {
    var modalElement = document.getElementById("updateModal");
    var modal = new Modal(modalElement);
    modal.show();
}

function startURLWith(url, domainArray) {
    for (let index = 0; index < domainArray.length; index++) {
        const element = domainArray[index];
        if (url.startsWith(element)) {
            return true;
        }
    }
    return false;
}
</script>

<style scoped>
#movieModal .modal-body {
    text-align: center;
    font-size: 1.2rem;
}

#movieModal .modal-body h4 {
    margin-top: 0.8rem;
    margin-bottom: 1.2rem;
}

#movieModal .modal-body>hr {
    margin-top: 3rem;
}

#movieModal .row.justify-content-center {
    margin-top: 1.5rem;
}

#movieModal .modal-body .col-auto {
    padding-left: 5px;
    padding-right: 5px;
}

#movieModal p.tag-outline {
    color: rgb(0, 102, 255);
    margin-bottom: 5px;
    font-size: 0.9rem;
    cursor: pointer;
}

img {
    border-radius: 8px;
    -webkit-box-shadow: 4px 4px 5px 0px rgba(0, 0, 0, 0.75);
    -moz-box-shadow: 4px 4px 5px 0px rgba(0, 0, 0, 0.75);
    box-shadow: 4px 4px 5px 0px rgba(0, 0, 0, 0.75);
}

.wikis {
    margin-top: 11rem;
}

.modal-footer>.row {
    width: 95%;
}

.modal-footer button {
    margin-left: 1rem;
}

/** Molbile */
@media (max-width: 768px) {
    .wikis {
        margin-top: 2rem;
    }

    .mobile>img {
        margin-bottom: 2rem;
    }
}
</style>