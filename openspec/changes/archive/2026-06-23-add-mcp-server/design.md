## Context

The Movie Archive (Quarkus 3, Java 21) has a clean `actions` layer: read actions (`SearchAction`, `GetMovieByIDAction`, `GetMoviesAction`) and admin actions (`AddMovieAction`, `UpdateMovieAction`) that already encapsulate validation, IMDB metadata enrichment, and audit logging. Authorization is enforced via `SecurityIdentity` roles and the `quarkus.http.auth.permission` config. The `add-api-tokens` change adds a Bearer `HttpAuthenticationMechanism` that produces a `SecurityIdentity` carrying the token owner's snapshotted role.

This change exposes the archive as an MCP server so AI assistants can call these actions as tools, authenticated by an API token.

## Goals / Non-Goals

**Goals:**
- An MCP server endpoint over streamable HTTP, served beside the existing REST API/UI.
- Read tools for any valid token; admin-only write tools.
- Tools delegate to existing `actions`/`services` so behavior matches the REST API (search semantics, metadata enrichment, audit logging).
- Authentication and authorization reuse the API-token Bearer mechanism and role model — no parallel auth.

**Non-Goals:**
- Defining the token mechanism itself (owned by `add-api-tokens`).
- Conversational/LLM logic — recommendation and counting are done by the client using tool results.
- Streaming partial results, MCP resources/prompts (only tools in v1).
- New search algorithms — reuse `SearchAction` as-is.

## Decisions

### Decision: Use `quarkus-mcp-server-http`
Adopt the Quarkiverse MCP server extension (artifact `io.quarkiverse.mcp:quarkus-mcp-server-http`, which provides both Streamable HTTP and the older HTTP/SSE transports) and declare tools with `@Tool`-annotated methods. Optionally add `quarkus-mcp-server-hibernate-validator` to validate tool arguments.

- **Why over a hand-rolled JSON-RPC endpoint:** the extension handles the MCP handshake, tool discovery/JSON-schema generation, and transport, and integrates with Quarkus CDI/security. Hand-rolling would re-implement the spec with high maintenance cost. Streamable HTTP (over stdio) suits a long-running server reachable by remote clients.

### Decision: Thin `@Tool` layer delegating to existing actions
Create an `mcp/MovieTools` CDI bean whose `@Tool` methods call the existing actions:
- `search_movies` → `SearchAction`
- `list_genres` / `find_movies_by_genre` → `MovieMetaData.genres` (the IMDb genres shown in the UI). The legacy `TagEntity`/`TagResource` system is **not** used by the current Qute UI (only the dropped Vue SPA consumed it), so genre discovery targets the metadata genres instead.
- `get_movie` → `GetMovieByIDAction`
- `create_movie` / `update_movie` → `AddMovieAction` / `UpdateMovieAction`

- **Why:** single source of truth for behavior (search fallback logic, uuid generation, metadata enrichment, audit logging) and no duplicated validation. Tool DTOs reuse `DetailedMovie`/`MoviePreview` shapes.

### Decision: Authorize tools with Quarkus security annotations
The MCP server extension supports Quarkus security annotations directly on feature methods/classes (confirmed against the extension's security reference). Annotate the tools bean `@Authenticated` and gate write tools with `@RolesAllowed(<admin-role>)`. Additionally lock the MCP transport path via config:

```properties
quarkus.http.auth.permission.mcp-endpoints.paths=/mcp/*
quarkus.http.auth.permission.mcp-endpoints.policy=authenticated
```

- **Why:** keeps one authorization model — the same role strings as the REST API and the same `add-api-tokens` Bearer identity. Note: when auth/authorization fails on a tool call, the extension returns an **MCP error (code `-32001`)**, not an HTTP status — tests must assert on the MCP error, not a 401/403 HTTP code.

### Decision: Acting user for audit logging
Write tools must record the token owner in the audit log. Admin actions currently inject OIDC `UserInfo`. Since MCP requests authenticate via token (no `UserInfo`), pass the principal from `SecurityIdentity` into the audit-log call. May require a small overload of `SaveAuditLogAction`/the admin actions that accepts a principal/username instead of `UserInfo`.

- **Why:** audit integrity — token-driven changes must be attributable to the owner.

### Decision: Configuration & enablement
Configure the MCP server path and enablement in `application.properties` per profile. Keep it enabled in all profiles; in `%dev`/`%test` the permissive auth applies, so add tests that exercise tool authorization with explicit token identities.

## Risks / Trade-offs

- **Auth-failure surfacing** → The extension reports auth/authz failures as MCP error `-32001`, not HTTP status. Mitigation: write tests against the MCP error; document the behavior for clients.
- **Audit logging without `UserInfo`** → Mitigation: refactor admin actions to accept a username/principal source so both OIDC and token callers log correctly.
- **Write tools mutating data via AI** → Mitigation: gated to ADMIN tokens only; reuse existing validation; all writes audit-logged and reversible via existing admin UI.
- **Tool output size** (search returning many movies, e.g. franchise counts) → Mitigation: reuse `MoviePreview` (lightweight) for list results and cap/paginate large result sets in the tool.
- **Version coupling** with Quarkus platform BOM → Mitigation: align the MCP extension version with the managed Quarkus version; add to `dependencyManagement` if needed.

## Migration Plan

1. Ship `add-api-tokens` first (hard dependency).
2. Add the MCP extension dependency and `mcp/MovieTools`; wire tools to existing actions.
3. Confirm Bearer identity propagation; add role enforcement on write tools and audit-log principal plumbing.
4. Add config; add integration tests for tool discovery, read tools, and admin-only write tools (USER vs ADMIN token).
5. **Rollback:** the MCP endpoint is additive; removing the extension/bean disables MCP while REST/UI/tokens continue to work.

## Open Questions

- Exact MCP path (extension default vs a dedicated `/mcp` root) — the `http.auth.permission` path must match whatever is configured.
- Result-size cap/pagination strategy for `search_movies` on large franchises.

_Resolved (via Quarkus MCP docs):_ `@Authenticated`/`@RolesAllowed` are supported directly on `@Tool` methods; no in-method role check is required. Auth failures surface as MCP error `-32001`.
