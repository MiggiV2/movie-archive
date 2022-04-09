import { HOST } from "@/main";
import { getCookie } from "./Cookies";

export function getMovies(page) {
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

export function searchMovie(query) {
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



export function getSortedMovies(page, sortID) {
    var sort = getSorteByID(sortID);
    return fetch(HOST + "user/sorted-movies/by-" + sort.sortType + "?page=" + page + "&desc=" + sort.desc, {
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