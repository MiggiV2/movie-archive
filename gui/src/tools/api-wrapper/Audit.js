import { HOST } from "@/main";
import { getAuthManager } from "../AuthManager";

var mgr = getAuthManager();

export async function getAuditLog(page) {
    const user = await mgr?.getUser();
    return fetch(HOST + "audit?page=" + page, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.status == 204) {
            throw new Error("Expected body. Got no content!");
        }
        if (response.status == 401) {
            throw new Error("You are not authorized to read this log!");
        }
        if (response.status == 403) {
            throw new Error("You only admins can read this log!");
        }
        console.error(response);
        throw new Error("Failed to read the audit log!");
    });
}

export async function getAuditLogPageCount() {
    const user = await mgr?.getUser();
    return fetch(HOST + "audit/pages", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + user.access_token
        },
    }).then(response => {
        if (response.status == 200) {
            return response.json();
        }
        if (response.status == 204) {
            throw new Error("Expected body. Got no content!");
        }
        if (response.status == 401) {
            throw new Error("You are not authorized to read this log!");
        }
        if (response.status == 403) {
            throw new Error("You only admins can read this log!");
        }
        console.error(response);
        throw new Error("Failed to read the audit log!");
    });
}
