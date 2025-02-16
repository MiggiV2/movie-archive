import { getCookie } from "@/tools/Cookies";
import { checkToken } from "@/tools/Auth";

const HOST = process.env.VUE_APP_API_HOST;

export async function addMovie(movie) {
    await checkToken();
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

export async function updateMovie(movie) {
    await checkToken();
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


export async function deleteMovie(movie) {
    await checkToken();
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

export async function getAuditLog(page) {
    await checkToken();
    return fetch(HOST + "admin/auditlog?page=" + page, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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
    await checkToken();
    return fetch(HOST + "admin/auditlog-page-count", {
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("accessToken")
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