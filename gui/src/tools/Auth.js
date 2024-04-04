import { setCookieInSec, getCookie, setCookie, setCookieSeasson } from "@/tools/Cookies";
import { v4 as uuidv4 } from "uuid";
import { getUser } from "@/tools/User";

var authURL;
var logoutURL;
var redirectURI = window.location.protocol.replace(":", "%3A") + "%2F%2F" + window.location.host;
// Config
const CLIENT_ID = process.env.VUE_APP_AUTH_CLIENT_ID;
const HOST = process.env.VUE_APP_API_HOST;
const AUTH_HOST = process.env.VUE_APP_AUTH_HOST;

setTimeout(() => {
    authURL = AUTH_HOST +
        "realms/quarkus/protocol/openid-connect/auth" +
        "?client_id=backend-service" +
        "&redirect_uri=" + redirectURI + "%2Fauth" +
        "&response_type=code" +
        "&scope=openid&state=";
    logoutURL = AUTH_HOST +
        "realms/quarkus/protocol/openid-connect/logout" +
        "?post_logout_redirect_ur=" + redirectURI +
        "&client_id=" + CLIENT_ID;
}, 100)

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
            window.location = "/logout";
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
            if (response.status === 200) {
                return response.json();
            }
            if (response.status === 400) {
                window.location = "/logout";
            } else if (getCookie("error-showed") !== "yes"){
                setCookieInSec("error-showed", "yes", 2);
                alert("Oopps! Something went wrong! " + response.statusText);
            }
        })
        .then((tokens) => {
            if (tokens != null) {
                setCookieInSec("accessToken", tokens.accessToken, tokens.expiresIn);
                setCookieInSec(
                    "refreshToken",
                    tokens.refreshToken,
                    tokens.refreshExpiresIn
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