## 1. Prerequisite

- [x] 1.1 Confirm `add-api-tokens` is implemented (Bearer `HttpAuthenticationMechanism` produces a `SecurityIdentity` with the token's role)

## 2. Dependency & configuration

- [x] 2.1 Add `io.quarkiverse.mcp:quarkus-mcp-server-http` to `pom.xml` (Streamable HTTP + SSE; align version with the Quarkus platform, add to `dependencyManagement` if required); optionally add `quarkus-mcp-server-hibernate-validator` for tool-arg validation
- [x] 2.2 Configure MCP server path/enablement in `application.properties` per profile
- [x] 2.3 Lock the MCP transport path: `quarkus.http.auth.permission.mcp-endpoints.paths=<mcp-path>/*` with `policy=authenticated`

## 3. Read tools

- [x] 3.1 Create `mcp/MovieTools` CDI bean
- [x] 3.2 `search_movies` tool delegating to `SearchAction`; return lightweight `MoviePreview`-shaped results (id, title, year)
- [x] 3.3 `list_genres` and `find_movies_by_genre` tools over `MovieMetaData.genres` (the IMDb genres shown in the UI; the legacy `TagEntity` is unused by the current UI)
- [x] 3.4 `get_movie` tool delegating to `GetMovieByIDAction`; return not-found result for unknown ids
- [x] 3.5 Add clear `@Tool` descriptions and input schemas so clients can map natural-language queries to tools

## 4. Write tools (admin only)

- [x] 4.1 Refactor admin actions / `SaveAuditLogAction` to accept a principal/username source (not only OIDC `UserInfo`) so token callers are attributed correctly
- [x] 4.2 `create_movie` tool delegating to `AddMovieAction` (uuid generation + metadata enrichment + audit)
- [x] 4.3 `update_movie` tool delegating to `UpdateMovieAction` (diff + audit)
- [x] 4.4 Annotate the tools bean `@Authenticated` and gate write tools with `@RolesAllowed(<admin-role>)`

## 5. Tests

- [x] 5.1 Tool discovery test: client lists tools and sees expected names/schemas
- [x] 5.2 Read-tool tests: `search_movies` by title; `get_movie` (incl. unknown id); `list_genres`/`find_movies_by_genre`
- [x] 5.3 Authorization tests: USER token denied on `create_movie`/`update_movie` (assert MCP error `-32001`); ADMIN token succeeds
- [x] 5.4 Auth tests: missing/invalid/revoked token rejected (MCP error `-32001`)
- [x] 5.5 Verify write tools produce audit log entries attributed to the token owner

## 6. Docs

- [x] 6.1 Add a README section: connecting an MCP client, the streamable-HTTP URL, and authenticating with an API token
- [x] 6.2 Document the available tools and which require an ADMIN token
