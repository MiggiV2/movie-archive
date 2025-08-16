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