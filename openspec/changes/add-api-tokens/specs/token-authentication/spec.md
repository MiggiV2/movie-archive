## ADDED Requirements

### Requirement: Bearer token authentication

The system SHALL authenticate API requests that present a valid `Authorization: Bearer <token>` header by matching the presented secret against stored token hashes and establishing a `SecurityIdentity` for the token's owner.

#### Scenario: Valid token authenticates request

- **WHEN** a request to a protected `/api/v2` endpoint includes `Authorization: Bearer <valid-secret>`
- **THEN** the system identifies the token by its hash, establishes a `SecurityIdentity` whose principal is the token's owner and whose roles reflect the token's snapshotted role
- **AND** the request proceeds as if that user were authenticated

#### Scenario: Invalid or unknown token is rejected

- **WHEN** a request presents a Bearer secret that matches no stored token hash
- **THEN** the system rejects the request as unauthenticated (HTTP 401)

#### Scenario: Revoked token is rejected

- **WHEN** a request presents a Bearer secret whose token has been deleted
- **THEN** the system rejects the request as unauthenticated (HTTP 401)

### Requirement: Token role governs authorization

A Bearer-authenticated request SHALL be authorized using the token's snapshotted role, so that the existing admin-only restrictions on mutating endpoints apply to tokens exactly as they do to OIDC users.

#### Scenario: USER token cannot perform admin operations

- **WHEN** a request authenticated by a `USER`-role token calls an admin-only endpoint (e.g. POST/PUT/DELETE `/api/v2/movie`)
- **THEN** the system responds with HTTP 403 Forbidden

#### Scenario: ADMIN token can perform admin operations

- **WHEN** a request authenticated by an `ADMIN`-role token calls an admin-only endpoint
- **THEN** the request is authorized and proceeds

### Requirement: Token authentication coexists with OIDC

Bearer token authentication SHALL operate alongside the existing OIDC browser flow without disabling it.

#### Scenario: OIDC requests are unaffected

- **WHEN** a request authenticates via the existing OIDC session/flow and presents no Bearer token
- **THEN** the request is handled exactly as before this change

#### Scenario: Last-used timestamp is updated

- **WHEN** a token successfully authenticates a request
- **THEN** the system records the token's most recent use time (lastUsedAt)
