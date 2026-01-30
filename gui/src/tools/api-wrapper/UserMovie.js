import { HOST } from "@/main";
import { getAuthManager } from "../AuthManager";

const mgr = getAuthManager();

export async function getMovie(id) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie/" + id, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

export async function searchMovie(query) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie/search?query=" + query, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

export async function getSortedMovies(page, sortID) {
    const user = await mgr?.getUser();
    var sort = getSorteByID(sortID);
    return fetch(HOST + "movie/preview/by-" + sort.sortType + "?page=" + page + "&desc=" + sort.desc, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

export async function getTags() {
    const user = await mgr?.getUser();
    return fetch(HOST + "tag/tags", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

export async function searchByTag(tagId) {
    const user = await mgr?.getUser();
    return fetch(HOST + "tag/tags/" + tagId, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}


export async function getTagsByMovie(movieId) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie/" + movieId + "/tags", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return [];
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

export async function getExportSession() {
    const user = await mgr?.getUser();
    return fetch(HOST + "service/export/session", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.text();
        }
        console.error(response);
        return new Error("Can't load movies!");
    });
}

function getSorteByID(id) {
    var sort = {
        sortType: "",
        desc: false,
    }
    switch (id) {
        case "1":
            sort.sortType = "name";
            sort.desc = true;
            break;
        case "2":
            sort.sortType = "name";
            break;
        case "3":
            sort.sortType = "year";
            sort.desc = true;
            break;
        case "4":
            sort.sortType = "year";
            break;
        default:
            sort.sortType = "name";
            console.log("Unkown id " + id + "! Using id=2 now.");
    }
    return sort;
}