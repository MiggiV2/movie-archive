## Context

Movie Archive is a Quarkus 3 (Java 21) app using OIDC (Keycloak/`sso.mymiggi.de`) for authentication. Authorization is enforced declaratively in `application.properties` via `quarkus.http.auth.permission.admin_v2` (POST/PUT/DELETE require the admin role) and read at runtime through `SecurityIdentity` / `UserInfo` (see `UiSession`, admin actions). There is **no User entity** — identity is ephemeral, derived from the OIDC token; the principal name is the user's email and the admin role is `movie_admins@sso.mymiggi.de` (configurable via `DE_MYMIGGI_ADMIN_ROLE`).

This change adds non-interactive Bearer authentication so scripts and the upcoming MCP server can call the API as a specific user. Tokens must carry the owner's permission level so existing admin-only rules keep working unchanged.

## Goals / Non-Goals

**Goals:**
- User-owned API tokens: create, list (own), revoke — via REST and a `/ui` page.
- Bearer auth that yields a `SecurityIdentity` whose roles match the token's snapshotted role, so existing `quarkus.http.auth.permission` rules apply unchanged.
- Store only a hash of the secret; show plaintext once.
- Coexist with OIDC without disabling it.

**Non-Goals:**
- The MCP server itself (separate `add-mcp-server` change).
- Per-token fine-grained scopes/permissions beyond the USER/ADMIN role snapshot.
- Token expiry/rotation policies (tokens live until revoked) — can be a later change.
- A general User entity / user management.

## Decisions

### Decision: Custom `HttpAuthenticationMechanism` + custom `IdentityProvider` for Bearer tokens
Split responsibilities the idiomatic Quarkus way (confirmed against current Quarkus security docs):
- A CDI `@ApplicationScoped HttpAuthenticationMechanism` inspects `Authorization: Bearer <secret>`. When absent it returns `Uni`-null ("not attempted") so OIDC handles interactive flows. When present it wraps the secret in a custom `TokenAuthenticationRequest` and delegates to `IdentityProviderManager.authenticate(request)` — it does **not** touch the database itself.
- A custom `@ApplicationScoped IdentityProvider<TokenAuthenticationRequest>` performs the (blocking) Panache lookup: hash the presented secret, find the `ApiTokenEntity`, and build a `QuarkusSecurityIdentity` with the owner principal and the snapshotted role mapped to the app's role string (admin → `DE_MYMIGGI_ADMIN_ROLE`; user → the standard group role).

- **Why this split:** `HttpAuthenticationMechanism.authenticate(...)` runs on the I/O thread and returns a `Uni`; doing a blocking Panache query there would block the event loop. The `IdentityProvider` is the documented place for blocking credential verification — run the DB work via `AuthenticationRequestContext.runBlocking(...)` (and `@ActivateRequestContext` if the ORM request context is needed). The mechanism/provider pair also integrates cleanly with the existing `quarkus.http.auth.permission` policies — no endpoint code changes, admin rules just work. A JAX-RS `ContainerRequestFilter` was rejected because it runs after Quarkus security and would force us to re-implement role enforcement.
- **Coexistence:** OIDC's own bearer handling targets JWTs; our opaque tokens are routed by our mechanism. Both mechanisms stay active; ours engages only on a `Bearer` header it recognizes.

### Decision: Map token role to existing role strings
The token stores an enum (`USER` | `ADMIN`). At authentication the mechanism grants the configured admin role string for `ADMIN`, and the standard user/group role string for `USER`. This reuses the existing `admin-policy` (`roles-allowed=${DE_MYMIGGI_ADMIN_ROLE}`) verbatim.

- **Why:** Authorization stays single-sourced in `application.properties`; no parallel role logic.

### Decision: Hashing with SHA-256 + random secret, no per-token salt
Generate 256 bits of randomness (`SecureRandom`), encode as URL-safe Base64 with a recognizable prefix (e.g. `mvk_`). Store `SHA-256(secret)` hex. Lookups are by exact hash match (indexed column), so a unique deterministic hash is required.

- **Why over bcrypt/Argon2:** The secret is high-entropy (256-bit random), not a human password, so slow salted hashing buys little and would prevent O(1) lookup by hash. SHA-256 of a 256-bit random value is not brute-forceable. (Recorded as a trade-off below.)

### Decision: Entity & migration
New `ApiTokenEntity extends PanacheEntity` with fields: `name`, `tokenHash` (unique, indexed), `principal`, `role` (string of enum), `createdAt`, `lastUsedAt`. New Flyway migration `V1.1.0__add_api_tokens.sql` creating `apitokenentity` + sequence, following the `V1.0.0__init.sql` conventions. `lastUsedAt` updated asynchronously/best-effort on successful auth.

### Decision: REST + UI shape
- `ApiTokenResource` at `/api/v2/token`: `POST` (create, returns secret once), `GET` (list own), `DELETE /{id}` (revoke). Ownership derived from `SecurityIdentity` principal; users only ever see/act on their own tokens.
- UI: `AccountTokenResource` (or similar) at `/ui/tokens` with Qute templates (list + create form, htmx), plus a navbar entry. Available to any authenticated user (admin role snapshot just affects what the token can later do).
- Token create/revoke reuse the existing audit log (`SaveAuditLogAction`) with new action messages.

## Risks / Trade-offs

- **Unsalted SHA-256 hashes** → Acceptable because secrets are 256-bit random (not guessable); column is treated as sensitive, never returned. Mitigation: prefix-tagged secrets, hashes never exposed via API/UI.
- **Stale role snapshot** (user later promoted/demoted but old token keeps old role) → Documented behavior; mitigation is revoke + recreate. A future change could re-resolve role at auth time.
- **DB write on every authenticated request** for `lastUsedAt` → Mitigation: update best-effort/throttled (e.g. skip if updated within last minute) to avoid write amplification.
- **Two auth mechanisms active** → Risk of ordering/conflict with OIDC. Mitigation: our mechanism only engages for `Bearer` and only when the secret is not a valid OIDC JWT; integration test both paths.
- **Blocking DB lookup on the I/O thread** → Mitigation: do the hash+lookup inside the `IdentityProvider` via `AuthenticationRequestContext.runBlocking(...)`, never inline in the mechanism.

## Migration Plan

1. Add dependency (if needed) and `ApiTokenEntity` + `V1.1.0__add_api_tokens.sql`; `quarkus.flyway.migrate-at-start=true` applies it on deploy.
2. Ship the custom `HttpAuthenticationMechanism`, resource, and UI behind the existing security config (no permission rule changes required).
3. In `%dev`/`%test`, auth is already permissive — add tests with explicit token rows to exercise the mechanism.
4. **Rollback:** the mechanism is additive and only triggers on Bearer headers; reverting the code disables token auth while OIDC continues. The table can remain (harmless) or be dropped by a follow-up migration.

## Open Questions

- Secret prefix string and exact length — cosmetic; pick at implementation (`mvk_` proposed).
- Whether to throttle `lastUsedAt` writes now or defer — leaning toward simple throttle in v1.
