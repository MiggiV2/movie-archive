import { setCookieInSec, getCookie } from "./Cookies";
import { HOST } from "../main.js";

export function login(code, funcToRun) {
    fetch(HOST + "code-exchange", {
            method: "POST",
            credentails: "same-origin",
            mode: "cors",
            body: code,
            headers: {
                "Content-Type": "application/json",
            },
        })
        .then((response) => {
            if (response.status == 200) {
                return response.json();
            }
            alert("Error! " + response.statusText);
        })
        .then((tokens) => {
            if (tokens != null) {
                setCookieInSec("access_token", tokens.access_token, tokens.expires_in);
                setCookieInSec(
                    "refresh_token",
                    tokens.refresh_token,
                    tokens.refresh_expires_in
                );
                getUserInfo(funcToRun);
            }
        });
}

export function getUserInfo(funcToRun) {
    fetch(HOST + "user-info", {
            method: "GET",
            credentails: "same-origin",
            mode: "cors",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + getCookie("access_token")
            },
        })
        .then((response) => {
            if (response.status == 200) {
                return response.json();
            }
            alert("Cant load user info! " + response.statusText);
        })
        .then((user) => {
            if (user != null) {
                localStorage.setItem("username", user.name);
                localStorage.setItem("isAdmin", user.admin);
            }
            if (typeof funcToRun == "function") {
                funcToRun();
            } else {
                window.location = localStorage.getItem("redirect") ? localStorage.getItem("redirect") : "/";
            }
        });
}

export function refreshToken(funcToRun) {
    fetch(HOST + "refresh-token", {
            method: "POST",
            credentails: "same-origin",
            mode: "cors",
            body: getCookie("refresh_token"),
            headers: {
                "Content-Type": "application/json",
            },
        })
        .then((response) => {
            if (response.status == 200) {
                return response.json();
            }
            alert("Oopps! Something went wrong! " + response.statusText);
        })
        .then((tokens) => {
            if (tokens != null) {
                setCookieInSec("access_token", tokens.access_token, tokens.expires_in);
                setCookieInSec(
                    "refresh_token",
                    tokens.refresh_token,
                    tokens.refresh_expires_in
                );
                getUserInfo(funcToRun);
            }
        });
}

export function checkToken() {
    if (getCookie("refresh_token") && !getCookie("access_token")) {
        refreshToken();
    }
}

export function checkTokenAndRun(funcToRun) {
    if (getCookie("access_token")) {
        funcToRun();
    } else if (getCookie("refresh_token")) {
        refreshToken(funcToRun);
    }
    if (!getCookie("access_token") && !getCookie("refresh_token")) {
        alert("Pls login first!");
    }
}

export function getURLHashParams() {
    var hash = window.location.hash.substr(1);
    var result = hash.split('&').reduce(function(res, item) {
        var parts = item.split('=');
        res[parts[0]] = parts[1];
        return res;
    }, {});
    return result;
}

export function isAdmin() {
    if (!getCookie("access_token")) {
        if (getCookie("refresh_token")) {
            refreshToken(goToAdmin);
        }
        return false;
    }
    var user = parseJwt(getCookie("access_token"));
    return user.apiRoles.includes("admin");
}

function goToAdmin() {
    window.location = "/admin";
}

function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}