## Quarkus RestAPI

This RestAPI handles all movie data.  
You can _add_, _remove_, _update_ and _search_ movies.

### Requirements

To start the backend you need docker or podman installed.

### Setup

Run `docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:22 start-dev`
to start Keycloak.  
To set up the Keycloak realm, check out
the [Quarkus documentation](https://quarkus.io/guides/security-keycloak-authorization#starting-and-configuring-the-keycloak-server)!

### Live coding with Quarkus

Run `./mvnw quarkus:dev` to start Quarkus in dev mode.   
Quarkus will set up the Postgres Database and import some demo data.

### Swagger UI

Pls try the [Swagger UI](http://localhost:8080/q/swagger-ui/) to get started with this API.

### Building Quarkus with GraalVM

Run `./mvnw package -Pnative` to build the native application. This can take a few minutes!  
Run `docker build -f src/main/docker/Dockerfile.native-micro -t movie-api` to build the docker image.