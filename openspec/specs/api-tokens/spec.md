# api-tokens

## Purpose

Lifecycle (create, list, revoke) of user-owned API tokens, the secret-hashing rules, and the role snapshot. Lets a logged-in user mint long-lived credentials for non-interactive clients without sharing their OIDC identity.

## Requirements

### Requirement: User can create an API token

An authenticated user SHALL be able to create a personal API token by providing a human-readable name. The system SHALL generate a cryptographically random secret, store only a hash of it, and bind the token to the creating user's principal and current role.

#### Scenario: Successful token creation

- **WHEN** an authenticated user POSTs a token name to `/api/v2/token`
- **THEN** the system generates a random secret, persists an `ApiTokenEntity` with the token name, a hash of the secret, the user's principal, the user's role (`USER` or `ADMIN`), and a creation timestamp
- **AND** returns the plaintext secret exactly once in the response together with the token's id, name, role, and creation time

#### Scenario: Secret is never retrievable after creation

- **WHEN** a user lists or fetches their tokens after creation
- **THEN** the response contains the token metadata (id, name, role, createdAt, lastUsedAt) but never the plaintext secret or its hash

#### Scenario: Name is required

- **WHEN** a user POSTs to `/api/v2/token` without a non-blank name
- **THEN** the system responds with HTTP 400 and does not create a token

#### Scenario: Role is snapshotted at creation

- **WHEN** an admin user creates a token
- **THEN** the stored token has role `ADMIN`
- **AND** when a non-admin user creates a token the stored token has role `USER`

### Requirement: User can list their own API tokens

An authenticated user SHALL be able to list the API tokens they own, and SHALL NOT see tokens owned by other users.

#### Scenario: List own tokens

- **WHEN** an authenticated user GETs `/api/v2/token`
- **THEN** the system returns only tokens whose principal matches the requesting user, each with id, name, role, createdAt, and lastUsedAt

#### Scenario: Tokens are isolated per user

- **WHEN** user A lists their tokens
- **THEN** tokens created by user B are not included in the response

### Requirement: User can revoke an API token

An authenticated user SHALL be able to delete (revoke) one of their own tokens by id, after which that token can no longer authenticate any request.

#### Scenario: Successful revocation

- **WHEN** an authenticated user DELETEs `/api/v2/token/{id}` for a token they own
- **THEN** the system removes the token and returns a success status
- **AND** subsequent requests using that token's secret are rejected as unauthenticated

#### Scenario: Cannot revoke another user's token

- **WHEN** a user attempts to delete a token id they do not own
- **THEN** the system responds with HTTP 404 (or 403) and leaves the token intact

### Requirement: Token lifecycle is audited

The system SHALL record token creation and revocation in the audit log.

#### Scenario: Creation and revocation are logged

- **WHEN** a token is created or revoked
- **THEN** an audit log entry is written capturing the acting user, the action, and the token name
