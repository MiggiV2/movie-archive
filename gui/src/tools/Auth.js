import { setCookieInSec, getCookie, setCookie, setCookieSeasson } from "./Cookies";
import { HOST } from "../main.js";
import { v4 as uuidv4 } from "uuid";

var authURL =
    "http://localhost:8180/realms/quarkus/protocol/openid-connect/auth" +
    "?client_id=backend-service" +
    "&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fauth" +
    "&response_type=code" +
    "&scope=openid&state=";

var logoutURL =
    "http://localhost:8180/realms/quarkus/protocol/openid-connect/logout" +
    "?redirect_uri=http%3A%2F%2Flocalhost:3000";

export function openLogin() {
    localStorage.setItem("redirect", window.location.pathname);
    var uuid = uuidv4();
    setCookieSeasson("state", uuid);
    window.location = authURL + uuid;
}

export function openLogout() {
    setCookie("accessToken", "", -1);
    setCookie("refreshToken", "", -1);
    setCookie("login-toast", "", -1);
    window.location = logoutURL;
}

export function login(code) {
    var data = {
        "code": code
    };
    return fetch(HOST + "public/code-exchange", {
            method: "POST",
            body: JSON.stringify(data),
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
                console.log("Expires in " + tokens.expiresIn);
                setCookieInSec("accessToken", tokens.accessToken, tokens.expiresIn);
                setCookieInSec("state", "", -1);
                setCookieInSec(
                    "refreshToken",
                    tokens.refreshToken,
                    tokens.refreshExpiresIn
                );
                console.log("Welcome " + getUser().preferred_username + "!");
                if (localStorage.getItem("redirect") != null) {
                    window.location = localStorage.getItem("redirect");
                } else {
                    window.location = "/";
                }
            }
        }).catch(e => {
            console.error(e);
            alert("Something went wrong!");
        });
}

export function refreshToken() {
    var data = {
        "refreshToken": getCookie("refreshToken")
    };
    return fetch(HOST + "public/refresh-exchange", {
            method: "POST",
            credentails: "same-origin",
            mode: "cors",
            body: JSON.stringify(data),
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
                console.log("Expires in " + tokens.expiresIn)
                setCookieInSec("accessToken", tokens.accessToken, tokens.expiresIn);
                setCookieInSec(
                    "refreshToken",
                    tokens.refreshToken,
                    tokens.refresh_expires_in
                );
            }
        });
}

export function checkToken() {
    if (getCookie("refreshToken") && !getCookie("accessToken")) {
        refreshToken();
    }
}

export function checkTokenAndRun(callBack) {
    if (getCookie("accessToken")) {
        if (typeof callBack == "function") {
            callBack();
        } else {
            console.log("nope - " + typeof callBack);
        }
    } else if (getCookie("refreshToken")) {
        refreshToken().then(() => {
            if (typeof callBack == "function") {
                callBack();
            } else {
                console.log("nope - " + typeof callBack);
            }
        });
    }
    if (!getCookie("accessToken") && !getCookie("refreshToken")) {
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
    return getUser().realm_access.roles.includes("admin");
}

export function getUserName() {
    return getUser().preferred_username;
}

export function getUser() {
    if (!getCookie("accessToken")) {
        if (getCookie("refreshToken")) {
            refreshToken();
        } else {
            console.log("Can't update user!");
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