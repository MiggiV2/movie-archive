const HOST = process.env.VUE_APP_API_HOST;

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

export function getMoviePageCount() {
    return fetch(HOST + "public/movie-page-count", {
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
    window.open(HOST + "public/export/csv/" + session + "/export-movies.csv");
}