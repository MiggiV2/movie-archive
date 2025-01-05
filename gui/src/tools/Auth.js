import { setCookieInSec, getCookie, setCookie, setCookieSeasson } from "@/tools/Cookies";
import { getUserName, initUserData } from "@/tools/User";

const redirectUrl = window.location.protocol + "//" + window.location.host + "/auth";

export async function openLogin() {
    const config = await fetchOpenIdConnectDiscovery();
    const state = generateRandomString(42);
    const authURL = config.authorization_endpoint +
        "?client_id=" + process.env.VUE_APP_AUTH_CLIENT_ID +
        "&redirect_uri=" + redirectUrl.replace(":", "%3A").replaceAll("/", "%2F") +
        "&response_type=code" +
        "&scope=openid%20email%20profile&state=" + state;
    const codeVerifier = generateRandomString(128);

    localStorage.setItem("codeVerifier", codeVerifier);
    localStorage.setItem("redirect", window.location.pathname);
    setCookieSeasson("state", state);

    generateCodeChallenge(codeVerifier)
        .then(codeChallenge => {
            window.location = authURL + "&code_challenge_method=S256&code_challenge=" + codeChallenge;
        });
}

export function openLogout() {
    console.log("Logout is not implemented yet!");

    setCookie("accessToken", "", -1);
    setCookie("refreshToken", "", -1);
    setCookie("login-toast", "", -1);

    localStorage.removeItem("name");
    localStorage.removeItem("preferred_username");

    window.location = "/";
}

export async function login(code) {
    const tokens = await exchangeCodeForToken(code);
    setCookieInSec("accessToken", tokens.access_token, tokens.expires_in);
    setCookieInSec("state", "", -1);
    setCookieInSec(
        "refreshToken",
        tokens.refresh_token,
        tokens.expires_in * 300
    );
    await initUserData();
    console.log("Welcome " + getUserName() + "!");
    const url = localStorage.getItem("redirect");
    localStorage.removeItem("redirect");
    localStorage.removeItem("codeVerifier");
    if (url != null) {
        window.location = url;
    } else {
        window.location = "/";
    }
}

async function fetchOpenIdConnectDiscovery() {
    const config = localStorage.getItem("OpenIdConnectDiscovery");
    if (config) {
        return JSON.parse(config);
    }

    const url = process.env.VUE_APP_AUTH_HOST + "/.well-known/openid-configuration";
    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`Error: ${response.status} ${response.statusText}`);
        }

        const discoveryResponse = await response.json();
        localStorage.setItem("OpenIdConnectDiscovery", JSON.stringify(discoveryResponse));
        return discoveryResponse; // This will contain the access token and possibly a refresh token

    } catch (error) {
        console.error('Error fetching OpenID Connect Discovery 1.0:', error);
        throw error; // Rethrow the error for further handling if needed
    }
}

async function exchangeCodeForToken(code) {
    const config = await fetchOpenIdConnectDiscovery();
    const tokenEndpoint = config.token_endpoint;
    const codeVerifier = localStorage.getItem("codeVerifier");
    const clientId = process.env.VUE_APP_AUTH_CLIENT_ID;

    // Prepare the request body
    const body = new URLSearchParams();
    body.append('grant_type', 'authorization_code');
    body.append('code', code);
    body.append('redirect_uri', redirectUrl);
    body.append('client_id', clientId);
    body.append('code_verifier', codeVerifier);

    try {
        const response = await fetch(tokenEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: body.toString(),
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status} ${response.statusText}`);
        }

        const tokenResponse = await response.json();
        return tokenResponse; // This will contain the access token and possibly a refresh token
    } catch (error) {
        console.error('Error exchanging code for token:', error);
        throw error; // Rethrow the error for further handling if needed
    }
}

export async function runRefreshTokenFlow() {
    const refreshToken = getCookie("refreshToken")
    const config = await fetchOpenIdConnectDiscovery();
    const tokenEndpoint = config.token_endpoint;

    const params = new URLSearchParams();
    params.append('grant_type', 'refresh_token');
    params.append('refresh_token', refreshToken);
    params.append('client_id', process.env.VUE_APP_AUTH_CLIENT_ID); // Replace with your actual client ID

    try {
        const response = await fetch(tokenEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params.toString(),
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status} ${response.statusText}`);
        }

        const tokens = await response.json();
        setCookieInSec("accessToken", tokens.access_token, tokens.expires_in);
        setCookieInSec(
            "refreshToken",
            tokens.refresh_token,
            tokens.expires_in * 300
        );
        return tokens; // This will contain the new access token and possibly a new refresh token
    } catch (error) {
        console.error('Failed to refresh access token:', error);
        throw error; // Rethrow the error for further handling if needed
    }
}

export function checkToken() {
    if (getCookie("refreshToken") && !getCookie("accessToken")) {
        runRefreshTokenFlow();
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
        runRefreshTokenFlow().then(() => {
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
    var result = hash.split('&').reduce(function (res, item) {
        var parts = item.split('=');
        res[parts[0]] = parts[1];
        return res;
    }, {});
    return result;
}

// PKCE
// Function to generate a random string (code verifier)
function generateRandomString(length = 128) {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let codeVerifier = '';
    for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * charset.length);
        codeVerifier += charset[randomIndex];
    }
    return codeVerifier;
}

// Function to create a SHA-256 hash of the code verifier and then base64-url encode it
async function generateCodeChallenge(codeVerifier) {
    const encoder = new TextEncoder();
    const data = encoder.encode(codeVerifier);
    const hash = await crypto.subtle.digest('SHA-256', data);
    const base64String = btoa(String.fromCharCode(...new Uint8Array(hash)));
    const codeChallenge = base64String.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    return codeChallenge;
}