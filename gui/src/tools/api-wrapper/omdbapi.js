import { OMDB_KEY } from "@/main";

export function getPostUrl(movie) {
    return fetch("https://www.omdbapi.com/?apikey=" + OMDB_KEY + "&t=" + movie.name + "&y=" + movie.year + "&type=movie&plot=short&r=json")
    .then(r => {
        if(r.ok) {
            return r.json();
        }
        console.error(r);
        return new Error("Can't load poster!");
    });
}