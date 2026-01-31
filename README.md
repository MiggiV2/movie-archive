# Movie Archive

Movie Archive is a full-stack web app to manage a personal Blu-ray collection. It stores metadata only (no video files) and offers search, tagging, exports, and an audit log.

![start_page](./assets/Screenshot%202023-07-19%20at%2011-05-25%20MovieArchive%20Home.png)

## Features
- Add, update, and search movies with metadata, ratings, and tags
- Tag-based browsing and detailed movie view
- CSV export of the collection
- Admin audit log for changes
- OIDC authentication (Keycloak-compatible)
- REST API with OpenAPI/Swagger

## Tech stack
- Backend: Quarkus 3 (Java 21), REST, Flyway, PostgreSQL
- Frontend: Vue 3, Vue Router, Bootstrap
- Auth: OIDC (Keycloak)
- Containers: Docker (Quarkus Dockerfiles, Nginx for UI)

## Repository layout
- `api/` - Quarkus REST API (root path `/api/v2`)
- `gui/` - Vue single-page app

## Development setup

### Backend (Quarkus API)
Requirements: Java 21 and Docker/Podman.

```
cd api
./mvnw quarkus:dev
```

Quarkus Dev Services will start PostgreSQL and load demo data via Flyway.
API base URL: http://localhost:8080/api/v2/

Swagger UI: http://localhost:8080/q/swagger-ui/
OpenAPI: http://localhost:8080/api/v2/openapi

### Frontend (Vue)
Requirements: Node.js 22+ and Yarn.

```
cd gui
yarn install
yarn serve
```

The dev server runs at https://localhost:1024 (self-signed cert).
In dev, the frontend calls the API at http://localhost:8080/api/v2/.
If you change the dev port, update `quarkus.http.cors.origins` accordingly.

### Authentication (OIDC / Keycloak)
The frontend fetches auth config from `GET /api/v2/service/config` and uses
`/auth` as the OIDC redirect URI.

In development, the API is permissive (`%dev` profile), so you can run without
an IdP. To test authentication flows, run a Keycloak dev server and point
Quarkus to it:

```
docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:22 start-dev
```

Then set OIDC config (for example via env vars) to your realm issuer URL and client:
`QUARKUS_OIDC_AUTH_SERVER_URL`, `QUARKUS_OIDC_CLIENT_ID`, `QUARKUS_OIDC_CREDENTIALS_SECRET`.
For production, the app reads `CLIENT_ID` and `CLIENT_SECRET`
(see `api/src/main/resources/application.properties`).

## Containers (production)

### Backend
```
cd api
./mvnw package -Dquarkus.container-image.push=false
docker build -f src/main/docker/Dockerfile.jvm -t movie-api .
```

### Frontend
```
docker build -t movie-gui ./gui
```

The frontend image serves static files via Nginx; deploy it alongside the API so
`/api/v2` is reachable on the same host.

## Screenshots
Search:
![search_page](./assets/Screenshot%202025-07-31%20at%2011-50-05%20MovieArchive%20Suche.avif)

Movie Information:
![movie_information](./assets/Screenshot%202025-07-31%20at%2011-55-31%20MovieArchive%20Suche.avif)

Admin Log:
![admin_log](./assets/Screenshot%202024-04-01%20at%2008-06-32%20MovieArchive%20Auditlog.avif)
