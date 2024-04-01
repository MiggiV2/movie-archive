# movie-archive

A simple archive to save movie information.

![start_page](./assets/Screenshot%202023-07-19%20at%2011-05-25%20MovieArchive%20Home.png)

### This repository contains:
- Quarkus RestAPI
- Vue.JS frontend

This project does not store any video files! Only the information about movies you have at home, for example.

## Vue.JS frontend:
Search:
![search_page](./assets/Screenshot%202024-04-01%20at%2008-06-03%20MovieArchive%20Suche.avif)

Movie Information:
![movie_information](./assets/Screenshot%202024-04-01%20at%2008-06-17%20MovieArchive%20Suche.avif)

Admin Log:
![admin_log](./assets/Screenshot%202024-04-01%20at%2008-06-32%20MovieArchive%20Auditlog.avif)

## Quarkus backend:
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