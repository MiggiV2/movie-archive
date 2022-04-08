import { HOST } from "@/main";
import { getCookie } from "./Cookies";

export function addMovie(movie) {
    return fetch(HOST + "admin/add-movie", {
        body: JSON.stringify(movie),
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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

export function updateMovie(movie) {
    return fetch(HOST + "admin/update-movie", {
        body: JSON.stringify(movie),
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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


export function deleteMovie(movie) {
    return fetch(HOST + "admin/delete-movie?id=" + movie.id, {
        body: JSON.stringify(movie),
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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