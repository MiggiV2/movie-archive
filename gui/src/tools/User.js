import { runRefreshTokenFlow } from "@/tools/Auth";
import { getCookie } from "@/tools/Cookies";

export function isAdmin() {
    return localStorage.getItem("is_admin") == "true";
}

export function getUserName() {
    return localStorage.getItem("name");
}

export function getUsersSimpleName() {
    return localStorage.getItem("name")
}

export function getPreferredUsername() {
    return localStorage.getItem("preferred_username");
}

export function getUserEMail() {
    return localStorage.getItem("email");
}

export function isUserMailVerified() {
    return localStorage.getItem("email_verified");
}

export async function initUserData() {
    if (localStorage.getItem("name") == null) {
        if (!getCookie("accessToken")) {
            if (getCookie("refreshToken")) {
                runRefreshTokenFlow();
            } else {
                return {};
            }
        }
        const user = await getUserInfo();
        localStorage.setItem("name", user.name);
        localStorage.setItem("preferred_username", user.preferred_username);
        localStorage.setItem("email", user.email);
        localStorage.setItem("email_verified", user.email_verified);
        const adminGroup = process.env.VUE_APP_AUTH_ADMIN;
        localStorage.setItem("is_admin", user.groups.includes(adminGroup));
    }
}


async function getUserInfo() {
    const clientId = process.env.VUE_APP_AUTH_CLIENT_ID;
    const userInfoEndpoint = `https://sso.mymiggi.de/oauth2/openid/${clientId}/userinfo`; // User info endpoint

    try {
        const response = await fetch(userInfoEndpoint, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getCookie("accessToken")}`, // Include the access token in the Authorization header
                'Content-Type': 'text/plain', // Set the content type
            },
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status} ${response.statusText}`);
        }

        const userInfo = await response.json();
        return userInfo; // This will contain the user information
    } catch (error) {
        console.error('Error fetching user info:', error);
        throw error; // Rethrow the error for further handling if needed
    }
}