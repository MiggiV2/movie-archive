import { HOST } from "@/main";
import { getAuthManager } from "../AuthManager";

const mgr = getAuthManager();

export async function addMovie(movie) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie", {
        body: JSON.stringify(movie),
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.status == 401) {
            throw new Error("You are not authorized to add movies!");
        }
        if (response.status == 403) {
            throw new Error("You only admins can add movies!");
        }
        console.error(response);
        throw new Error("Can't add movie!");
    });
}

export async function updateMovie(movie) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie", {
        body: JSON.stringify(movie),
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.ok) {
            return {};
        }
        if (response.status == 401) {
            throw new Error("You are not authorized to update movies!");
        }
        if (response.status == 403) {
            throw new Error("You only admins can update movies!");
        }
        console.error(response);
        throw new Error("Can't update movie!");
    });
}

export async function deleteMovie(movie) {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie?id=" + movie.id, {
        body: JSON.stringify(movie),
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.ok) {
            return response;
        }
        if (response.status == 401) {
            throw new Error("You are not authorized to delete movies!");
        }
        if (response.status == 403) {
            throw new Error("You only admins can delete movies!");
        }
        console.error(response);
        throw new Error("Can't delete movie!");
    });
}

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

export async function getMovieCount() {
    return fetch(HOST + "movie/count", {
        headers: {
            "Content-Type": "application/json",
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        console.error("Can't load movie count!");
    });
}

export async function getMoviePageCount() {
    const user = await mgr?.getUser();
    return fetch(HOST + "movie/pages", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token,
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        console.error("Can't load movie page count!");
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
