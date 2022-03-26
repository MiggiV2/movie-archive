import { HOST } from "@/main";

export function getMovieCount() {
    return fetch(HOST + "public/movie-count", {
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