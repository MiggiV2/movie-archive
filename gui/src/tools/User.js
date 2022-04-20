import { refreshToken } from "@/tools/Auth";
import { getCookie } from "@/tools/Cookies";

export function isAdmin() {
    var user = getUser();
    return (user.realm_access == undefined) ? false : user.realm_access.roles.includes("admin");
}

export function getUserName() {
    const user = getUser();
    return user.name == undefined ? user.preferred_username : user.name;
}

export function getUsersSimpleName() {
    return getUser().preferred_username;
}

export function getUsersFullName() {
    return getUser().name;
}

export function getUser() {
    if (!getCookie("accessToken")) {
        if (getCookie("refreshToken")) {
            refreshToken();
        } else {
            return {};
        }
    }
    return parseJwt(getCookie("accessToken"));
}

function parseJwt(token) {
    if (token == null || token == "") {
        return {};
    }
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}