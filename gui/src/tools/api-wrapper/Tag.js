import { HOST } from "@/main";
import { getAuthManager } from "../AuthManager";

const mgr = getAuthManager();

export async function getTags() {
    const user = await mgr?.getUser();
    return fetch(HOST + "tag/tags", {
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

export async function searchByTag(tagId) {
    const user = await mgr?.getUser();
    return fetch(HOST + "tag/tags/" + tagId, {
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
