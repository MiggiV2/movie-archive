import { HOST } from "@/main";
import { getCookie } from "./Cookies";

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
        console.error(response);
        return new Error("Can't update movie!");
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
        console.error(response);
        return new Error("Can't delete movie!");
    });
}