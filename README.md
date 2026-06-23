# Movie Archive

Movie Archive is a web app to manage a personal Blu-ray collection. It stores metadata only (no video files) and offers search, tagging, exports, and an audit log.

![home](./assets/ui-home.png)

## Features
- Add, update, and search movies with metadata, ratings, and tags
- Tag-based browsing and detailed movie view
- Server-side UI at `/ui` (Qute templates + htmx, dark theme)
- CSV export of the collection
- Admin audit log for changes
- OIDC authentication (Keycloak-compatible)
- REST API with OpenAPI/Swagger
- Personal API tokens for non-interactive clients (scripts, automation, MCP)

## Tech stack
- Quarkus 3 (Java 21), REST, Flyway, PostgreSQL
- Qute templates + htmx + Bootstrap for the UI
- OIDC (Keycloak)
- Docker (Quarkus Dockerfiles)

## Development setup

Requirements: Java 21 and Docker/Podman.

```
./mvnw quarkus:dev
```

Quarkus Dev Services will start PostgreSQL and load demo data via Flyway.

- UI: http://localhost:8080/ui
- API base URL: http://localhost:8080/api/v2/
- Swagger UI: http://localhost:8080/q/swagger-ui/
- OpenAPI: http://localhost:8080/api/v2/openapi

### Authentication (OIDC / Keycloak)

In development, the API is permissive (`%dev` profile), so you can run without
an IdP. To test authentication flows, run a Keycloak dev server and point
Quarkus to it:

```
docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:22 start-dev
```

Then set OIDC config (for example via env vars) to your realm issuer URL and client:
`QUARKUS_OIDC_AUTH_SERVER_URL`, `QUARKUS_OIDC_CLIENT_ID`, `QUARKUS_OIDC_CREDENTIALS_SECRET`.
For production, the app reads `CLIENT_ID` and `CLIENT_SECRET`
(see `src/main/resources/application.properties`).

In production the IdP enforces PKCE and the app runs behind a TLS-terminating
reverse proxy. The `%prod` profile enables PKCE and proxy-forwarding headers
automatically — no extra configuration is needed beyond `CLIENT_ID`/`CLIENT_SECRET`.

## Containers (production)

```
./mvnw package -Dquarkus.container-image.push=false
docker build -f src/main/docker/Dockerfile.jvm -t movie-api .
```

## API Tokens

Any logged-in user can create long-lived personal tokens for non-interactive clients (scripts, automation, the MCP server) instead of going through the OIDC browser flow.

### Managing tokens

**Web UI**: navigate to `/ui/tokens` ("Tokens" link in the navbar) to create, list, and revoke your own tokens. The plaintext secret is shown exactly once immediately after creation and cannot be retrieved again.

**REST API** (`/api/v2/token`, requires authentication):

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v2/token` | Create a token — returns `{id, name, role, createdAt, secret}`. The `secret` field is only present in this response. |
| `GET` | `/api/v2/token` | List your own tokens — returns metadata only (`id, name, role, createdAt, lastUsedAt`), never the secret. |
| `DELETE` | `/api/v2/token/{id}` | Revoke one of your own tokens (404 if not yours). |

Create a token:

```bash
curl -s -X POST https://<host>/api/v2/token \
  -H "Content-Type: application/json" \
  -d '{"name": "my-script"}' \
  # authenticate with your session or OIDC token here
```

### Using a token

Send the secret in the `Authorization` header on any `/api/v2/*` request:

```bash
curl -H "Authorization: Bearer mvk_xxx" "https://<host>/api/v2/movie?page=0"
```

The request is authenticated as the token's owner.

### Role snapshot

Each token captures the creating user's role (`USER` or `ADMIN`) at creation time. An ADMIN token can perform admin-only operations (POST/PUT/DELETE on protected endpoints); a USER token gets a 403 on those. The snapshot is fixed — if the user's role changes later, existing tokens are unaffected. Revoke and recreate to pick up a new role.

### Security notes

- Only a SHA-256 hash of the secret is stored; the plaintext is never persisted or logged.
- Secrets are 256-bit random values with an `mvk_` prefix.
- Token create and revoke events are recorded in the audit log.
- Token authentication coexists with the existing OIDC browser flow; both can be active simultaneously.

## Screenshots

Browse:
![movies](./assets/ui-movies.png)

Movie detail:
![movie_detail](./assets/ui-movie-detail.png)

Audit log:
![audit](./assets/ui-audit.png)
