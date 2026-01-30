import { HOST } from "@/main";

export function getMovieCount() {
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

export function getMoviePageCount() {
    return fetch(HOST + "movie/pages", {
        headers: {
            "Content-Type": "application/json",
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        console.error("Can't load movie page count!");
    });
}

export function getConfig() {
    return fetch(HOST + "service/config", {
        headers: {
            "Content-Type": "application/json",
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        console.error("Can't load movie page count!");
    });
}

export function DownloadExport(session) {
    window.open(HOST + "service/export/session/" + session + "/movies.csv");
}