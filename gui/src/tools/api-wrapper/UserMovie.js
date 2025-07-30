import { getCookie } from "@/tools/Cookies";
import { checkToken } from "@/tools/Auth";

const HOST = process.env.VUE_APP_API_HOST;

export async function getMovies(page) {
    await checkToken();
    return fetch(HOST + "user/get-movies?page=" + page, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "user/search?query=" + query, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    var sort = getSorteByID(sortID);
    return fetch(HOST + "user/preview-movies/by-" + sort.sortType + "?page=" + page + "&desc=" + sort.desc, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "user/tags", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "user/tags/" + tagId, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "user/tags/by-movie/" + movieId, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "user/export/session", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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