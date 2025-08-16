import { UserManager } from 'oidc-client-ts';

/**
 * Configuration object accepted by createAuthManager.
 * @typedef {Object} AuthConfig
 * @property {string} [authority]
 * @property {string} [client_id]
 * @property {string} [redirect_uri]
 * @property {string} [response_type]
 * @property {string} [scope]
 * @property {any} [additional] - any additional options forwarded to UserManager
 */

/**
 * Singleton UserManager instance.
 * @type {UserManager|null}
 */
let mgr = null;

/**
 * Return the singleton UserManager, creating it lazily.
 *
 * If `mgr` is not yet created this will initialise it using the provided
 * `authority` and `client_id` parameters (if present) or fall back to
 * values from `localStorage`.
 *
 * Note: once the singleton has been created, calling this function again
 * with different `authority`/`client_id` values will NOT replace the
 * existing manager; use `createAuthManager` to explicitly recreate it.
 *
 * @param {string} [authority] Optional authority URL to initialise the manager.
 * @param {string} [client_id] Optional client id to initialise the manager.
 * @returns {UserManager} The singleton UserManager instance.
 */
export function getAuthManager(authority, client_id) {
    if(!localStorage.getItem("authServerUrl")) {
        return undefined;
    }
    if (!mgr) {
        mgr = new UserManager({
            authority: authority || localStorage.getItem("authServerUrl"),
            client_id: client_id || localStorage.getItem("authClientId"),
            redirect_uri: window.location.origin + "/auth",
            response_type: "code",
            scope: "openid profile email groups",
        });
    }
    return mgr;
}

/**
 * Begin or ensure a signed-in session.
 *
 * This helper will lazily initialise the singleton UserManager (via
 * `getAuthManager`) if required, then check whether a user is currently
 * available. If no user is present it triggers an interactive redirect
 * sign-in. It also registers an access-token-expiring handler that attempts
 * to refresh the token silently.
 *
 * Important notes:
 * - If called while the app is on the `/auth` callback path this function
 *   no-ops to avoid re-initialising the manager during the post-auth flow.
 * - Calling this with different `authority`/`client_id` after the manager
 *   has been created will not replace it; use `createAuthManager` if you
 *   need to force a new instance.
 *
 * @param {string} [authority] Optional authority URL used to initialise the manager.
 * @param {string} [client_id] Optional client id used to initialise the manager.
 * @returns {void}
 */
export function login(authority, client_id) {
    const mgr = getAuthManager(
        authority,
        client_id
    );

    mgr.getUser().then((userData) => {
        if (!userData) {
            mgr.signinRedirect().then(user => {
                console.log("User signed in:", user.profile.name);
            }).catch(err => {
                console.error("Error signing in:", err);
            });
        }
    });

    mgr.events.addAccessTokenExpiring(() => {
        console.log("Access token is expiring, refreshing...");
        mgr.signinSilent().catch(err => {
            console.error("Error during silent sign-in:", err);
        });
    });

}

/**
 * Export the current manager instance (may be null until initialised).
 * @type {UserManager|null}
 */
export { mgr };
