import { HOST } from "@/main";
import { getAuthManager } from "../AuthManager";

const mgr = getAuthManager();

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
