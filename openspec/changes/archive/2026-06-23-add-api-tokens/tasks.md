## 1. Data model & migration

- [x] 1.1 Create `ApiTokenEntity extends PanacheEntity` in `entity/db/` with fields: `name`, `tokenHash` (unique), `principal`, `role` (string), `createdAt`, `lastUsedAt`
- [x] 1.2 Add a `TokenRole` enum (`USER`, `ADMIN`) and helper to map it to the app's role strings
- [x] 1.3 Add Flyway migration `src/main/resources/db/migration/V1.1.0__add_api_tokens.sql` creating `apitokenentity` table + sequence + unique index on `tokenhash`, following `V1.0.0__init.sql` conventions
- [x] 1.4 Add dev/test seed rows for tokens in `src/main/resources/dev-db/migration/R__test_data.sql` if helpful for manual testing

## 2. Token generation & hashing

- [x] 2.1 Add a `TokenSecrets` utility: generate 256-bit `SecureRandom` secret, URL-safe Base64 encode with `mvk_` prefix, and compute `SHA-256` hex hash
- [x] 2.2 Unit-test secret generation (uniqueness, prefix) and hashing (stable, hex)

## 3. Token lifecycle (actions + REST)

- [x] 3.1 Add `CreateTokenAction` (under `actions/`): validate name, generate secret, persist entity with principal + snapshotted role, write audit log, return plaintext once
- [x] 3.2 Add `ListTokensAction`: return only tokens for the current principal (metadata only, never hash/secret)
- [x] 3.3 Add `RevokeTokenAction`: delete by id only if owned by current principal (else 404), write audit log
- [x] 3.4 Add response/request DTOs (`CreateTokenRequest`, `TokenView`, `TokenSecretView`) — `TokenView` excludes secret and hash
- [x] 3.5 Add `ApiTokenResource` at `/api/v2/token` wiring POST/GET/DELETE to the actions; derive principal from `SecurityIdentity`
- [x] 3.6 Add new audit messages/types for token create and revoke

## 4. Bearer authentication mechanism

- [x] 4.1 Add a custom `TokenAuthenticationRequest implements AuthenticationRequest` carrying the presented secret
- [x] 4.2 Implement `@ApplicationScoped TokenAuthenticationMechanism implements HttpAuthenticationMechanism`: read `Authorization: Bearer <secret>`, return `Uni`-null when absent; otherwise wrap the secret in `TokenAuthenticationRequest` and delegate to `IdentityProviderManager.authenticate(...)`. Also implement `getCredentialTypes()` (returns `TokenAuthenticationRequest`) and `getChallenge()`
- [x] 4.3 Implement `@ApplicationScoped IdentityProvider<TokenAuthenticationRequest>`: hash the presented secret and look up `ApiTokenEntity` inside `context.runBlocking(...)` (with `@ActivateRequestContext` if needed); on no match fail (401)
- [x] 4.4 In the provider, build a `QuarkusSecurityIdentity` with the owner principal and the role string mapped from the token's snapshotted role
- [x] 4.5 Update `lastUsedAt` best-effort/throttled on successful authentication
- [x] 4.6 Verify coexistence: opaque Bearer handled by this mechanism, OIDC interactive flow and JWTs unaffected

## 5. UI

- [x] 5.1 Add `AccountTokenResource` at `/ui/tokens` (authenticated users) with `@CheckedTemplate` methods
- [x] 5.2 Add Qute templates: token list + create form (htmx), showing the plaintext secret once after creation with a copy affordance
- [x] 5.3 Add a navbar entry in `base.html` linking to `/ui/tokens`

## 6. Tests

- [x] 6.1 `ApiTokenResourceTest` (@QuarkusTest, RestAssured): create returns secret once; list shows only own tokens; delete revokes; cannot delete others' tokens
- [x] 6.2 Token auth integration test: valid token authenticates; USER token gets 403 on admin endpoint; ADMIN token succeeds; revoked/unknown token gets 401
- [x] 6.3 Confirm OIDC path still passes existing tests unchanged

## 7. Docs

- [x] 7.1 Update `README.md` with API token usage and the `/ui/tokens` page
- [x] 7.2 Document the `Authorization: Bearer` flow and the role-snapshot behavior
