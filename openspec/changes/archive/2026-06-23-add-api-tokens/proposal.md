## Why

Today the only way to authenticate against the Movie Archive is an interactive OIDC browser flow. Non-interactive clients (scripts, automations, and the upcoming MCP server) have no way to authenticate. We need long-lived, user-owned API tokens that carry the owner's permission level so machine clients can call the API as a specific user without sharing OIDC credentials.

## What Changes

- Introduce a personal **API token** that a logged-in user can create and revoke.
- Each token is bound to the creating user (principal) and **snapshots their role** (`USER` or `ADMIN`) at creation time.
- Token secrets are shown **once** at creation and stored only as a hash; they cannot be retrieved again.
- Add a Bearer authentication path: requests presenting a valid `Authorization: Bearer <token>` are authenticated as the token's owner with the token's role, alongside the existing OIDC flow.
- Add REST endpoints under `/api/v2/token` to create, list (own tokens), and delete tokens.
- Add a Qute UI page under `/ui` for managing one's own tokens.
- Record token creation and revocation in the existing audit log.

## Capabilities

### New Capabilities
- `api-tokens`: Lifecycle (create, list, revoke) of user-owned API tokens, the secret-hashing rules, and the role snapshot.
- `token-authentication`: Authenticating API requests via `Authorization: Bearer <token>`, mapping a token to a `SecurityIdentity` with the snapshotted role, and how token auth coexists with OIDC.

### Modified Capabilities
<!-- No existing specs in openspec/specs/; behavior of existing endpoints is unchanged. -->

## Impact

- **New dependency**: a password/secret hashing primitive for token hashes (e.g. SHA-256 via JDK, or Quarkus Elytron) — no new external service.
- **New entity & migration**: `ApiTokenEntity` plus a new Flyway migration (next after `V1.0.0__init.sql`).
- **Security config**: a custom `HttpAuthenticationMechanism` / augmentor so Bearer tokens produce a `SecurityIdentity`; existing `quarkus.http.auth.permission` rules (admin = POST/PUT/DELETE) continue to apply unchanged.
- **New REST resource**: `ApiTokenResource` at `/api/v2/token`.
- **New UI**: token management page + Qute templates and navbar entry.
- **Audit log**: new `AuditLogType` values (or messages) for token create/revoke.
- **Enables** the separate `add-mcp-server` change, which depends on token-based auth.
